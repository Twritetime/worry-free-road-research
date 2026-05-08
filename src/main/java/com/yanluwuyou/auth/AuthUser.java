package com.yanluwuyou.auth;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 认证用户信息
 * 用于在请求间传递当前认证用户的基本信息
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class AuthUser {
    /**
     * 用户ID
     */
    private Long userId;
    
    /**
     * 用户角色
     */
    private String role;
}
