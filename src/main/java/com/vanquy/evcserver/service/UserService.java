package com.vanquy.evcserver.service;

import com.vanquy.evcserver.dto.request.ChangePasswordRequest;
import com.vanquy.evcserver.dto.request.UpdateProfileRequest;
import com.vanquy.evcserver.dto.response.UserResponse;

/**
 * Service interface cho User Profile.
 */
public interface UserService {

    /**
     * Lấy thông tin cá nhân của user hiện tại.
     */
    UserResponse getProfile(Long userId);

    /**
     * Cập nhật thông tin cá nhân.
     */
    UserResponse updateProfile(Long userId, UpdateProfileRequest request);

    /**
     * Đổi mật khẩu.
     */
    void changePassword(Long userId, ChangePasswordRequest request);
}
