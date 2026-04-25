package com.movieticket.booking.controller;

import com.movieticket.booking.dto.BookingResponse;
import com.movieticket.booking.dto.CreateBookingRequest;
import com.movieticket.booking.security.JwtAuthFilter.AuthenticatedUser;
import com.movieticket.booking.service.BookingService;
import jakarta.validation.Valid;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/bookings")
@RequiredArgsConstructor
public class BookingController {

    private final BookingService bookingService;

    @PostMapping
    public ResponseEntity<BookingResponse> create(
            @AuthenticationPrincipal AuthenticatedUser principal,
            @Valid @RequestBody CreateBookingRequest request) {
        BookingResponse resp = bookingService.create(principal.userId(), request);
        return ResponseEntity.status(HttpStatus.ACCEPTED).body(resp);
    }

    @GetMapping
    public ResponseEntity<List<BookingResponse>> listMine(
            @AuthenticationPrincipal AuthenticatedUser principal) {
        return ResponseEntity.ok(bookingService.listMine(principal.userId()));
    }

    @GetMapping("/{id}")
    public ResponseEntity<BookingResponse> getById(
            @AuthenticationPrincipal AuthenticatedUser principal,
            @PathVariable String id) {
        return ResponseEntity.ok(bookingService.getById(principal.userId(), id));
    }
}
