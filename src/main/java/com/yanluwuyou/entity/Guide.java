package com.yanluwuyou.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 报考指南实体类
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("yl_guide")
public class Guide extends BaseEntity {

    @TableId(type = IdType.AUTO)
    private Long id;

    /**
     * 标题
     */
    private String title;

    /**
     * 内容
     */
    private String content;

    /**
     * 分类: zhaoshengjianzhang(招生简章), zhuanyemulu(专业目录), kaoshidagang(考试大纲), fushixize(复试细则)
     */
    private String category;

    /**
     * 浏览量
     */
    private Integer viewCount;

    /**
     * 评论数
     */
    private Integer commentCount;

    /**
     * 报考院校
     */
    private String institution;

    /**
     * 报考专业
     */
    private String major;

    /**
     * 状态: 1:已发布, 0:草稿
     */
    private Integer status;
}
