package com.yanluwuyou.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.EqualsAndHashCode;
import java.time.LocalDateTime;

/**
 * 用户反馈实体类
 * 对应数据库表 feedback
 * 存储用户的意见反馈、问题报告等，管理员可进行回复
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("feedback")
public class Feedback extends BaseEntity {
    
    /**
     * 反馈ID（主键，自增）
     */
    @TableId(type = IdType.AUTO)
    private Long id;

    /**
     * 用户ID（外键，关联sys_user表）
     */
    private Long userId;

    /**
     * 反馈类型: bug(错误报告), suggestion(建议), other(其他)
     */
    private String type;

    /**
     * 联系方式（邮箱或手机号）
     */
    private String contact;

    /**
     * 反馈内容详情
     */
    private String content;

    /**
     * 截图或附件URL
     */
    private String coverImg;

    /**
     * 管理员回复内容
     */
    private String reply;

    /**
     * 处理状态: 0-待处理, 1-已回复, 2-已关闭
     */
    private Integer status;

    /**
     * 回复时间
     */
    private LocalDateTime replyTime;

    /**
     * 用户昵称（非数据库字段，用于前端展示）
     */
    @TableField(exist = false)
    private String nickname;

    /**
     * 用户头像（非数据库字段，用于前端展示）
     */
    @TableField(exist = false)
    private String avatar;
}
