package com.yanluwuyou.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@TableName("yl_user_behavior")
public class UserBehavior extends BaseEntity {

    @TableId(type = IdType.AUTO)
    private Long id;

    private Long userId;

    private Integer behaviorType;

    private Long targetType;

    private Long targetId;

    private String targetTitle;

    private LocalDateTime createTime;

    public static final int TYPE_VIEW = 1;
    public static final int TYPE_FAVORITE = 2;
    public static final int TYPE_PURCHASE = 3;
    public static final int TYPE_SEARCH = 4;
    public static final int TYPECOMMENT = 5;
}
