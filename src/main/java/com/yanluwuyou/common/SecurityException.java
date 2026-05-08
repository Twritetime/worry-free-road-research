package com.yanluwuyou.common;

/**
 * 安全异常类
 * 用于表示与安全相关的业务异常，如权限不足、认证失败等
 */
public class SecurityException extends RuntimeException {

    /**
     * 异常状态码，默认400
     */
    private int code = 400;

    /**
     * 构造安全异常（使用默认状态码400）
     * 
     * @param message 异常消息
     */
    public SecurityException(String message) {
        super(message);
    }

    /**
     * 构造安全异常（指定状态码）
     * 
     * @param code 状态码
     * @param message 异常消息
     */
    public SecurityException(int code, String message) {
        super(message);
        this.code = code;
    }

    /**
     * 获取异常状态码
     * 
     * @return 状态码
     */
    public int getCode() {
        return code;
    }
}
