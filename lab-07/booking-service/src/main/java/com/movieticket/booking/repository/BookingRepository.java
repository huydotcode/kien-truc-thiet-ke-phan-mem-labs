package com.movieticket.booking.repository;

import com.movieticket.booking.domain.Booking;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface BookingRepository extends JpaRepository<Booking, String> {

    List<Booking> findByUserIdOrderByCreatedAtDesc(String userId);

    @Query("""
            select b from Booking b
            where b.movieId = :movieId and b.showtime = :showtime
              and b.status <> com.movieticket.booking.domain.BookingStatus.FAILED
            """)
    List<Booking> findActiveByMovieAndShowtime(String movieId, java.time.Instant showtime);
}
