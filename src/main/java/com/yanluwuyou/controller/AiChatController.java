package com.yanluwuyou.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.yanluwuyou.auth.AuthGuard;
import com.yanluwuyou.auth.RequireLogin;
import com.yanluwuyou.auth.RequireRoles;
import com.yanluwuyou.common.Result;
import com.yanluwuyou.entity.ChatMessage;
import com.yanluwuyou.service.AiChatService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

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

    @GetMapping("/admin/list")
    @RequireRoles("ADMIN")
    public Result<Page<ChatMessage>> adminGetChatList(
            @RequestParam(defaultValue = "1") Integer pageNum,
            @RequestParam(defaultValue = "10") Integer pageSize,
            @RequestParam(required = false) Long userId,
            @RequestParam(required = false) String keyword) {
        Page<ChatMessage> page = aiChatService.adminGetChatList(pageNum, pageSize, userId, keyword);
        return Result.success(page);
    }

    @GetMapping("/admin/session")
    @RequireRoles("ADMIN")
    public Result<List<ChatMessage>> adminGetChatSession(@RequestParam String sessionId) {
        List<ChatMessage> messages = aiChatService.adminGetChatSession(sessionId);
        return Result.success(messages);
    }

    @DeleteMapping("/admin/session")
    @RequireRoles("ADMIN")
    public Result<Void> adminDeleteSession(@RequestParam String sessionId) {
        aiChatService.adminDeleteSession(sessionId);
        return Result.success(null);
    }

    @DeleteMapping("/admin/message")
    @RequireRoles("ADMIN")
    public Result<Void> adminDeleteMessage(@RequestParam Long id) {
        aiChatService.adminDeleteMessage(id);
        return Result.success(null);
    }

    @GetMapping("/admin/stats")
    @RequireRoles("ADMIN")
    public Result<Map<String, Object>> adminGetStats() {
        Map<String, Object> stats = aiChatService.adminGetStats();
        return Result.success(stats);
    }
}
