package com.vanquy.evcserver.util;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;

/**
 * Utility class lấy thông tin user hiện tại từ SecurityContext.
 * Dùng chung cho tất cả Controller/Service cần authenticated user.
 */
public final class SecurityUtil {

    private SecurityUtil() {}

    /**
     * Lấy userId của user đang đăng nhập từ JWT token.
     *
     * @return userId (Long)
     * @throws IllegalStateException nếu chưa authenticated
     */
    public static Long getCurrentUserId() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();

        if (auth == null || !auth.isAuthenticated()) {
            throw new IllegalStateException("User chưa đăng nhập");
        }

        Object principal = auth.getPrincipal();

        if (principal instanceof UserDetails userDetails) {
            return Long.parseLong(userDetails.getUsername());
        }

        return Long.parseLong(principal.toString());
    }
}
