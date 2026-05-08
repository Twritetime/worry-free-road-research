package com.yanluwuyou.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 收藏实体类
 * 对应数据库表 favorite
 * 存储用户收藏的内容，支持新闻、指南、资料等多种类型
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("favorite")
public class Favorite extends BaseEntity {
    
    /**
     * 收藏ID（主键，自增）
     */
    @TableId(type = IdType.AUTO)
    private Long id;
    
    /**
     * 用户ID（外键，关联sys_user表）
     */
    private Long userId;
    
    /**
     * 目标内容ID
     */
    private Long targetId;
    
    /**
     * 目标类型: 1-新闻(News), 2-指南(Guide), 3-资料(Material)
     */
    private Integer targetType;
    
    /**
     * 目标标题（冗余存储，用于列表展示时避免联表查询）
     */
    private String targetTitle;
    
    /**
     * 目标封面图URL（冗余存储，用于列表展示）
     */
    private String targetCover;
}
