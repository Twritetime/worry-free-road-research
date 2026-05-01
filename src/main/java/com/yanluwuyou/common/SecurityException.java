package com.yanluwuyou.common;

public class SecurityException extends RuntimeException {

    private int code = 400;

    public SecurityException(String message) {
        super(message);
    }

    public SecurityException(int code, String message) {
        super(message);
        this.code = code;
    }

    public int getCode() {
        return code;
    }
}
