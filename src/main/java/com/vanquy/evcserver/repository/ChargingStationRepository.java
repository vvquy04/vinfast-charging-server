package com.vanquy.evcserver.repository;

import com.vanquy.evcserver.model.ChargingStation;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ChargingStationRepository extends JpaRepository<ChargingStation, Long> {

    /**
     * Tìm trạm sạc trong bán kính (km) dùng Haversine formula.
     * Hỗ trợ lọc tùy chọn theo: loại cổng sạc, công suất tối thiểu, rating tối thiểu.
     * Tất cả filter đều nullable — nếu null thì bỏ qua điều kiện đó.
     */
    @Query(value = """
            SELECT DISTINCT s.*, (
                6371 * acos(
                    cos(radians(:lat)) * cos(radians(s.latitude))
                    * cos(radians(s.longitude) - radians(:lng))
                    + sin(radians(:lat)) * sin(radians(s.latitude))
                )
            ) AS distance
            FROM charging_stations s
            LEFT JOIN connector_types ct ON ct.station_id = s.station_id
            WHERE s.is_active = true
              AND s.latitude BETWEEN :minLat AND :maxLat
              AND s.longitude BETWEEN :minLng AND :maxLng
              AND (:connectorType IS NULL OR ct.type = :connectorType)
              AND (:minPowerKw IS NULL OR ct.power_kw >= :minPowerKw)
              AND (:minRating IS NULL OR s.rating >= :minRating)
            HAVING distance <= :radius
            ORDER BY distance ASC
            """,
            nativeQuery = true)
    List<Object[]> findNearbyStationsFiltered(
            @Param("lat") double lat,
            @Param("lng") double lng,
            @Param("radius") double radius,
            @Param("connectorType") String connectorType,
            @Param("minPowerKw") Integer minPowerKw,
            @Param("minRating") Double minRating,
            @Param("minLat") double minLat,
            @Param("maxLat") double maxLat,
            @Param("minLng") double minLng,
            @Param("maxLng") double maxLng
    );
    /**
     * Đếm số trạm sạc theo trạng thái hoạt động.
     */
    long countByIsActive(Boolean isActive);
}
