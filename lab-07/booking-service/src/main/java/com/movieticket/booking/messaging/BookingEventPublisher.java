package com.movieticket.booking.messaging;

import com.movieticket.booking.config.RabbitMQConfig;
import com.movieticket.booking.event.BookingCreatedEvent;
import com.movieticket.booking.event.EventEnvelope;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class BookingEventPublisher {

    private static final String PRODUCER = "booking-service";

    private final RabbitTemplate rabbitTemplate;

    public void publishBookingCreated(BookingCreatedEvent payload) {
        EventEnvelope<BookingCreatedEvent> env =
                EventEnvelope.of(BookingCreatedEvent.TYPE, PRODUCER, payload);
        rabbitTemplate.convertAndSend(
                RabbitMQConfig.EXCHANGE, BookingCreatedEvent.ROUTING_KEY, env);
        log.info("Published BOOKING_CREATED bookingId={} eventId={}",
                payload.bookingId(), env.eventId());
    }
}
