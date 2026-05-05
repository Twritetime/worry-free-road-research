package com.yanluwuyou.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.yanluwuyou.entity.ChatMessage;
import java.util.List;
import java.util.Map;

public interface AiChatService {

    ChatMessage sendMessage(Long userId, String content);

    List<ChatMessage> getHistory(Long userId, String sessionId, int limit);

    void clearHistory(Long userId, String sessionId);

    Page<ChatMessage> adminGetChatList(Integer pageNum, Integer pageSize, Long userId, String keyword);

    List<ChatMessage> adminGetChatSession(String sessionId);

    void adminDeleteSession(String sessionId);

    void adminDeleteMessage(Long id);

    Map<String, Object> adminGetStats();
}
