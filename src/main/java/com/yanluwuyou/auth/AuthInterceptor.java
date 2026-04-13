package com.yanluwuyou.auth;

import com.yanluwuyou.entity.User;
import com.yanluwuyou.service.UserService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.HandlerInterceptor;

import java.util.Arrays;

@Component
public class AuthInterceptor implements HandlerInterceptor {

    @Autowired
    private TokenService tokenService;

    @Autowired
    private UserService userService;

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) {
        if (!(handler instanceof HandlerMethod handlerMethod)) {
            return true;
        }
        RequireLogin requireLogin = handlerMethod.getMethodAnnotation(RequireLogin.class);
        if (requireLogin == null) {
            requireLogin = handlerMethod.getBeanType().getAnnotation(RequireLogin.class);
        }
        RequireRoles requireRoles = handlerMethod.getMethodAnnotation(RequireRoles.class);
        if (requireRoles == null) {
            requireRoles = handlerMethod.getBeanType().getAnnotation(RequireRoles.class);
        }
        if (requireLogin == null && requireRoles == null) {
            return true;
        }

        String token = resolveToken(request);
        if (token == null || token.isBlank()) {
            throw new RuntimeException("请先登录");
        }
        AuthUser authUser = tokenService.parseToken(token);
        User user = userService.getById(authUser.getUserId());
        if (user == null) {
            throw new RuntimeException("账号不存在");
        }
        if (user.getStatus() != null && user.getStatus() == 0) {
            throw new RuntimeException("账号已被禁用，请联系管理员");
        }
        authUser.setRole(user.getRole());
        AuthContext.set(authUser);

        if (requireRoles != null && Arrays.stream(requireRoles.value()).noneMatch(role -> role.equals(authUser.getRole()))) {
            throw new RuntimeException("无权限执行该操作");
        }
        return true;
    }

    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) {
        AuthContext.clear();
    }

    private String resolveToken(HttpServletRequest request) {
        String authorization = request.getHeader("Authorization");
        if (authorization != null && authorization.startsWith("Bearer ")) {
            return authorization.substring(7);
        }
        return request.getHeader("token");
    }
}
