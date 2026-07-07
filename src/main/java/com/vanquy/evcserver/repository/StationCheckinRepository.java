package com.vanquy.evcserver.repository;

import com.vanquy.evcserver.model.StationCheckin;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface StationCheckinRepository extends JpaRepository<StationCheckin, Long> {

    /**
     * Lấy bản ghi check-in mới nhất của một trạm sạc.
     */
    Optional<StationCheckin> findFirstByStationStationIdOrderByCreatedAtDesc(Long stationId);

    /**
     * Lấy bản ghi check-in mới nhất cho một danh sách trạm sạc (batch query).
     * Dùng để tối ưu hiệu suất khi truy vấn trạng thái cho nhiều trạm cùng lúc.
     */
    @Query(value = """
            SELECT sc.* FROM station_checkins sc
            INNER JOIN (
                SELECT station_id, MAX(created_at) AS max_created
                FROM station_checkins
                WHERE station_id IN (:stationIds)
                GROUP BY station_id
            ) latest ON sc.station_id = latest.station_id AND sc.created_at = latest.max_created
            """, nativeQuery = true)
    List<StationCheckin> findLatestCheckinsByStationIds(@Param("stationIds") List<Long> stationIds);

    /**
     * Kiểm tra chống spam: tìm check-in của user tại station trong khoảng thời gian gần đây.
     */
    Optional<StationCheckin> findFirstByUserUserIdAndStationStationIdAndCreatedAtAfterOrderByCreatedAtDesc(
            Long userId, Long stationId, LocalDateTime after);

    /**
     * Đếm số lượt check-in của một trạm sạc, nhóm theo thứ trong tuần và giờ trong ngày.
     */
    @Query(value = """
            SELECT DAYOFWEEK(created_at) AS day_of_week, HOUR(created_at) AS hour_of_day, COUNT(*) AS count
            FROM station_checkins
            WHERE station_id = :stationId
            GROUP BY DAYOFWEEK(created_at), HOUR(created_at)
            """, nativeQuery = true)
    List<Object[]> countCheckinsGroupByDayAndHour(@Param("stationId") Long stationId);

    // ═══════════════════════════════════════════════════
    //  THỐNG KÊ ADMIN DASHBOARD
    // ═══════════════════════════════════════════════════

    /**
     * Đếm số lượt check-in nhóm theo trạng thái (EMPTY, MODERATE, BUSY, MAINTENANCE).
     */
    @Query(value = """
            SELECT status, COUNT(*) AS count
            FROM station_checkins
            GROUP BY status
            """, nativeQuery = true)
    List<Object[]> countCheckinsByStatus();

    /**
     * Đếm số lượt check-in nhóm theo tháng (yyyy-MM).
     */
    @Query(value = """
            SELECT DATE_FORMAT(created_at, '%Y-%m') AS month, COUNT(*) AS count
            FROM station_checkins
            GROUP BY DATE_FORMAT(created_at, '%Y-%m')
            ORDER BY month
            """, nativeQuery = true)
    List<Object[]> countCheckinsByMonth();

    /**
     * Top N trạm sạc có nhiều lượt check-in nhất.
     */
    @Query(value = """
            SELECT sc.station_id, cs.name, COUNT(*) AS total
            FROM station_checkins sc
            JOIN charging_stations cs ON sc.station_id = cs.station_id
            GROUP BY sc.station_id, cs.name
            ORDER BY total DESC
            LIMIT :limit
            """, nativeQuery = true)
    List<Object[]> findTopCheckinStations(@Param("limit") int limit);
}
