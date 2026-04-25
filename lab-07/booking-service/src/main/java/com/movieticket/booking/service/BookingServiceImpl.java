package com.movieticket.booking.service;

import com.movieticket.booking.domain.Booking;
import com.movieticket.booking.domain.BookingStatus;
import com.movieticket.booking.domain.ProcessedEvent;
import com.movieticket.booking.dto.BookingResponse;
import com.movieticket.booking.dto.CreateBookingRequest;
import com.movieticket.booking.event.BookingCreatedEvent;
import com.movieticket.booking.event.BookingFailedEvent;
import com.movieticket.booking.event.PaymentCompletedEvent;
import com.movieticket.booking.exception.AppException;
import com.movieticket.booking.messaging.BookingEventPublisher;
import com.movieticket.booking.repository.BookingRepository;
import com.movieticket.booking.repository.ProcessedEventRepository;
import java.math.BigDecimal;
import java.time.Instant;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
public class BookingServiceImpl implements BookingService, BookingStatusUpdater {

    private final BookingRepository bookingRepository;
    private final ProcessedEventRepository processedEventRepository;
    private final BookingEventPublisher publisher;

    @Override
    @Transactional
    public BookingResponse create(String userId, CreateBookingRequest req) {
        Set<String> requested = new HashSet<>(req.seats());
        if (requested.size() != req.seats().size()) {
            throw new AppException(HttpStatus.BAD_REQUEST, "DUPLICATE_SEATS",
                    "Duplicate seat in request");
        }

        List<Booking> existing = bookingRepository
                .findActiveByMovieAndShowtime(req.movieId(), req.showtime());
        Set<String> takenSeats = existing.stream()
                .flatMap(b -> Arrays.stream(b.getSeats().split(",")))
                .map(String::trim)
                .collect(Collectors.toSet());
        Set<String> conflict = new HashSet<>(requested);
        conflict.retainAll(takenSeats);
        if (!conflict.isEmpty()) {
            throw new AppException(HttpStatus.CONFLICT, "SEAT_TAKEN",
                    "Seats already booked: " + conflict);
        }

        BigDecimal amount = req.unitPrice()
                .multiply(BigDecimal.valueOf(req.seats().size()));

        Instant now = Instant.now();
        Booking booking = Booking.builder()
                .id("b-" + UUID.randomUUID().toString().substring(0, 8))
                .userId(userId)
                .movieId(req.movieId())
                .showtime(req.showtime())
                .seats(String.join(",", req.seats()))
                .unitPrice(req.unitPrice())
                .amount(amount)
                .status(BookingStatus.PENDING)
                .createdAt(now)
                .updatedAt(now)
                .build();
        Booking saved = bookingRepository.save(booking);

        publisher.publishBookingCreated(new BookingCreatedEvent(
                saved.getId(), saved.getUserId(), saved.getMovieId(), saved.getShowtime(),
                req.seats(), saved.getUnitPrice(), saved.getAmount()));

        log.info("Created booking {} user={} movie={} amount={}",
                saved.getId(), saved.getUserId(), saved.getMovieId(), saved.getAmount());
        return toResponse(saved);
    }

    @Override
    public BookingResponse getById(String userId, String bookingId) {
        Booking b = bookingRepository.findById(bookingId)
                .orElseThrow(() -> new AppException(HttpStatus.NOT_FOUND,
                        "BOOKING_NOT_FOUND", "Booking #" + bookingId + " not found"));
        if (!b.getUserId().equals(userId)) {
            throw new AppException(HttpStatus.FORBIDDEN, "FORBIDDEN",
                    "Cannot access another user's booking");
        }
        return toResponse(b);
    }

    @Override
    public List<BookingResponse> listMine(String userId) {
        return bookingRepository.findByUserIdOrderByCreatedAtDesc(userId).stream()
                .map(BookingServiceImpl::toResponse).toList();
    }

    @Override
    @Transactional
    public void markConfirmed(String eventId, PaymentCompletedEvent event) {
        if (alreadyProcessed(eventId)) {
            log.warn("Skip duplicate event {}", eventId);
            return;
        }
        Booking b = bookingRepository.findById(event.bookingId()).orElse(null);
        if (b == null) {
            log.warn("PAYMENT_COMPLETED for unknown booking {}", event.bookingId());
            recordProcessed(eventId, "PAYMENT_COMPLETED");
            return;
        }
        if (b.getStatus() == BookingStatus.PENDING) {
            b.setStatus(BookingStatus.CONFIRMED);
            b.setUpdatedAt(Instant.now());
        }
        recordProcessed(eventId, "PAYMENT_COMPLETED");
    }

    @Override
    @Transactional
    public void markFailed(String eventId, BookingFailedEvent event) {
        if (alreadyProcessed(eventId)) {
            log.warn("Skip duplicate event {}", eventId);
            return;
        }
        Booking b = bookingRepository.findById(event.bookingId()).orElse(null);
        if (b == null) {
            log.warn("BOOKING_FAILED for unknown booking {}", event.bookingId());
            recordProcessed(eventId, "BOOKING_FAILED");
            return;
        }
        if (b.getStatus() == BookingStatus.PENDING) {
            b.setStatus(BookingStatus.FAILED);
            b.setFailedReason(event.reason());
            b.setUpdatedAt(Instant.now());
        }
        recordProcessed(eventId, "BOOKING_FAILED");
    }

    private boolean alreadyProcessed(String eventId) {
        return processedEventRepository.existsById(eventId);
    }

    private void recordProcessed(String eventId, String type) {
        processedEventRepository.save(ProcessedEvent.builder()
                .eventId(eventId)
                .eventType(type)
                .processedAt(Instant.now())
                .build());
    }

    private static BookingResponse toResponse(Booking b) {
        List<String> seats = Arrays.stream(b.getSeats().split(",")).map(String::trim).toList();
        return new BookingResponse(b.getId(), b.getUserId(), b.getMovieId(), b.getShowtime(),
                seats, b.getUnitPrice(), b.getAmount(), b.getStatus(), b.getFailedReason(),
                b.getCreatedAt(), b.getUpdatedAt());
    }
}
