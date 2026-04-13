package com.yanluwuyou.controller;

import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.yanluwuyou.auth.AuthGuard;
import com.yanluwuyou.auth.RequireLogin;
import com.yanluwuyou.auth.RequireRoles;
import com.yanluwuyou.common.Result;
import com.yanluwuyou.entity.Feedback;
import com.yanluwuyou.entity.User;
import com.yanluwuyou.service.FeedbackService;
import com.yanluwuyou.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/feedback")
@RequireLogin
public class FeedbackController {

    @Autowired
    private FeedbackService feedbackService;

    @Autowired
    private UserService userService;

    @GetMapping("/list")
    public Result<Page<Feedback>> list(@RequestParam(required = false) Long userId,
                                       @RequestParam(required = false) String keyword,
                                       @RequestParam(required = false) Integer status,
                                       @RequestParam(defaultValue = "1") Integer pageNum,
                                       @RequestParam(defaultValue = "10") Integer pageSize) {
        if (!AuthGuard.isAdminOrOperator()) {
            userId = AuthGuard.currentUserId();
            status = null;
        }
        Page<Feedback> page = new Page<>(pageNum, pageSize);
        LambdaQueryWrapper<Feedback> query = new LambdaQueryWrapper<>();
        if (userId != null) {
            query.eq(Feedback::getUserId, userId);
        }
        if (StrUtil.isNotBlank(keyword)) {
            query.like(Feedback::getContent, keyword);
        }
        if (status != null) {
            query.eq(Feedback::getStatus, status);
        }
        query.orderByDesc(Feedback::getCreateTime);
        
        Page<Feedback> result = feedbackService.page(page, query);
        
        // Populate user info
        if (!result.getRecords().isEmpty()) {
            List<Long> userIds = result.getRecords().stream()
                    .map(Feedback::getUserId)
                    .collect(Collectors.toList());
            List<User> users = userService.listByIds(userIds);
            Map<Long, User> userMap = users.stream()
                    .collect(Collectors.toMap(User::getId, u -> u));
            
            for (Feedback fb : result.getRecords()) {
                User u = userMap.get(fb.getUserId());
                if (u != null) {
                    fb.setNickname(u.getNickname() != null ? u.getNickname() : u.getUsername());
                    fb.setAvatar(u.getAvatar());
                }
            }
        }
        return Result.success(result);
    }

    @PostMapping
    public Result<?> save(@RequestBody Feedback feedback) {
        feedback.setUserId(AuthGuard.currentUserId());
        feedback.setStatus(0); // Pending
        feedbackService.save(feedback);
        return Result.success();
    }
    
    // For admin to reply
    @PutMapping("/reply")
    @RequireRoles({User.ROLE_ADMIN, User.ROLE_OPERATOR})
    public Result<?> reply(@RequestBody Feedback feedback) {
        Feedback exist = feedbackService.getById(feedback.getId());
        if (exist == null) {
            return Result.error("反馈不存在");
        }
        exist.setReply(feedback.getReply());
        exist.setStatus(1); // Replied
        exist.setReplyTime(LocalDateTime.now());
        feedbackService.updateById(exist);
        return Result.success();
    }

    @DeleteMapping("/batch")
    @RequireRoles({User.ROLE_ADMIN, User.ROLE_OPERATOR})
    public Result<?> batchDelete(@RequestBody List<Long> ids) {
        if (ids == null || ids.isEmpty()) {
            return Result.error("请选择要删除的反馈");
        }
        feedbackService.removeByIds(ids);
        return Result.success();
    }
}
