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
@Table(name = "bookings")
public class Booking {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "booking_id")
    private Long bookingId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "station_id", nullable = false)
    private ChargingStation station;

    @Column(name = "connector_type", nullable = false, length = 20)
    private String connectorType;

    @Column(name = "booking_time", nullable = false)
    private LocalDateTime bookingTime;

    @Column(name = "expiry_time", nullable = false)
    private LocalDateTime expiryTime;

    @Column(name = "booking_fee", nullable = false)
    @Builder.Default
    private Double bookingFee = 20000.0;

    @Column(name = "status", nullable = false, length = 20)
    @Builder.Default
    private String status = "PENDING"; // PENDING, ACTIVE, COMPLETED, CANCELLED, EXPIRED

    @PrePersist
    protected void onCreate() {
        this.bookingTime = LocalDateTime.now();
        if (this.expiryTime == null) {
            this.expiryTime = this.bookingTime.plusMinutes(15);
        }
    }
}
