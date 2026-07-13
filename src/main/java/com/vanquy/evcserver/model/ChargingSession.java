package com.vanquy.evcserver.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Table(name = "charging_sessions")
public class ChargingSession {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "session_id")
    private Long sessionId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "station_id", nullable = false)
    private ChargingStation station;

    @Column(name = "connector_type", nullable = false, length = 20)
    private String connectorType;

    @Column(name = "power_kw", nullable = false)
    private Double powerKw;

    @Column(name = "energy_charged", nullable = false)
    @Builder.Default
    private Double energyCharged = 0.0;

    @Column(name = "total_cost", nullable = false)
    @Builder.Default
    private Double totalCost = 0.0;

    @Column(name = "start_time", nullable = false)
    private LocalDateTime startTime;

    @Column(name = "end_time")
    private LocalDateTime endTime;

    @Column(name = "status", nullable = false, length = 20)
    @Builder.Default
    private String status = "CHARGING"; // CHARGING, COMPLETED, CANCELLED

    @PrePersist
    protected void onCreate() {
        this.startTime = LocalDateTime.now();
    }
}
