package com.yanluwuyou.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.yanluwuyou.dto.OrderCreateDTO;
import com.yanluwuyou.dto.OrderDTO;
import com.yanluwuyou.entity.Order;

import java.util.List;

/**
 * 订单服务接口
 */
public interface OrderService extends IService<Order> {
    
    /**
     * 创建订单
     */
    OrderDTO createOrder(OrderCreateDTO orderCreateDTO);
    
    /**
     * 获取用户订单列表
     */
    List<OrderDTO> getUserOrders(Long userId);
    
    /**
     * 支付订单
     */
    void payOrder(Long orderId);

    /**
     * 更新订单状态
     */
    void updateOrderStatus(Long orderId, Integer status);

    /**
     * 检查用户是否购买过某资料
     */
    boolean checkPurchased(Long userId, Long materialId);
}
