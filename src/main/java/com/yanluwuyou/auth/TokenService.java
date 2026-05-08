package com.yanluwuyou.auth;

import cn.hutool.jwt.JWT;
import cn.hutool.jwt.JWTUtil;
import com.yanluwuyou.entity.User;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;

/**
 * JWT Token服务类
 * 负责Token的生成、解析和验证
 * 使用Hutool JWT库实现JSON Web Token功能
 */
@Component
public class TokenService {

    /**
     * JWT签名密钥（从配置文件读取）
     */
    @Value("${auth.token.secret}")
    private String secret;

    /**
     * Token过期时间（小时），默认72小时（3天）
     */
    @Value("${auth.token.expire-hours:72}")
    private long expireHours;

    /**
     * 为用户创建JWT Token
     * Token中包含用户ID、角色和过期时间等信息
     * 
     * @param user 用户实体对象
     * @return JWT Token字符串
     */
    public String createToken(User user) {
        long expireAt = System.currentTimeMillis() + expireHours * 60 * 60 * 1000;
        Map<String, Object> payload = new HashMap<>();
        payload.put("userId", user.getId());
        payload.put("role", user.getRole());
        payload.put("expireAt", expireAt);
        return JWTUtil.createToken(payload, secret.getBytes(StandardCharsets.UTF_8));
    }

    /**
     * 解析并验证JWT Token
     * 验证签名有效性和Token是否过期
     * 
     * @param token JWT Token字符串
     * @return 认证用户信息对象（包含用户ID和角色）
     * @throws RuntimeException 当Token无效或已过期时抛出异常
     */
    public AuthUser parseToken(String token) {
        byte[] key = secret.getBytes(StandardCharsets.UTF_8);
        if (!JWTUtil.verify(token, key)) {
            throw new RuntimeException("登录已失效，请重新登录");
        }
        JWT jwt = JWTUtil.parseToken(token);
        Object expireAtObj = jwt.getPayload("expireAt");
        if (expireAtObj == null || System.currentTimeMillis() > Long.parseLong(String.valueOf(expireAtObj))) {
            throw new RuntimeException("登录已过期，请重新登录");
        }
        Object userIdObj = jwt.getPayload("userId");
        Object roleObj = jwt.getPayload("role");
        if (userIdObj == null || roleObj == null) {
            throw new RuntimeException("无效的登录凭证");
        }
        Long userId = Long.valueOf(String.valueOf(userIdObj));
        String role = String.valueOf(roleObj);
        return new AuthUser(userId, role);
    }
}
