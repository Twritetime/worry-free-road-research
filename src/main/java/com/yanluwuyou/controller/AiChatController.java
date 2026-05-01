package com.yanluwuyou.controller;

import com.yanluwuyou.auth.AuthGuard;
import com.yanluwuyou.auth.RequireLogin;
import com.yanluwuyou.common.Result;
import com.yanluwuyou.entity.ChatMessage;
import com.yanluwuyou.service.AiChatService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/ai")
public class AiChatController {

    @Autowired
    private AiChatService aiChatService;

    @PostMapping("/chat")
    @RequireLogin
    public Result<ChatMessage> sendMessage(@RequestBody String content) {
        Long userId = AuthGuard.currentUserId();
        ChatMessage message = aiChatService.sendMessage(userId, content);
        return Result.success(message);
    }

    @GetMapping("/history")
    @RequireLogin
    public Result<List<ChatMessage>> getHistory(
            @RequestParam(required = false) String sessionId,
            @RequestParam(defaultValue = "20") int limit) {
        Long userId = AuthGuard.currentUserId();
        List<ChatMessage> history = aiChatService.getHistory(userId, sessionId, limit);
        return Result.success(history);
    }

    @DeleteMapping("/history")
    @RequireLogin
    public Result<Void> clearHistory(@RequestParam String sessionId) {
        Long userId = AuthGuard.currentUserId();
        aiChatService.clearHistory(userId, sessionId);
        return Result.success(null);
    }
}
