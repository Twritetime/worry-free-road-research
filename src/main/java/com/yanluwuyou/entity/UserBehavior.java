package com.yanluwuyou.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 用户行为记录实体类
 * 对应数据库表 yl_user_behavior
 * 记录用户在系统中的各种操作行为，用于数据分析和个性化推荐
 */
@Data
@TableName("yl_user_behavior")
public class UserBehavior extends BaseEntity {

    /**
     * 行为记录ID（主键，自增）
     */
    @TableId(type = IdType.AUTO)
    private Long id;

    /**
     * 用户ID（外键，关联sys_user表）
     */
    private Long userId;

    /**
     * 行为类型: 1-浏览, 2-收藏, 3-购买, 4-搜索, 5-评论
     */
    private Integer behaviorType;

    /**
     * 目标内容类型（如：1-资料, 2-新闻, 3-指南等）
     */
    private Long targetType;

    /**
     * 目标内容ID
     */
    private Long targetId;

    /**
     * 目标内容标题（冗余存储，便于分析查询）
     */
    private String targetTitle;

    /**
     * 行为发生时间
     */
    private LocalDateTime createTime;

    /**
     * 浏览行为常量
     */
    public static final int TYPE_VIEW = 1;
    
    /**
     * 收藏行为常量
     */
    public static final int TYPE_FAVORITE = 2;
    
    /**
     * 购买行为常量
     */
    public static final int TYPE_PURCHASE = 3;
    
    /**
     * 搜索行为常量
     */
    public static final int TYPE_SEARCH = 4;
    
    /**
     * 评论行为常量
     */
    public static final int TYPECOMMENT = 5;
}
