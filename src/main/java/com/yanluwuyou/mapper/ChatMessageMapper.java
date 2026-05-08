package com.yanluwuyou.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.yanluwuyou.entity.ChatMessage;
import org.apache.ibatis.annotations.Mapper;

/**
 * AI聊天消息数据访问层接口
 * 继承MyBatis-Plus的BaseMapper，提供聊天消息实体的数据库操作
 * 对应数据库表: yl_chat_message
 */
@Mapper
public interface ChatMessageMapper extends BaseMapper<ChatMessage> {
}
