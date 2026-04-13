package com.yanluwuyou.auth;

public class AuthContext {
    private static final ThreadLocal<AuthUser> CURRENT = new ThreadLocal<>();

    public static void set(AuthUser authUser) {
        CURRENT.set(authUser);
    }

    public static AuthUser get() {
        return CURRENT.get();
    }

    public static void clear() {
        CURRENT.remove();
    }
}
