package com.vanquy.evcserver.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Entity ánh xạ bảng `connector_types`.
 * Mỗi trạm sạc có nhiều loại cổng sạc khác nhau.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Table(name = "connector_types")
public class ConnectorType {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "connector_id")
    private Long connectorId;

    @Column(name = "type", nullable = false, length = 20)
    private String type;

    @Column(name = "power_kw", nullable = false)
    private Integer powerKw;

    @Column(name = "total_ports", nullable = false)
    @Builder.Default
    private Integer totalPorts = 1;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "station_id", nullable = false)
    @JsonIgnore
    private ChargingStation station;
}
