package com.vanquy.evcserver.dto.request;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class StartChargingRequest {
    private Long stationId;
    private String connectorType;
    private Double powerKw;
}
