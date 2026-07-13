package com.vanquy.evcserver.service.impl;

import com.vanquy.evcserver.exception.BadRequestException;
import com.vanquy.evcserver.exception.ResourceNotFoundException;
import com.vanquy.evcserver.model.Booking;
import com.vanquy.evcserver.model.ChargingStation;
import com.vanquy.evcserver.model.User;
import com.vanquy.evcserver.repository.BookingRepository;
import com.vanquy.evcserver.repository.ChargingStationRepository;
import com.vanquy.evcserver.repository.UserRepository;
import com.vanquy.evcserver.service.BookingService;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class BookingServiceImpl implements BookingService {

    private final BookingRepository bookingRepository;
    private final UserRepository userRepository;
    private final ChargingStationRepository chargingStationRepository;

    @Override
    @Transactional
    public Booking createBooking(Long userId, Long stationId, String connectorType) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy người dùng"));

        ChargingStation station = chargingStationRepository.findById(stationId)
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy trạm sạc"));

        // Kiểm tra xem có booking nào đang active không
        bookingRepository.findActiveBooking(userId, LocalDateTime.now()).ifPresent(b -> {
            throw new BadRequestException("Bạn đang có một lịch đặt chỗ chưa hoàn tất!");
        });

        // Kiểm tra số dư ví
        double fee = 20000.0;
        double currentBalance = user.getBalance() != null ? user.getBalance() : 0.0;
        if (currentBalance < fee) {
            throw new BadRequestException("Số dư ví không đủ để thực hiện đặt chỗ (cần tối thiểu 20.000đ). Vui lòng nạp thêm tiền!");
        }

        // Khấu trừ cọc giữ chỗ 20k
        user.setBalance(currentBalance - fee);
        userRepository.save(user);

        Booking booking = Booking.builder()
                .user(user)
                .station(station)
                .connectorType(connectorType)
                .bookingFee(fee)
                .status("PENDING")
                .expiryTime(LocalDateTime.now().plusMinutes(15))
                .build();

        return bookingRepository.save(booking);
    }

    @Override
    @Transactional
    public void cancelBooking(Long bookingId) {
        Booking booking = bookingRepository.findById(bookingId)
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy lịch đặt chỗ"));

        if (!"PENDING".equalsIgnoreCase(booking.getStatus())) {
            throw new BadRequestException("Chỉ có thể hủy lịch đặt chỗ ở trạng thái Đang chờ!");
        }

        // Hoàn cọc 20k
        User user = booking.getUser();
        if (user != null) {
            double currentBalance = user.getBalance() != null ? user.getBalance() : 0.0;
            user.setBalance(currentBalance + booking.getBookingFee());
            userRepository.save(user);
        }

        booking.setStatus("CANCELLED");
        bookingRepository.save(booking);
    }

    @Override
    public Booking getActiveBooking(Long userId) {
        return bookingRepository.findActiveBooking(userId, LocalDateTime.now()).orElse(null);
    }

    @Override
    public List<Booking> getBookingHistory(Long userId) {
        return bookingRepository.findByUserUserIdOrderByBookingTimeDesc(userId);
    }

    @Override
    @Transactional
    @Scheduled(fixedDelay = 30000) // Tự động quét và hủy các đặt chỗ quá hạn mỗi 30 giây
    public void checkAndExpireBookings() {
        List<Booking> expiredList = bookingRepository.findExpiredBookings(LocalDateTime.now());
        if (!expiredList.isEmpty()) {
            for (Booking booking : expiredList) {
                booking.setStatus("EXPIRED"); // Trạng thái quá hạn, không hoàn cọc giữ chỗ
                bookingRepository.save(booking);
            }
        }
    }
}
