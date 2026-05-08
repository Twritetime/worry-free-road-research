package com.yanluwuyou.dto;

import lombok.Data;

/**
 * 用户登录请求DTO
 * 用于接收前端传递的用户登录信息
 */
@Data
public class UserLoginDTO {
    
    /**
     * 用户名（登录账号）
     */
    private String username;
    
    /**
     * 登录密码（明文，后端会进行加密验证）
     */
    private String password;
}
