package com.movieticket.booking.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.math.BigDecimal;
import java.time.Instant;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "bookings")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Booking {

    @Id
    @Column(length = 40)
    private String id;

    @Column(name = "user_id", nullable = false, length = 40)
    private String userId;

    @Column(name = "movie_id", nullable = false, length = 40)
    private String movieId;

    @Column(nullable = false)
    private Instant showtime;

    /**
     * Comma-separated seat list (e.g. "A1,A2,A3").
     * Đơn giản hóa cho demo; production nên tách bảng `booking_seats`.
     */
    @Column(nullable = false, length = 500)
    private String seats;

    @Column(name = "unit_price", nullable = false, precision = 12, scale = 2)
    private BigDecimal unitPrice;

    @Column(nullable = false, precision = 12, scale = 2)
    private BigDecimal amount;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private BookingStatus status;

    @Column(name = "failed_reason", length = 200)
    private String failedReason;

    @Column(name = "created_at", nullable = false)
    private Instant createdAt;

    @Column(name = "updated_at", nullable = false)
    private Instant updatedAt;
}
