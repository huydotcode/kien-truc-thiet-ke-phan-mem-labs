package com.movieticket.booking.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.movieticket.booking.domain.Booking;
import com.movieticket.booking.domain.BookingStatus;
import com.movieticket.booking.domain.ProcessedEvent;
import com.movieticket.booking.dto.BookingResponse;
import com.movieticket.booking.dto.CreateBookingRequest;
import com.movieticket.booking.event.BookingFailedEvent;
import com.movieticket.booking.event.PaymentCompletedEvent;
import com.movieticket.booking.exception.AppException;
import com.movieticket.booking.messaging.BookingEventPublisher;
import com.movieticket.booking.repository.BookingRepository;
import com.movieticket.booking.repository.ProcessedEventRepository;
import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class BookingServiceImplTest {

    @Mock private BookingRepository bookingRepository;
    @Mock private ProcessedEventRepository processedEventRepository;
    @Mock private BookingEventPublisher publisher;

    @InjectMocks private BookingServiceImpl service;

    @Test
    void create_shouldComputeAmountAndPublish() {
        CreateBookingRequest req = new CreateBookingRequest(
                "m-1", Instant.parse("2026-04-25T19:00:00Z"),
                List.of("A1", "A2"), new BigDecimal("100000"));
        when(bookingRepository.findActiveByMovieAndShowtime("m-1", req.showtime()))
                .thenReturn(List.of());
        when(bookingRepository.save(any(Booking.class))).thenAnswer(i -> i.getArgument(0));

        BookingResponse resp = service.create("u-1", req);

        assertThat(resp.amount()).isEqualByComparingTo("200000");
        assertThat(resp.status()).isEqualTo(BookingStatus.PENDING);
        verify(publisher).publishBookingCreated(argThat(e ->
                e.bookingId().equals(resp.id()) && e.seats().equals(req.seats())));
    }

    @Test
    void create_shouldRejectDuplicateSeatsInRequest() {
        CreateBookingRequest req = new CreateBookingRequest(
                "m-1", Instant.now(), List.of("A1", "A1"), new BigDecimal("100000"));
        assertThatThrownBy(() -> service.create("u-1", req))
                .isInstanceOf(AppException.class)
                .hasMessageContaining("Duplicate");
    }

    @Test
    void create_shouldRejectAlreadyTakenSeats() {
        Instant when = Instant.parse("2026-04-25T19:00:00Z");
        Booking other = Booking.builder().id("b-x").userId("u-other").movieId("m-1")
                .showtime(when).seats("A1,A2").unitPrice(new BigDecimal("100000"))
                .amount(new BigDecimal("200000")).status(BookingStatus.CONFIRMED)
                .createdAt(Instant.now()).updatedAt(Instant.now()).build();
        when(bookingRepository.findActiveByMovieAndShowtime("m-1", when))
                .thenReturn(List.of(other));

        CreateBookingRequest req = new CreateBookingRequest(
                "m-1", when, List.of("A2", "A3"), new BigDecimal("100000"));

        assertThatThrownBy(() -> service.create("u-1", req))
                .isInstanceOf(AppException.class)
                .hasMessageContaining("Seats already booked");
    }

    @Test
    void markConfirmed_shouldUpdateStatusOnce() {
        Booking b = bookingPending();
        when(processedEventRepository.existsById("evt-1")).thenReturn(false);
        when(bookingRepository.findById("b-1")).thenReturn(Optional.of(b));

        service.markConfirmed("evt-1", new PaymentCompletedEvent(
                "p-1", "b-1", "u-1", new BigDecimal("200000"), Instant.now()));

        assertThat(b.getStatus()).isEqualTo(BookingStatus.CONFIRMED);
        verify(processedEventRepository).save(any(ProcessedEvent.class));
    }

    @Test
    void markConfirmed_shouldSkipDuplicateEvent() {
        when(processedEventRepository.existsById("evt-1")).thenReturn(true);

        service.markConfirmed("evt-1", new PaymentCompletedEvent(
                "p-1", "b-1", "u-1", new BigDecimal("200000"), Instant.now()));

        verify(bookingRepository, times(0)).findById(any());
    }

    @Test
    void markFailed_shouldStoreReason() {
        Booking b = bookingPending();
        when(processedEventRepository.existsById("evt-2")).thenReturn(false);
        when(bookingRepository.findById("b-1")).thenReturn(Optional.of(b));

        service.markFailed("evt-2", new BookingFailedEvent("b-1", "u-1", "card_declined"));

        assertThat(b.getStatus()).isEqualTo(BookingStatus.FAILED);
        assertThat(b.getFailedReason()).isEqualTo("card_declined");
    }

    private static Booking bookingPending() {
        return Booking.builder().id("b-1").userId("u-1").movieId("m-1")
                .showtime(Instant.now()).seats("A1,A2").unitPrice(new BigDecimal("100000"))
                .amount(new BigDecimal("200000")).status(BookingStatus.PENDING)
                .createdAt(Instant.now()).updatedAt(Instant.now()).build();
    }
}
