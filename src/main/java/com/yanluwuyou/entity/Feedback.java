package com.yanluwuyou.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.EqualsAndHashCode;
import java.time.LocalDateTime;

@Data
@EqualsAndHashCode(callSuper = true)
@TableName("feedback")
public class Feedback extends BaseEntity {
    @TableId(type = IdType.AUTO)
    private Long id;

    private Long userId;
    private String content;
    private String reply;
    /**
     * 0: Pending, 1: Replied
     */
    private Integer status;
    private LocalDateTime replyTime;

    @TableField(exist = false)
    private String nickname;
    
    @TableField(exist = false)
    private String avatar;
}
