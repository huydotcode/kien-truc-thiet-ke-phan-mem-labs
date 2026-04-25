package com.movieticket.booking.event;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;

public record BookingCreatedEvent(
        String bookingId,
        String userId,
        String movieId,
        Instant showtime,
        List<String> seats,
        BigDecimal unitPrice,
        BigDecimal amount
) {
    public static final String TYPE = "BOOKING_CREATED";
    public static final String ROUTING_KEY = "booking.created";
}
