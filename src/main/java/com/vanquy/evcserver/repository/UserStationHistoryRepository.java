package com.vanquy.evcserver.repository;

import com.vanquy.evcserver.model.UserStationHistory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserStationHistoryRepository extends JpaRepository<UserStationHistory, Long> {

    /**
     * Lấy lịch sử xem trạm của user, sắp xếp theo lần xem gần nhất.
     */
    List<UserStationHistory> findByUserUserIdOrderByLastVisitedDesc(Long userId);

    /**
     * Tìm bản ghi lịch sử theo cặp (userId, stationId) — dùng cho upsert.
     */
    Optional<UserStationHistory> findByUserUserIdAndStationStationId(Long userId, Long stationId);
}
