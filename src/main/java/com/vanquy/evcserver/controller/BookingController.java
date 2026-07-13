package com.vanquy.evcserver.controller;

import com.vanquy.evcserver.dto.request.BookingRequest;
import com.vanquy.evcserver.model.Booking;
import com.vanquy.evcserver.service.BookingService;
import com.vanquy.evcserver.util.SecurityUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/bookings")
@RequiredArgsConstructor
public class BookingController {

    private final BookingService bookingService;

    @PostMapping("/create")
    public ResponseEntity<?> createBooking(@RequestBody BookingRequest request) {
        Long userId = SecurityUtil.getCurrentUserId();
        Booking booking = bookingService.createBooking(userId, request.getStationId(), request.getConnectorType());
        return ResponseEntity.ok(Map.of(
                "success", true,
                "message", "Đặt giữ chỗ trạm sạc thành công!",
                "data", booking
        ));
    }

    @PostMapping("/cancel/{id}")
    public ResponseEntity<?> cancelBooking(@PathVariable Long id) {
        bookingService.cancelBooking(id);
        return ResponseEntity.ok(Map.of(
                "success", true,
                "message", "Đã hủy lịch đặt chỗ thành công, cọc 20.000đ đã được hoàn về ví!"
        ));
    }

    @GetMapping("/active")
    public ResponseEntity<?> getActiveBooking() {
        Long userId = SecurityUtil.getCurrentUserId();
        Booking booking = bookingService.getActiveBooking(userId);
        return ResponseEntity.ok(Map.of(
                "success", true,
                "data", booking != null ? booking : Map.of()
        ));
    }

    @GetMapping("/history")
    public ResponseEntity<?> getBookingHistory() {
        Long userId = SecurityUtil.getCurrentUserId();
        List<Booking> bookings = bookingService.getBookingHistory(userId);
        return ResponseEntity.ok(Map.of(
                "success", true,
                "data", bookings
        ));
    }
}
