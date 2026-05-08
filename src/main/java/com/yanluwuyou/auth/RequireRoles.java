package com.yanluwuyou.auth;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 需要角色注解
 * 标记在Controller类或方法上，表示该接口需要特定角色才能访问
 * 可应用于类级别（对整个Controller生效）或方法级别（对单个接口生效）
 */
@Target({ElementType.TYPE, ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
public @interface RequireRoles {
    /**
     * 允许访问的角色列表
     * 
     * @return 角色数组
     */
    String[] value();
}
