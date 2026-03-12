package com.yanluwuyou.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.yanluwuyou.common.Result;
import com.yanluwuyou.entity.Feedback;
import com.yanluwuyou.entity.OrderItem;
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

        // 1. User Count
        stats.put("userCount", userService.count());

        // 2. Sales Amount (Total sales from paid orders)
        // This is a bit complex, let's just sum up order totalAmount where status = 1 or 3
        // Or simpler: Material sales sum * price (approx) or Order totalAmount
        // Let's use Order totalAmount for paid orders
        // Note: For large datasets this should be cached or pre-calculated.
        // For demo, we iterate or use custom SQL. Since I can't write XML mapper easily, I'll fetch paid orders.
        // Wait, fetching all orders is bad. Let's use Material sales field sum as proxy or just count orders.
        // Let's use OrderService count for now to avoid OOM, but user wants "Sales Amount".
        // Let's try to get it via service.
        // Actually, let's just use a simple estimation or list loop if data is small. 
        // Assuming data is small for this project.
        List<com.yanluwuyou.entity.Order> paidOrders = orderService.list(new LambdaQueryWrapper<com.yanluwuyou.entity.Order>()
                .in(com.yanluwuyou.entity.Order::getStatus, 1, 3));
        BigDecimal totalSales = paidOrders.stream()
                .map(com.yanluwuyou.entity.Order::getTotalAmount)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        stats.put("salesAmount", totalSales);

        // 3. Post Count
        stats.put("postCount", postService.count());

        // 4. Pending Feedback
        stats.put("pendingFeedback", feedbackService.count(new LambdaQueryWrapper<Feedback>().eq(Feedback::getStatus, 0)));

        return Result.success(stats);
    }
}
