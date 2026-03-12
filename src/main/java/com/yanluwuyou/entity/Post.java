package com.yanluwuyou.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 论坛帖子实体类
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("yl_post")
public class Post extends BaseEntity {

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
     * 作者ID
     */
    private Long userId;

    /**
     * 作者昵称 (冗余字段，方便查询)
     */
    private String nickname;

    /**
     * 作者头像 (冗余字段)
     */
    private String avatar;

    /**
     * 浏览量
     */
    private Integer viewCount;

    /**
     * 点赞数
     */
    private Integer likeCount;

    /**
     * 评论数
     */
    private Integer commentCount;

    /**
     * 板块分类: 1:公共课, 2:专业课, 3:复试经验, 4:其他
     */
    private Integer category;

    /**
     * 状态: 0:待审核, 1:正常(已审核), 2:拒绝
     */
    private Integer status;

    /**
     * 是否置顶: 0:否, 1:是
     */
    private Integer isTop;
}
