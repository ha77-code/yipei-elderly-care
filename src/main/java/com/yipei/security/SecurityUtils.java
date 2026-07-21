package com.yipei.security;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

/**
 * 安全工具类：从SecurityContext中获取当前登录用户信息。
 */
public final class SecurityUtils {

    private SecurityUtils() {}

    /** 获取当前登录用户的JwtUser信息 */
    public static JwtUser getCurrentUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !authentication.isAuthenticated()) {
            return null;
        }
        Object principal = authentication.getPrincipal();
        if (principal instanceof JwtUser) {
            return (JwtUser) principal;
        }
        // 兼容：如果details中存储了JwtUser
        if (authentication.getDetails() instanceof JwtUser) {
            return (JwtUser) authentication.getDetails();
        }
        return null;
    }

    /** 获取当前登录用户ID */
    public static Long getCurrentUserId() {
        JwtUser user = getCurrentUser();
        return user != null ? user.getUserId() : null;
    }

    /** 获取当前登录用户角色 */
    public static String getCurrentUserRole() {
        JwtUser user = getCurrentUser();
        return user != null ? user.getRole() : null;
    }

    /** 判断当前用户是否为管理员 */
    public static boolean isAdmin() {
        JwtUser user = getCurrentUser();
        return user != null && user.isAdmin();
    }

    /** 获取当前登录用户，若未登录则抛异常 */
    public static JwtUser requireLogin() {
        JwtUser user = getCurrentUser();
        if (user == null) {
            throw new com.yipei.exception.ForbiddenException("请先登录");
        }
        return user;
    }

    /** 获取当前登录用户ID，若未登录则抛异常 */
    public static Long requireLoginUserId() {
        return requireLogin().getUserId();
    }
}