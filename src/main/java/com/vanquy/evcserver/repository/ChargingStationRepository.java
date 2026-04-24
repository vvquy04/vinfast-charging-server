package com.vanquy.evcserver.repository;

import com.vanquy.evcserver.model.ChargingStation;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface ChargingStationRepository extends JpaRepository<ChargingStation, Long> {

    /**
     * Tìm trạm sạc trong bán kính (km) dùng Haversine formula.
     * Trả về kèm distance (km) — sort theo distance ASC.
     */
    @Query(value = """
            SELECT s.*, (
                6371 * acos(
                    cos(radians(:lat)) * cos(radians(s.latitude))
                    * cos(radians(s.longitude) - radians(:lng))
                    + sin(radians(:lat)) * sin(radians(s.latitude))
                )
            ) AS distance
            FROM charging_stations s
            WHERE s.is_active = true
              AND s.latitude BETWEEN :minLat AND :maxLat
              AND s.longitude BETWEEN :minLng AND :maxLng
            HAVING distance <= :radius
            ORDER BY distance ASC
            """,
            countQuery = """
            SELECT COUNT(*) FROM (
                SELECT s.station_id, (
                    6371 * acos(
                        cos(radians(:lat)) * cos(radians(s.latitude))
                        * cos(radians(s.longitude) - radians(:lng))
                        + sin(radians(:lat)) * sin(radians(s.latitude))
                    )
                ) AS distance
                FROM charging_stations s
                WHERE s.is_active = true
                  AND s.latitude BETWEEN :minLat AND :maxLat
                  AND s.longitude BETWEEN :minLng AND :maxLng
                HAVING distance <= :radius
            ) AS cnt
            """,
            nativeQuery = true)
    Page<Object[]> findNearbyStations(
            @Param("lat") double lat,
            @Param("lng") double lng,
            @Param("radius") double radius,
            @Param("minLat") double minLat,
            @Param("maxLat") double maxLat,
            @Param("minLng") double minLng,
            @Param("maxLng") double maxLng,
            Pageable pageable
    );

    /**
     * Tìm trạm sạc trong bán kính + lọc theo loại cổng sạc.
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
            JOIN connector_types ct ON ct.station_id = s.station_id
            WHERE s.is_active = true
              AND s.latitude BETWEEN :minLat AND :maxLat
              AND s.longitude BETWEEN :minLng AND :maxLng
              AND ct.type = :connectorType
            HAVING distance <= :radius
            ORDER BY distance ASC
            """,
            countQuery = """
            SELECT COUNT(*) FROM (
                SELECT DISTINCT s.station_id, (
                    6371 * acos(
                        cos(radians(:lat)) * cos(radians(s.latitude))
                        * cos(radians(s.longitude) - radians(:lng))
                        + sin(radians(:lat)) * sin(radians(s.latitude))
                    )
                ) AS distance
                FROM charging_stations s
                JOIN connector_types ct ON ct.station_id = s.station_id
                WHERE s.is_active = true
                  AND s.latitude BETWEEN :minLat AND :maxLat
                  AND s.longitude BETWEEN :minLng AND :maxLng
                  AND ct.type = :connectorType
                HAVING distance <= :radius
            ) AS cnt
            """,
            nativeQuery = true)
    Page<Object[]> findNearbyStationsByConnectorType(
            @Param("lat") double lat,
            @Param("lng") double lng,
            @Param("radius") double radius,
            @Param("connectorType") String connectorType,
            @Param("minLat") double minLat,
            @Param("maxLat") double maxLat,
            @Param("minLng") double minLng,
            @Param("maxLng") double maxLng,
            Pageable pageable
    );
}
