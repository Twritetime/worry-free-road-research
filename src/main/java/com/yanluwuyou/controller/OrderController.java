package com.yanluwuyou.controller;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.yanluwuyou.common.Result;
import com.yanluwuyou.dto.OrderCreateDTO;
import com.yanluwuyou.dto.OrderDTO;
import com.yanluwuyou.entity.Order;
import com.yanluwuyou.entity.OrderItem;
import com.yanluwuyou.entity.User;
import com.yanluwuyou.config.AlipayConfig;
import com.yanluwuyou.service.OrderItemService;
import com.yanluwuyou.service.OrderService;
import com.yanluwuyou.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import jakarta.servlet.http.HttpServletResponse;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 订单控制器
 */
@RestController
@RequestMapping("/order")
public class OrderController {

    @Autowired
    private OrderService orderService;

    @Autowired
    private OrderItemService orderItemService;

    @Autowired
    private UserService userService;

    @Autowired
    private AlipayConfig alipayConfig;

    /**
     * 创建订单
     */
    @PostMapping("/create")
    public Result<OrderDTO> create(@RequestBody OrderCreateDTO orderCreateDTO) {
        return Result.success(orderService.createOrder(orderCreateDTO));
    }

    @GetMapping("/by-no/{orderNo}")
    public Result<OrderDTO> getByOrderNo(@PathVariable String orderNo) {
        return Result.success(orderService.getByOrderNo(orderNo));
    }

    /**
     * 获取用户订单
     */
    @GetMapping("/list")
    public Result<List<OrderDTO>> list(@RequestParam Long userId) {
        return Result.success(orderService.getUserOrders(userId));
    }

    /**
     * 获取所有订单列表 (管理端)
     */
    @GetMapping("/list-all")
    public Result<Page<OrderDTO>> listAll(@RequestParam(defaultValue = "1") Integer pageNum,
                                       @RequestParam(defaultValue = "10") Integer pageSize,
                                       @RequestParam(required = false) String keyword,
                                       @RequestParam(required = false) Integer status) {
        Page<Order> page = new Page<>(pageNum, pageSize);
        LambdaQueryWrapper<Order> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.like(StrUtil.isNotBlank(keyword), Order::getOrderNo, keyword);
        if (status != null) {
            queryWrapper.eq(Order::getStatus, status);
        }
        queryWrapper.orderByDesc(Order::getCreateTime);
        
        Page<Order> orderPage = orderService.page(page, queryWrapper);
        
        // 转换为DTO并填充items
        Page<OrderDTO> dtoPage = new Page<>(pageNum, pageSize, orderPage.getTotal());
        List<OrderDTO> dtoList = orderPage.getRecords().stream().map(order -> {
            OrderDTO dto = new OrderDTO();
            BeanUtil.copyProperties(order, dto);
            // 查询订单项
            List<OrderItem> items = orderItemService.list(new LambdaQueryWrapper<OrderItem>()
                    .eq(OrderItem::getOrderId, order.getId()));
            dto.setItems(items);
            // 查询用户
            User user = userService.getById(order.getUserId());
            if (user != null) {
                dto.setUsername(user.getNickname());
            } else {
                dto.setUsername("未知用户");
            }
            return dto;
        }).collect(Collectors.toList());
        
        dtoPage.setRecords(dtoList);
        return Result.success(dtoPage);
    }

    /**
     * 支付订单
     */
    @PostMapping("/pay/{id}")
    public Result<String> pay(@PathVariable Long id) {
        try {
            return Result.success(orderService.payOrder(id));
        } catch (RuntimeException e) {
            if ("订单已支付".equals(e.getMessage())) {
                // 如果是“订单已支付”异常，我们手动触发一次状态更新（这里不依赖事务回滚）
                Order order = orderService.getById(id);
                if (order != null && order.getStatus() == 0) {
                    orderService.updateOrderStatus(id, 1);
                }
            }
            throw e; // 继续抛出让全局处理器返回错误信息给前端，前端逻辑依赖这个错误文本
        }
    }

    /**
     * 支付宝异步回调通知
     */
    @PostMapping("/alipay-notify")
    public String alipayNotify(@RequestParam java.util.Map<String, String> params) {
        boolean verified = orderService.handleAlipayNotify(params);
        return verified ? "success" : "fail";
    }

    @GetMapping("/alipay-return")
    public void alipayReturn(@RequestParam java.util.Map<String, String> params, HttpServletResponse response) {
        orderService.handleAlipayNotify(params);
        String orderNo = params.get("out_trade_no");
        String target = alipayConfig.getFrontendReturnUrl();
        if (orderNo != null && !orderNo.isEmpty()) {
            String encoded = URLEncoder.encode(orderNo, StandardCharsets.UTF_8);
            if (target.contains("?")) {
                target = target + "&out_trade_no=" + encoded;
            } else {
                target = target + "?out_trade_no=" + encoded;
            }
        }
        try {
            response.sendRedirect(target);
        } catch (Exception ignored) {
        }
    }

    /**
     * 更新订单状态 (管理端)
     */
    @PutMapping("/{id}/status/{status}")
    public Result<?> updateStatus(@PathVariable Long id, @PathVariable Integer status) {
        try {
            orderService.updateOrderStatus(id, status);
            return Result.success();
        } catch (Exception e) {
            return Result.error(e.getMessage());
        }
    }

    /**
     * 取消订单
     */
    @PostMapping("/cancel/{id}")
    public Result<?> cancel(@PathVariable Long id) {
        Order order = orderService.getById(id);
        if (order == null) {
            return Result.error("订单不存在");
        }
        // 只有待付款状态可以取消
        if (order.getStatus() != 0) {
            return Result.error("当前状态无法取消");
        }
        order.setStatus(2); // 2: 已取消
        orderService.updateById(order);
        return Result.success();
    }

    /**
     * 检查是否已购买
     */
    @GetMapping("/check-purchased")
    public Result<Boolean> checkPurchased(@RequestParam Long userId, @RequestParam Long materialId) {
        return Result.success(orderService.checkPurchased(userId, materialId));
    }
}
