package com.movieticket.booking.event;

import java.math.BigDecimal;
import java.time.Instant;

public record PaymentCompletedEvent(
        String paymentId,
        String bookingId,
        String userId,
        BigDecimal amount,
        Instant paidAt
) {
    public static final String TYPE = "PAYMENT_COMPLETED";
    public static final String ROUTING_KEY = "payment.completed";
}
