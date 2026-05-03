package com.vanquy.evcserver.dto.request;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Request body cho đăng nhập bằng mạng xã hội (Google).
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class OAuth2LoginRequest {

    @NotBlank(message = "idToken không được để trống")
    private String idToken;
}
