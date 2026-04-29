package com.vanquy.evcserver.repository;

import com.vanquy.evcserver.model.ConnectorType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ConnectorTypeRepository extends JpaRepository<ConnectorType, Long> {

    /**
     * Lấy danh sách loại cổng sạc theo stationId.
     */
    List<ConnectorType> findByStationStationId(Long stationId);
}
