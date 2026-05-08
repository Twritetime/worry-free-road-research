package com.yanluwuyou.auth;

/**
 * 用户认证上下文
 * 使用ThreadLocal存储当前请求的用户认证信息
 * 确保线程安全，避免并发请求时的数据混乱
 */
public class AuthContext {
    private static final ThreadLocal<AuthUser> CURRENT = new ThreadLocal<>();

    /**
     * 设置当前线程的用户认证信息
     * 
     * @param authUser 认证用户信息
     */
    public static void set(AuthUser authUser) {
        CURRENT.set(authUser);
    }

    /**
     * 获取当前线程的用户认证信息
     * 
     * @return 认证用户信息
     */
    public static AuthUser get() {
        return CURRENT.get();
    }

    /**
     * 清除当前线程的用户认证信息
     * 防止内存泄漏，必须在请求完成后调用
     */
    public static void clear() {
        CURRENT.remove();
    }
}
