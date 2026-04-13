package com.yanluwuyou.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.yanluwuyou.auth.RequireRoles;
import com.yanluwuyou.common.Result;
import com.yanluwuyou.entity.Feedback;
import com.yanluwuyou.entity.OrderItem;
import com.yanluwuyou.entity.User;
import com.yanluwuyou.service.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 仪表盘统计控制器
 */
@RestController
@RequestMapping("/dashboard")
@RequireRoles({User.ROLE_ADMIN, User.ROLE_OPERATOR})
public class DashboardController {

    @Autowired
    private UserService userService;

    @Autowired
    private OrderService orderService;

    @Autowired
    private OrderItemService orderItemService;

    @Autowired
    private PostService postService;

    @Autowired
    private FeedbackService feedbackService;

    @Autowired
    private MaterialService materialService;

    @GetMapping("/stats")
    public Result<Map<String, Object>> getStats() {
        Map<String, Object> stats = new HashMap<>();

        // 1. 统计用户总数（全表 count）
        stats.put("userCount", userService.count());

        // 2. 统计销售总额（核心业务逻辑）
        // 过滤出状态为 1(已支付) 或 3(已完成) 的订单，然后使用 Stream API 对总金额(totalAmount)进行累加（reduce）
        List<com.yanluwuyou.entity.Order> paidOrders = orderService.list(new LambdaQueryWrapper<com.yanluwuyou.entity.Order>()
                .in(com.yanluwuyou.entity.Order::getStatus, 1, 3));
        BigDecimal totalSales = paidOrders.stream()
                .map(com.yanluwuyou.entity.Order::getTotalAmount)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        stats.put("salesAmount", totalSales);

        // 3. 帖子总数
        stats.put("postCount", postService.count());

        // 4. 待处理反馈数
        stats.put("pendingFeedback", feedbackService.count(new LambdaQueryWrapper<Feedback>().eq(Feedback::getStatus, 0)));

        return Result.success(stats);
    }
}
