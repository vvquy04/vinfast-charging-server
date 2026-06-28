package com.vanquy.evcserver.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class StationHistoryResponse {
    private Long historyId;
    private Long stationId;
    private String stationName;
    private String stationAddress;
    private String stationImageUrl;
    private Integer visitCount;
    private LocalDateTime lastVisited;
}
