package com.vanquy.evcserver.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Response trả về khi user đăng nhập Google nhưng chưa có trong hệ thống
 * và cần phải bổ sung thêm số điện thoại để đăng ký hoàn tất.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class OAuth2RequirePhoneResponse {

    private boolean requiresPhone;
    private String email;
    private String fullName;
    private String avatarUrl;
}
