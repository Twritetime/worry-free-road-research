package com.yanluwuyou.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.List;

/**
 * 评论实体类
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("yl_comment")
public class Comment extends BaseEntity {

    @TableId(type = IdType.AUTO)
    private Long id;

    /**
     * 帖子ID (保留，兼容旧代码，建议迁移使用targetId)
     */
    private Long postId;

    /**
     * 目标类型: 1-帖子, 2-指南
     */
    private Integer targetType;

    /**
     * 目标ID
     */
    private Long targetId;

    /**
     * 父评论ID
     */
    private Long parentId;

    /**
     * 内容
     */
    private String content;

    /**
     * 用户ID
     */
    private Long userId;

    /**
     * 用户昵称 (冗余)
     */
    private String nickname;

    /**
     * 用户头像 (冗余)
     */
    private String avatar;

    /**
     * 被回复用户ID
     */
    private Long replyToUserId;

    /**
     * 被回复用户昵称
     */
    private String replyToNickname;

    /**
     * 子评论列表 (非数据库字段)
     */
    @TableField(exist = false)
    private List<Comment> children;
}
