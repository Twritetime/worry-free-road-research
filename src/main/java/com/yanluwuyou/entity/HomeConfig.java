package com.yanluwuyou.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 首页配置实体类
 * 对应数据库表 yl_home_config
 * 存储首页轮播图、通知公告、热门推荐、精品推荐等配置项
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("yl_home_config")
public class HomeConfig extends BaseEntity {

    @TableId(type = IdType.AUTO)
    private Long id;

    /**
     * 配置类型: banner(轮播图), notice(通知公告), hot(热门推荐), recommend(精品推荐)
     */
    private String type;

    /**
     * 标题
     */
    private String title;

    /**
     * 图片URL
     */
    @TableField("img_url")
    private String imgUrl;

    /**
     * 跳转链接
     */
    @TableField("link_url")
    private String linkUrl;

    /**
     * 排序顺序
     */
    @TableField("sort_order")
    private Integer sortOrder;

    /**
     * 状态: 1:启用, 0:禁用
     */
    private Integer status;
}