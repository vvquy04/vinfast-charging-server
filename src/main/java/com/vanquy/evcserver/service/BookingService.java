package com.vanquy.evcserver.service;

import com.vanquy.evcserver.model.Booking;

import java.util.List;

public interface BookingService {
    Booking createBooking(Long userId, Long stationId, String connectorType);
    void cancelBooking(Long bookingId);
    Booking getActiveBooking(Long userId);
    List<Booking> getBookingHistory(Long userId);
    void checkAndExpireBookings();
}
