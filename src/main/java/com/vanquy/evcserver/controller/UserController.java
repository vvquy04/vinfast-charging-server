package com.vanquy.evcserver.controller;

import com.vanquy.evcserver.dto.request.ChangePasswordRequest;
import com.vanquy.evcserver.dto.request.UpdateProfileRequest;
import com.vanquy.evcserver.dto.response.ApiResponse;
import com.vanquy.evcserver.dto.response.UserResponse;
import com.vanquy.evcserver.service.UserService;
import com.vanquy.evcserver.util.SecurityUtil;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 * REST Controller cho User Profile.
 * Quản lý thông tin cá nhân, đổi mật khẩu.
 */
@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    /**
     * GET /api/users/me — Lấy thông tin cá nhân.
     */
    @GetMapping("/me")
    public ResponseEntity<ApiResponse<UserResponse>> getProfile() {
        Long userId = SecurityUtil.getCurrentUserId();
        UserResponse data = userService.getProfile(userId);
        return ResponseEntity.ok(ApiResponse.success("OK", data));
    }

    /**
     * PUT /api/users/me — Cập nhật thông tin cá nhân.
     */
    @PutMapping("/me")
    public ResponseEntity<ApiResponse<UserResponse>> updateProfile(
            @RequestBody UpdateProfileRequest request
    ) {
        Long userId = SecurityUtil.getCurrentUserId();
        UserResponse data = userService.updateProfile(userId, request);
        return ResponseEntity.ok(ApiResponse.success("Cập nhật thành công", data));
    }

    /**
     * PUT /api/users/me/password — Đổi mật khẩu.
     */
    @PutMapping("/me/password")
    public ResponseEntity<ApiResponse<Object>> changePassword(
            @Valid @RequestBody ChangePasswordRequest request
    ) {
        Long userId = SecurityUtil.getCurrentUserId();
        userService.changePassword(userId, request);
        return ResponseEntity.ok(ApiResponse.success("Đổi mật khẩu thành công"));
    }
}
