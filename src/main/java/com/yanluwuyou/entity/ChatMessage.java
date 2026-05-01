package com.yanluwuyou.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@TableName("yl_chat_message")
public class ChatMessage extends BaseEntity {

    @TableId(type = IdType.AUTO)
    private Long id;

    private Long userId;

    private String sessionId;

    private Integer role;

    private String content;

    private LocalDateTime createTime;

    public static final int ROLE_USER = 1;
    public static final int ROLE_ASSISTANT = 2;
    public static final int ROLE_SYSTEM = 0;
}
