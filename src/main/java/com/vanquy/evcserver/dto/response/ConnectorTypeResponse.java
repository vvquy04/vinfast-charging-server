package com.vanquy.evcserver.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO hiển thị thông tin loại cổng sạc.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ConnectorTypeResponse {

    private String type;
    private Integer powerKw;
    private Integer totalPorts;
}
