package com.yanluwuyou.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.time.LocalDateTime;

/**
 * AI聊天消息实体类
 * 对应数据库表 yl_chat_message
 * 存储用户与AI助手之间的对话消息记录
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("yl_chat_message")
public class ChatMessage extends BaseEntity {

    /**
     * 消息ID（主键，自增）
     */
    @TableId(type = IdType.AUTO)
    private Long id;

    /**
     * 用户ID（外键，关联sys_user表）
     */
    private Long userId;

    /**
     * 会话ID（用于标识同一次对话）
     */
    private String sessionId;

    /**
     * 消息角色: 0-系统, 1-用户, 2-AI助手
     */
    private Integer role;

    /**
     * 消息内容
     */
    private String content;

    /**
     * 消息创建时间
     */
    private LocalDateTime createTime;

    /**
     * 用户昵称（非数据库字段，用于前端展示）
     */
    @TableField(exist = false)
    private String nickname;

    /**
     * 系统角色常量
     */
    public static final int ROLE_SYSTEM = 0;
    
    /**
     * 用户角色常量
     */
    public static final int ROLE_USER = 1;
    
    /**
     * AI助手角色常量
     */
    public static final int ROLE_ASSISTANT = 2;
}
