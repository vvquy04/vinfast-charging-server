package com.vanquy.evcserver.service.impl;

import com.vanquy.evcserver.dto.response.StationHistoryResponse;
import com.vanquy.evcserver.exception.BadRequestException;
import com.vanquy.evcserver.exception.ResourceNotFoundException;
import com.vanquy.evcserver.model.ChargingStation;
import com.vanquy.evcserver.model.User;
import com.vanquy.evcserver.model.UserStationHistory;
import com.vanquy.evcserver.repository.ChargingStationRepository;
import com.vanquy.evcserver.repository.UserRepository;
import com.vanquy.evcserver.repository.UserStationHistoryRepository;
import com.vanquy.evcserver.service.StationHistoryService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class StationHistoryServiceImpl implements StationHistoryService {

    private final UserStationHistoryRepository historyRepository;
    private final UserRepository userRepository;
    private final ChargingStationRepository stationRepository;

    @Override
    @Transactional
    public void recordVisit(Long userId, Long stationId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("Người dùng", "userId", userId));

        ChargingStation station = stationRepository.findById(stationId)
                .orElseThrow(() -> new ResourceNotFoundException("Trạm sạc", "stationId", stationId));

        // Upsert: nếu đã có bản ghi thì tăng visit_count, nếu chưa thì tạo mới
        Optional<UserStationHistory> existing = historyRepository
                .findByUserUserIdAndStationStationId(userId, stationId);

        if (existing.isPresent()) {
            UserStationHistory history = existing.get();
            history.setVisitCount(history.getVisitCount() + 1);
            history.setLastVisited(LocalDateTime.now());
            historyRepository.save(history);
        } else {
            UserStationHistory history = UserStationHistory.builder()
                    .user(user)
                    .station(station)
                    .visitCount(1)
                    .build();
            historyRepository.save(history);
        }
    }

    @Override
    public List<StationHistoryResponse> getHistory(Long userId) {
        List<UserStationHistory> histories = historyRepository
                .findByUserUserIdOrderByLastVisitedDesc(userId);

        return histories.stream()
                .map(this::mapToResponse)
                .toList();
    }

    @Override
    @Transactional
    public void deleteHistory(Long userId, Long historyId) {
        UserStationHistory history = historyRepository.findById(historyId)
                .orElseThrow(() -> new ResourceNotFoundException("Lịch sử", "historyId", historyId));

        if (!history.getUser().getUserId().equals(userId)) {
            throw new BadRequestException("Bạn không có quyền xóa lịch sử này");
        }

        historyRepository.delete(history);
    }

    // ─── Private Helper ─────────────────────────────────────

    private StationHistoryResponse mapToResponse(UserStationHistory history) {
        ChargingStation station = history.getStation();
        return StationHistoryResponse.builder()
                .historyId(history.getHistoryId())
                .stationId(station.getStationId())
                .stationName(station.getName())
                .stationAddress(station.getAddress())
                .stationImageUrl(station.getImageUrl())
                .visitCount(history.getVisitCount())
                .lastVisited(history.getLastVisited())
                .build();
    }
}
