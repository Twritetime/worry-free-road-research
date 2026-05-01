package com.yanluwuyou.service;

import com.yanluwuyou.entity.ChatMessage;
import java.util.List;

public interface AiChatService {

    ChatMessage sendMessage(Long userId, String content);

    List<ChatMessage> getHistory(Long userId, String sessionId, int limit);

    void clearHistory(Long userId, String sessionId);
}
