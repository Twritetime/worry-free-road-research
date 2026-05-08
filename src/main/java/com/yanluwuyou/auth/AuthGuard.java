package com.yanluwuyou.auth;

import com.yanluwuyou.entity.User;

/**
 * 认证守卫工具类
 * 提供便捷的方法获取当前用户信息和权限校验
 */
public class AuthGuard {

    /**
     * 获取当前登录用户的ID
     * 
     * @return 用户ID
     * @throws RuntimeException 如果用户未登录
     */
    public static Long currentUserId() {
        AuthUser authUser = AuthContext.get();
        if (authUser == null || authUser.getUserId() == null) {
            throw new RuntimeException("请先登录");
        }
        return authUser.getUserId();
    }

    /**
     * 获取当前登录用户的角色
     * 
     * @return 用户角色
     * @throws RuntimeException 如果用户未登录
     */
    public static String currentRole() {
        AuthUser authUser = AuthContext.get();
        if (authUser == null || authUser.getRole() == null) {
            throw new RuntimeException("请先登录");
        }
        return authUser.getRole();
    }

    /**
     * 判断当前用户是否为管理员或运营人员
     * 
     * @return true-是管理员或运营人员, false-普通用户
     */
    public static boolean isAdminOrOperator() {
        String role = currentRole();
        return User.ROLE_ADMIN.equals(role) || User.ROLE_OPERATOR.equals(role);
    }

    /**
     * 判断当前用户是否为管理员
     * 
     * @return true-是管理员, false-非管理员
     */
    public static boolean isAdmin() {
        return User.ROLE_ADMIN.equals(currentRole());
    }

    /**
     * 断言当前用户是数据所有者或管理员
     * 用于保护用户私有数据，防止越权访问
     * 
     * @param ownerId 数据所有者ID
     * @throws RuntimeException 如果数据所有者ID为空或当前用户无权限操作
     */
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
