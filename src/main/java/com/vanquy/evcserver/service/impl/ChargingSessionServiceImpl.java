package com.vanquy.evcserver.service.impl;

import com.vanquy.evcserver.exception.BadRequestException;
import com.vanquy.evcserver.exception.ResourceNotFoundException;
import com.vanquy.evcserver.model.Booking;
import com.vanquy.evcserver.model.ChargingSession;
import com.vanquy.evcserver.model.ChargingStation;
import com.vanquy.evcserver.model.User;
import com.vanquy.evcserver.repository.BookingRepository;
import com.vanquy.evcserver.repository.ChargingSessionRepository;
import com.vanquy.evcserver.repository.ChargingStationRepository;
import com.vanquy.evcserver.repository.UserRepository;
import com.vanquy.evcserver.service.ChargingSessionService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ChargingSessionServiceImpl implements ChargingSessionService {

    private final ChargingSessionRepository chargingSessionRepository;
    private final BookingRepository bookingRepository;
    private final UserRepository userRepository;
    private final ChargingStationRepository chargingStationRepository;

    @Override
    @Transactional
    public ChargingSession startSession(Long userId, Long stationId, String connectorType, Double powerKw) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy người dùng"));

        ChargingStation station = chargingStationRepository.findById(stationId)
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy trạm sạc"));

        // Kiểm tra xem có phiên sạc nào đang active không
        chargingSessionRepository.findActiveSession(userId).ifPresent(s -> {
            throw new BadRequestException("Bạn đang có một phiên sạc chưa kết thúc!");
        });

        // Kiểm tra và xử lý Booking PENDING nếu có
        bookingRepository.findActiveBooking(userId, LocalDateTime.now()).ifPresent(booking -> {
            if (booking.getStation().getStationId().equals(stationId)) {
                // Nhận chỗ thành công -> Chuyển trạng thái booking sang ACTIVE
                booking.setStatus("ACTIVE");
                bookingRepository.save(booking);

                // Hoàn trả cọc giữ chỗ 20.000đ vào ví của User
                double currentBalance = user.getBalance() != null ? user.getBalance() : 0.0;
                user.setBalance(currentBalance + booking.getBookingFee());
                userRepository.save(user);
            }
        });

        ChargingSession session = ChargingSession.builder()
                .user(user)
                .station(station)
                .connectorType(connectorType)
                .powerKw(powerKw)
                .energyCharged(0.0)
                .totalCost(0.0)
                .status("CHARGING")
                .startTime(LocalDateTime.now())
                .build();

        return chargingSessionRepository.save(session);
    }

    @Override
    @Transactional
    public ChargingSession stopSession(Long sessionId, Double energyCharged) {
        ChargingSession session = chargingSessionRepository.findById(sessionId)
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy phiên sạc"));

        if (!"CHARGING".equalsIgnoreCase(session.getStatus())) {
            throw new BadRequestException("Phiên sạc này đã kết thúc trước đó!");
        }

        User user = session.getUser();
        
        // Tính toán tổng tiền sạc thực tế: số kWh sạc x 3.858đ/kWh (Giá điện VinFast tiêu chuẩn)
        double pricePerKwh = 3858.0;
        double cost = energyCharged * pricePerKwh;
        
        // Làm tròn tiền sạc
        cost = Math.round(cost * 100.0) / 100.0;

        // Trừ tiền trực tiếp vào ví
        double currentBalance = user.getBalance() != null ? user.getBalance() : 0.0;
        user.setBalance(Math.max(0.0, currentBalance - cost));

        // Cộng điểm thưởng tích lũy (Cứ mỗi 10.000đ sạc xe được cộng 1 điểm thưởng)
        int pointsEarned = (int) (cost / 10000.0);
        user.setRewardPoints((user.getRewardPoints() != null ? user.getRewardPoints() : 0) + pointsEarned);

        userRepository.save(user);

        // Cập nhật phiên sạc
        session.setEndTime(LocalDateTime.now());
        session.setEnergyCharged(energyCharged);
        session.setTotalCost(cost);
        session.setStatus("COMPLETED");

        // Đồng thời cập nhật trạng thái Booking liên quan sang COMPLETED
        bookingRepository.findByUserUserIdOrderByBookingTimeDesc(user.getUserId()).stream()
                .filter(b -> "ACTIVE".equals(b.getStatus()) && b.getStation().getStationId().equals(session.getStation().getStationId()))
                .findFirst()
                .ifPresent(b -> {
                    b.setStatus("COMPLETED");
                    bookingRepository.save(b);
                });

        return chargingSessionRepository.save(session);
    }

    @Override
    public ChargingSession getActiveSession(Long userId) {
        return chargingSessionRepository.findActiveSession(userId).orElse(null);
    }

    @Override
    public List<ChargingSession> getSessionHistory(Long userId) {
        return chargingSessionRepository.findByUserUserIdOrderByStartTimeDesc(userId);
    }
}
