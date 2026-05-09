package com.yanluwuyou.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 论坛帖子实体类
 * 对应数据库表 yl_post
 * 存储用户发布的论坛帖子内容，支持分类和审核机制
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("yl_post")
public class Post extends BaseEntity {

    /**
     * 帖子ID（主键，自增）
     */
    @TableId(type = IdType.AUTO)
    private Long id;

    /**
     * 帖子标题
     */
    private String title;

    /**
     * 帖子内容（支持富文本格式）
     */
    private String content;

    /**
     * 作者用户ID（外键，关联sys_user表）
     */
    private Long userId;

    /**
     * 作者昵称（冗余字段，用于列表展示时避免联表查询）
     */
    private String nickname;

    /**
     * 作者头像URL（冗余字段）
     */
    private String avatar;

    /**
     * 浏览量统计
     */
    private Integer viewCount;

    /**
     * 点赞数统计
     */
    private Integer likeCount;

    /**
     * 评论数统计
     */
    private Integer commentCount;

    /**
     * 板块分类: 1-公共课(政治/英语/数学), 2-专业课, 3-复试经验, 4-其他
     */
    private Integer category;

    /**
     * 审核状态: 0-待审核, 1-正常(已审核通过), 2-拒绝(审核未通过)
     */
    private Integer status;

    /**
     * 是否置顶: 0-否, 1-是
     */
    private Integer isTop;

    /**
     * 审核备注（拒绝时填写原因）
     */
    private String auditRemark;
}
