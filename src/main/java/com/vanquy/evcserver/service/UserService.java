package com.vanquy.evcserver.service;

import com.vanquy.evcserver.dto.request.UpdateProfileRequest;
import com.vanquy.evcserver.dto.response.UserProfileResponse;

public interface UserService {

    /**
     * Lấy thông tin hồ sơ người dùng hiện tại.
     */
    UserProfileResponse getProfile(Long userId);

    /**
     * Cập nhật thông tin hồ sơ (partial update — chỉ ghi đè field khác null).
     */
    UserProfileResponse updateProfile(Long userId, UpdateProfileRequest request);
}
