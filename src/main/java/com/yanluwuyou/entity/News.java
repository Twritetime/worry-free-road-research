package com.yanluwuyou.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 新闻资讯实体类
 * 对应数据库表 yl_news
 * 存储系统发布的新闻动态、考研资讯等内容
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("yl_news")
public class News extends BaseEntity {

    /**
     * 新闻ID（主键，自增）
     */
    @TableId(type = IdType.AUTO)
    private Long id;

    /**
     * 新闻标题
     */
    private String title;

    /**
     * 新闻内容（支持富文本/Markdown格式）
     */
    private String content;

    /**
     * 新闻类型: 如政策解读、院校动态、考试通知等
     */
    private String type;

    /**
     * 封面图片URL
     */
    private String coverImg;

    /**
     * 浏览量统计
     */
    private Integer viewCount;

    /**
     * 评论数统计
     */
    private Integer commentCount;

    /**
     * 发布状态: 1-已发布, 0-草稿
     */
    private Integer status;

    /**
     * 排序权重（数值越大越靠前）
     */
    @TableField("sort_order")
    private Integer sortOrder;
}
