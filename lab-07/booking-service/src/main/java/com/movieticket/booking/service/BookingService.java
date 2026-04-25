package com.movieticket.booking.service;

import com.movieticket.booking.dto.BookingResponse;
import com.movieticket.booking.dto.CreateBookingRequest;
import java.util.List;

public interface BookingService {

    BookingResponse create(String userId, CreateBookingRequest request);

    BookingResponse getById(String userId, String bookingId);

    List<BookingResponse> listMine(String userId);
}
