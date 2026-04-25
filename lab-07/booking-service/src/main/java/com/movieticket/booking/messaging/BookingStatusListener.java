package com.movieticket.booking.messaging;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.movieticket.booking.config.RabbitMQConfig;
import com.movieticket.booking.event.BookingFailedEvent;
import com.movieticket.booking.event.EventEnvelope;
import com.movieticket.booking.event.PaymentCompletedEvent;
import com.movieticket.booking.service.BookingStatusUpdater;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class BookingStatusListener {

    private final BookingStatusUpdater updater;
    private final ObjectMapper objectMapper;

    @RabbitListener(queues = RabbitMQConfig.QUEUE_PAYMENT_COMPLETED)
    public void onPaymentCompleted(Map<String, Object> message) {
        EventEnvelope<PaymentCompletedEvent> env = decode(message,
                new TypeReference<EventEnvelope<PaymentCompletedEvent>>() {});
        log.info("Received PAYMENT_COMPLETED eventId={} bookingId={}",
                env.eventId(), env.payload().bookingId());
        updater.markConfirmed(env.eventId(), env.payload());
    }

    @RabbitListener(queues = RabbitMQConfig.QUEUE_BOOKING_FAILED)
    public void onBookingFailed(Map<String, Object> message) {
        EventEnvelope<BookingFailedEvent> env = decode(message,
                new TypeReference<EventEnvelope<BookingFailedEvent>>() {});
        log.info("Received BOOKING_FAILED eventId={} bookingId={} reason={}",
                env.eventId(), env.payload().bookingId(), env.payload().reason());
        updater.markFailed(env.eventId(), env.payload());
    }

    private <T> T decode(Map<String, Object> message, TypeReference<T> type) {
        return objectMapper.convertValue(message, type);
    }
}
