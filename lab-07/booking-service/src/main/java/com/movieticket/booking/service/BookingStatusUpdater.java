package com.movieticket.booking.service;

import com.movieticket.booking.event.BookingFailedEvent;
import com.movieticket.booking.event.PaymentCompletedEvent;

public interface BookingStatusUpdater {

    void markConfirmed(String eventId, PaymentCompletedEvent event);

    void markFailed(String eventId, BookingFailedEvent event);
}
