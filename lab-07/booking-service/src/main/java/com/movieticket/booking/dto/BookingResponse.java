package com.movieticket.booking.dto;

import com.movieticket.booking.domain.BookingStatus;
import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;

public record BookingResponse(
        String id,
        String userId,
        String movieId,
        Instant showtime,
        List<String> seats,
        BigDecimal unitPrice,
        BigDecimal amount,
        BookingStatus status,
        String failedReason,
        Instant createdAt,
        Instant updatedAt
) {}
