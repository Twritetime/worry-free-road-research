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
     * 根据订单号获取订单详情
     */
    OrderDTO getByOrderNo(String orderNo);

    /**
     * 获取用户订单列表
     */
    List<OrderDTO> getUserOrders(Long userId);
    
    /**
     * 支付订单
     * @return 支付宝支付表单 HTML
     */
    String payOrder(Long orderId);

    /**
     * 处理支付宝回调通知
     */
    boolean handleAlipayNotify(java.util.Map<String, String> params);

    /**
     * 更新订单状态
     */
    void updateOrderStatus(Long orderId, Integer status);

    /**
     * 检查用户是否购买过某资料
     */
    boolean checkPurchased(Long userId, Long materialId);
}
