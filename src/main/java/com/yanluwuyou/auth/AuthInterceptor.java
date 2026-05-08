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

/**
 * 认证拦截器
 * 实现基于JWT Token的用户认证和角色权限校验
 * 通过@RequireLogin和@RequireRoles注解控制接口访问权限
 */
@Component
public class AuthInterceptor implements HandlerInterceptor {

    @Autowired
    private TokenService tokenService;

    @Autowired
    private UserService userService;

    /**
     * 请求预处理方法（在Controller方法执行前调用）
     * 检查接口是否需要登录认证，验证Token有效性，校验用户权限
     * 
     * @param request HTTP请求对象
     * @param response HTTP响应对象
     * @param handler 处理器（Controller方法）
     * @return true-放行, false-拦截（抛出异常时由全局异常处理器处理）
     * @throws RuntimeException 当未登录、Token无效、账号被禁用或无权限时抛出异常
     */
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

    /**
     * 请求完成后的清理方法
     * 清除ThreadLocal中的用户上下文，防止内存泄漏
     */
    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) {
        AuthContext.clear();
    }

    /**
     * 从HTTP请求中提取Token
     * 支持两种方式: Authorization头（Bearer格式）或自定义token头
     * 
     * @param request HTTP请求对象
     * @return Token字符串，如果不存在则返回null
     */
    private String resolveToken(HttpServletRequest request) {
        String authorization = request.getHeader("Authorization");
        if (authorization != null && authorization.startsWith("Bearer ")) {
            return authorization.substring(7);
        }
        return request.getHeader("token");
    }
}
