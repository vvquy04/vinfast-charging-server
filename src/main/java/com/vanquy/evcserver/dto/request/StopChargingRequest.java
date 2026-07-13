package com.vanquy.evcserver.dto.request;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class StopChargingRequest {
    private Long sessionId;
    private Double energyCharged;
}
