package com.yanluwuyou.auth;

import com.yanluwuyou.entity.User;

public class AuthGuard {

    public static Long currentUserId() {
        AuthUser authUser = AuthContext.get();
        if (authUser == null || authUser.getUserId() == null) {
            throw new RuntimeException("请先登录");
        }
        return authUser.getUserId();
    }

    public static String currentRole() {
        AuthUser authUser = AuthContext.get();
        if (authUser == null || authUser.getRole() == null) {
            throw new RuntimeException("请先登录");
        }
        return authUser.getRole();
    }

    public static boolean isAdminOrOperator() {
        String role = currentRole();
        return User.ROLE_ADMIN.equals(role) || User.ROLE_OPERATOR.equals(role);
    }

    public static boolean isAdmin() {
        return User.ROLE_ADMIN.equals(currentRole());
    }

    public static void assertOwnerOrAdmin(Long ownerId) {
        if (ownerId == null) {
            throw new RuntimeException("数据异常");
        }
        if (isAdminOrOperator()) {
            return;
        }
        if (!ownerId.equals(currentUserId())) {
            throw new RuntimeException("无权限操作他人数据");
        }
    }
}
