package com.movieticket.booking;

import static org.assertj.core.api.Assertions.assertThat;
import static org.awaitility.Awaitility.await;

import com.movieticket.booking.config.RabbitMQConfig;
import com.movieticket.booking.domain.BookingStatus;
import com.movieticket.booking.event.BookingFailedEvent;
import com.movieticket.booking.event.EventEnvelope;
import com.movieticket.booking.event.PaymentCompletedEvent;
import com.movieticket.booking.repository.BookingRepository;
import com.movieticket.booking.dto.CreateBookingRequest;
import com.movieticket.booking.service.BookingService;
import java.math.BigDecimal;
import java.time.Duration;
import java.time.Instant;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.containers.RabbitMQContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

/**
 * Smoke integration test cho Booking Service.
 *
 * Yêu cầu: Docker đang chạy. Bỏ qua khi dev local không có Docker bằng cách
 * không chạy class này (nó tự skip nếu Docker không khả dụng — Testcontainers sẽ throw).
 *
 * Kịch bản:
 *   1. Tạo booking → status PENDING
 *   2. Publish PAYMENT_COMPLETED giả lập từ payment-service → booking → CONFIRMED
 *   3. Publish BOOKING_FAILED cho booking khác → FAILED
 */
@Testcontainers
@SpringBootTest(properties = {
        "jwt.secret=test-secret-with-at-least-32-bytes-long-key-here",
        "spring.autoconfigure.exclude="
})
class BookingFlowIT {

    @Container
    static PostgreSQLContainer<?> POSTGRES = new PostgreSQLContainer<>("postgres:16")
            .withDatabaseName("booking_db")
            .withUsername("test")
            .withPassword("test");

    @Container
    static RabbitMQContainer RABBIT = new RabbitMQContainer("rabbitmq:3.13-management");

    @DynamicPropertySource
    static void props(DynamicPropertyRegistry r) {
        r.add("spring.datasource.url", POSTGRES::getJdbcUrl);
        r.add("spring.datasource.username", POSTGRES::getUsername);
        r.add("spring.datasource.password", POSTGRES::getPassword);
        r.add("spring.rabbitmq.host", RABBIT::getHost);
        r.add("spring.rabbitmq.port", RABBIT::getAmqpPort);
        r.add("spring.rabbitmq.username", RABBIT::getAdminUsername);
        r.add("spring.rabbitmq.password", RABBIT::getAdminPassword);
    }

    @Autowired BookingService bookingService;
    @Autowired BookingRepository bookingRepository;
    @Autowired RabbitTemplate rabbitTemplate;

    @Test
    void paymentCompleted_shouldConfirmBooking() {
        var resp = bookingService.create("u-it1", new CreateBookingRequest(
                "m-it", Instant.parse("2026-04-25T19:00:00Z"),
                List.of("A1"), new BigDecimal("100000")));

        rabbitTemplate.convertAndSend(RabbitMQConfig.EXCHANGE, "payment.completed",
                EventEnvelope.of("PAYMENT_COMPLETED", "payment-service",
                        new PaymentCompletedEvent("p-it1", resp.id(), "u-it1",
                                resp.amount(), Instant.now())));

        await().atMost(Duration.ofSeconds(10)).untilAsserted(() -> {
            var b = bookingRepository.findById(resp.id()).orElseThrow();
            assertThat(b.getStatus()).isEqualTo(BookingStatus.CONFIRMED);
        });
    }

    @Test
    void bookingFailed_shouldMarkFailedWithReason() {
        var resp = bookingService.create("u-it2", new CreateBookingRequest(
                "m-it", Instant.parse("2026-04-25T20:00:00Z"),
                List.of("B1"), new BigDecimal("100000")));

        rabbitTemplate.convertAndSend(RabbitMQConfig.EXCHANGE, "booking.failed",
                EventEnvelope.of("BOOKING_FAILED", "payment-service",
                        new BookingFailedEvent(resp.id(), "u-it2", "card_declined")));

        await().atMost(Duration.ofSeconds(10)).untilAsserted(() -> {
            var b = bookingRepository.findById(resp.id()).orElseThrow();
            assertThat(b.getStatus()).isEqualTo(BookingStatus.FAILED);
            assertThat(b.getFailedReason()).isEqualTo("card_declined");
        });
    }
}
