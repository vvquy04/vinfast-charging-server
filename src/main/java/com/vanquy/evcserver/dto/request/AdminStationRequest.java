package com.vanquy.evcserver.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * Request body cho tạo/sửa trạm sạc từ Admin Dashboard.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class AdminStationRequest {

    @NotBlank(message = "Tên trạm không được để trống")
    private String name;

    @NotBlank(message = "Địa chỉ không được để trống")
    private String address;

    @NotNull(message = "Vĩ độ không được để trống")
    private Double latitude;

    @NotNull(message = "Kinh độ không được để trống")
    private Double longitude;

    private String openingHours = "24/7";
    private String imageUrl;

    private List<ConnectorInput> connectors;

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class ConnectorInput {
        @NotBlank
        private String type;
        @NotNull
        private Integer powerKw;
        @NotNull
        private Integer totalPorts;
    }
}
