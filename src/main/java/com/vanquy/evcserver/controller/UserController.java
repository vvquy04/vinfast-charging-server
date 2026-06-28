package com.vanquy.evcserver.controller;

import com.vanquy.evcserver.dto.request.ChangePasswordRequest;
import com.vanquy.evcserver.dto.request.UpdateProfileRequest;
import com.vanquy.evcserver.dto.response.UserProfileResponse;
import com.vanquy.evcserver.service.UserService;
import com.vanquy.evcserver.util.SecurityUtil;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    /**
     * GET /api/users/me — Lấy thông tin hồ sơ của người dùng hiện tại.
     */
    @GetMapping("/me")
    public ResponseEntity<?> getMyProfile() {
        Long userId = SecurityUtil.getCurrentUserId();
        UserProfileResponse profile = userService.getProfile(userId);
        return ResponseEntity.ok(Map.of(
                "success", true,
                "data", profile
        ));
    }

    /**
     * PUT /api/users/me — Cập nhật hồ sơ người dùng hiện tại.
     */
    @PutMapping("/me")
    public ResponseEntity<?> updateMyProfile(@RequestBody UpdateProfileRequest request) {
        Long userId = SecurityUtil.getCurrentUserId();
        UserProfileResponse profile = userService.updateProfile(userId, request);
        return ResponseEntity.ok(Map.of(
                "success", true,
                "message", "Cập nhật hồ sơ thành công",
                "data", profile
        ));
    }

    /**
     * PUT /api/users/me/change-password — Đổi mật khẩu người dùng hiện tại.
     */
    @PutMapping("/me/change-password")
    public ResponseEntity<?> changePassword(@Valid @RequestBody ChangePasswordRequest request) {
        Long userId = SecurityUtil.getCurrentUserId();
        userService.changePassword(userId, request);
        return ResponseEntity.ok(Map.of(
                "success", true,
                "message", "Đổi mật khẩu thành công"
        ));
    }
}

