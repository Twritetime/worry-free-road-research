package com.yanluwuyou.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.yanluwuyou.auth.RequireRoles;
import com.yanluwuyou.common.Result;
import com.yanluwuyou.entity.*;
import com.yanluwuyou.service.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

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

    @Autowired
    private CommentService commentService;

    @Autowired
    private NewsService newsService;

    @Autowired
    private FavoriteService favoriteService;

    @GetMapping("/stats")
    public Result<Map<String, Object>> getStats() {
        Map<String, Object> stats = new HashMap<>();
        stats.put("userCount", userService.count());
        stats.put("postCount", postService.count());
        stats.put("materialCount", materialService.count());
        stats.put("orderCount", orderService.count());
        stats.put("pendingFeedback", feedbackService.count(new LambdaQueryWrapper<Feedback>().eq(Feedback::getStatus, 0)));
        List<com.yanluwuyou.entity.Order> paidOrders = orderService.list(new LambdaQueryWrapper<com.yanluwuyou.entity.Order>()
                .in(com.yanluwuyou.entity.Order::getStatus, 1, 3));
        BigDecimal totalSales = paidOrders.stream()
                .map(com.yanluwuyou.entity.Order::getTotalAmount)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        stats.put("salesAmount", totalSales);
        return Result.success(stats);
    }

    @GetMapping("/sales-trend")
    public Result<List<Map<String, Object>>> getSalesTrend(
            @RequestParam(defaultValue = "7") int days) {
        LocalDateTime startDate = LocalDateTime.now().minusDays(days);
        List<com.yanluwuyou.entity.Order> orders = orderService.list(
                new LambdaQueryWrapper<com.yanluwuyou.entity.Order>()
                        .in(com.yanluwuyou.entity.Order::getStatus, 1, 3)
                        .ge(com.yanluwuyou.entity.Order::getCreateTime, startDate)
                        .orderByAsc(com.yanluwuyou.entity.Order::getCreateTime));

        Map<String, BigDecimal> dailySales = new LinkedHashMap<>();
        for (int i = days - 1; i >= 0; i--) {
            LocalDateTime date = LocalDateTime.now().minusDays(i);
            String dateKey = date.toLocalDate().toString();
            dailySales.put(dateKey, BigDecimal.ZERO);
        }

        for (com.yanluwuyou.entity.Order order : orders) {
            if (order.getCreateTime() != null) {
                String dateKey = order.getCreateTime().toLocalDate().toString();
                if (dailySales.containsKey(dateKey)) {
                    dailySales.merge(dateKey, order.getTotalAmount(), BigDecimal::add);
                }
            }
        }

        List<Map<String, Object>> trend = new ArrayList<>();
        dailySales.forEach((date, amount) -> {
            Map<String, Object> item = new HashMap<>();
            item.put("date", date);
            item.put("amount", amount);
            trend.add(item);
        });

        return Result.success(trend);
    }

    @GetMapping("/material-sales-ranking")
    public Result<List<Map<String, Object>>> getMaterialSalesRanking(
            @RequestParam(defaultValue = "10") int limit) {
        LambdaQueryWrapper<OrderItem> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.select(OrderItem::getMaterialId, OrderItem::getMaterialName)
                .apply("SUM(quantity) as totalQuantity")
                .groupBy(OrderItem::getMaterialId, OrderItem::getMaterialName)
                .orderByDesc(OrderItem::getQuantity)
                .last("LIMIT " + limit);

        List<OrderItem> items = orderItemService.list(queryWrapper);
        List<Map<String, Object>> ranking = new ArrayList<>();
        int rank = 1;
        for (OrderItem item : items) {
            Map<String, Object> entry = new HashMap<>();
            entry.put("rank", rank++);
            entry.put("materialId", item.getMaterialId());
            entry.put("materialName", item.getMaterialName());
            entry.put("salesCount", item.getQuantity() != null ? item.getQuantity() : 0);
            ranking.add(entry);
        }
        return Result.success(ranking);
    }

    @GetMapping("/category-sales")
    public Result<List<Map<String, Object>>> getCategorySales() {
        List<Material> materials = materialService.list();
        Map<String, Integer> categorySales = new HashMap<>();
        for (Material material : materials) {
            if (material.getSales() != null && material.getCategory() != null) {
                categorySales.merge(material.getCategory(), material.getSales(), Integer::sum);
            }
        }

        List<Map<String, Object>> result = new ArrayList<>();
        categorySales.forEach((category, sales) -> {
            Map<String, Object> entry = new HashMap<>();
            entry.put("category", category);
            entry.put("sales", sales);
            result.add(entry);
        });

        result.sort((a, b) -> ((Integer) b.get("sales")).compareTo((Integer) a.get("sales")));
        return Result.success(result);
    }

    @GetMapping("/user-growth")
    public Result<List<Map<String, Object>>> getUserGrowth(
            @RequestParam(defaultValue = "7") int days) {
        LocalDateTime startDate = LocalDateTime.now().minusDays(days);
        List<User> users = userService.list(
                new LambdaQueryWrapper<User>()
                        .ge(User::getCreateTime, startDate)
                        .orderByAsc(User::getCreateTime));

        Map<String, Integer> dailyUsers = new LinkedHashMap<>();
        for (int i = days - 1; i >= 0; i--) {
            LocalDateTime date = LocalDateTime.now().minusDays(i);
            String dateKey = date.toLocalDate().toString();
            dailyUsers.put(dateKey, 0);
        }

        for (User user : users) {
            if (user.getCreateTime() != null) {
                String dateKey = user.getCreateTime().toLocalDate().toString();
                if (dailyUsers.containsKey(dateKey)) {
                    dailyUsers.merge(dateKey, 1, Integer::sum);
                }
            }
        }

        int cumulative = 0;
        List<Map<String, Object>> growth = new ArrayList<>();
        for (Map.Entry<String, Integer> entry : dailyUsers.entrySet()) {
            cumulative += entry.getValue();
            Map<String, Object> item = new HashMap<>();
            item.put("date", entry.getKey());
            item.put("newUsers", entry.getValue());
            item.put("totalUsers", cumulative);
            growth.add(item);
        }

        return Result.success(growth);
    }

    @GetMapping("/activity-stats")
    public Result<Map<String, Object>> getActivityStats() {
        Map<String, Object> stats = new HashMap<>();
        LocalDateTime today = LocalDateTime.now().withHour(0).withMinute(0).withSecond(0);

        stats.put("todayPosts", postService.count(new LambdaQueryWrapper<Post>()
                .ge(Post::getCreateTime, today)));
        stats.put("todayComments", commentService.count(new LambdaQueryWrapper<Comment>()
                .ge(Comment::getCreateTime, today)));
        stats.put("todayOrders", orderService.count(new LambdaQueryWrapper<com.yanluwuyou.entity.Order>()
                .ge(com.yanluwuyou.entity.Order::getCreateTime, today)));
        stats.put("todayNewUsers", userService.count(new LambdaQueryWrapper<User>()
                .ge(User::getCreateTime, today)));
        stats.put("totalFavorites", favoriteService.count());
        stats.put("totalComments", commentService.count());

        return Result.success(stats);
    }

    @GetMapping("/hot-content")
    public Result<List<Map<String, Object>>> getHotContent(
            @RequestParam(defaultValue = "5") int limit) {
        List<Map<String, Object>> hotContent = new ArrayList<>();

        List<Post> topPosts = postService.list(
                new LambdaQueryWrapper<Post>()
                        .orderByDesc(Post::getViewCount)
                        .last("LIMIT " + limit));
        for (int i = 0; i < topPosts.size(); i++) {
            Post post = topPosts.get(i);
            Map<String, Object> item = new HashMap<>();
            item.put("type", "post");
            item.put("id", post.getId());
            item.put("title", post.getTitle());
            item.put("viewCount", post.getViewCount() != null ? post.getViewCount() : 0);
            item.put("createTime", post.getCreateTime());
            hotContent.add(item);
        }

        List<News> topNews = newsService.list(
                new LambdaQueryWrapper<News>()
                        .orderByDesc(News::getViewCount)
                        .last("LIMIT " + limit));
        for (News news : topNews) {
            Map<String, Object> item = new HashMap<>();
            item.put("type", "news");
            item.put("id", news.getId());
            item.put("title", news.getTitle());
            item.put("viewCount", news.getViewCount() != null ? news.getViewCount() : 0);
            item.put("createTime", news.getCreateTime());
            hotContent.add(item);
        }

        hotContent.sort((a, b) -> ((Integer) b.get("viewCount")).compareTo((Integer) a.get("viewCount")));
        return Result.success(hotContent.subList(0, Math.min(hotContent.size(), limit)));
    }
}
