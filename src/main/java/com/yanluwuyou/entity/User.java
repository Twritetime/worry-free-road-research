package com.yanluwuyou.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.time.LocalDateTime;

/**
 * 用户实体类
 * 对应数据库表 sys_user
 * 存储系统用户的基本信息，包括学生、运营人员和管理员
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("sys_user")
public class User extends BaseEntity {

    /**
     * 学生角色常量
     */
    public static final String ROLE_STUDENT = "STUDENT";
    
    /**
     * 运营人员角色常量
     */
    public static final String ROLE_OPERATOR = "OPERATOR";
    
    /**
     * 管理员角色常量
     */
    public static final String ROLE_ADMIN = "ADMIN";

    /**
     * 用户ID（主键，自增）
     */
    @TableId(type = IdType.AUTO)
    private Long id;

    /**
     * 用户名（登录账号）
     */
    private String username;

    /**
     * 密码（加密存储）
     */
    private String password;

    /**
     * 昵称（显示名称）
     */
    private String nickname;

    /**
     * 邮箱地址
     */
    private String email;

    /**
     * 手机号码
     */
    private String phone;

    /**
     * 头像URL路径
     */
    private String avatar;

    /**
     * 用户角色: STUDENT(学生), OPERATOR(运营), ADMIN(管理员)
     */
    private String role;

    /**
     * 账户状态: 0-禁用, 1-正常
     */
    private Integer status;

    /**
     * 最后登录时间
     */
    private LocalDateTime lastLoginTime;

    /**
     * 用户Token（非数据库字段，用于返回给前端）
     */
    @TableField(exist = false)
    private String token;
}
