package com.movieticket.booking.event;

public record BookingFailedEvent(
        String bookingId,
        String userId,
        String reason
) {
    public static final String TYPE = "BOOKING_FAILED";
    public static final String ROUTING_KEY = "booking.failed";
}
