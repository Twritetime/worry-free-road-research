package com.yanluwuyou.dto;

import lombok.Data;

/**
 * 用户注册请求DTO
 * 用于接收前端传递的用户注册信息
 */
@Data
public class UserRegisterDTO {
    
    /**
     * 用户名（登录账号，需唯一）
     */
    private String username;
    
    /**
     * 登录密码（明文，后端会进行加密存储）
     */
    private String password;
    
    /**
     * 昵称（显示名称）
     */
    private String nickname;
    
    /**
     * 邮箱地址（可选）
     */
    private String email;
    
    /**
     * 手机号码（可选）
     */
    private String phone;
}
