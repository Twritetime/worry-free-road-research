package com.yanluwuyou.auth;

import cn.hutool.jwt.JWT;
import cn.hutool.jwt.JWTUtil;
import com.yanluwuyou.entity.User;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;

@Component
public class TokenService {

    @Value("${auth.token.secret}")
    private String secret;

    @Value("${auth.token.expire-hours:72}")
    private long expireHours;

    public String createToken(User user) {
        long expireAt = System.currentTimeMillis() + expireHours * 60 * 60 * 1000;
        Map<String, Object> payload = new HashMap<>();
        payload.put("userId", user.getId());
        payload.put("role", user.getRole());
        payload.put("expireAt", expireAt);
        return JWTUtil.createToken(payload, secret.getBytes(StandardCharsets.UTF_8));
    }

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
