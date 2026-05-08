package com.yanluwuyou.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.yanluwuyou.dto.OrderCreateDTO;
import com.yanluwuyou.dto.OrderDTO;
import com.yanluwuyou.entity.Order;

import java.util.List;

/**
 * 订单服务接口
 * 提供订单创建、支付、查询等电商核心业务功能
 * 支持支付宝集成和订单状态管理
 * 继承MyBatis-Plus的IService接口，获得基础CRUD能力
 */
public interface OrderService extends IService<Order> {
    
    /**
     * 创建订单（支持购物车结算和直接购买两种模式）
     * 
     * @param orderCreateDTO 订单创建信息（用户ID、商品列表等）
     * @return 创建成功的订单详情（包含订单项明细）
     */
    OrderDTO createOrder(OrderCreateDTO orderCreateDTO);
    
    /**
     * 根据订单号查询订单详情
     * 
     * @param orderNo 订单编号（唯一业务标识）
     * @return 订单详情对象
     */
    OrderDTO getByOrderNo(String orderNo);

    /**
     * 获取指定用户的订单列表
     *
     * @param userId 用户ID
     * @param status 订单状态筛选（可选，为null时不筛选）
     * @return 该用户的订单列表（按创建时间倒序）
     */
    List<OrderDTO> getUserOrders(Long userId, Integer status);
    
    /**
     * 发起订单支付（调用支付宝接口）
     * 
     * @param orderId 订单ID
     * @return 支付宝支付表单HTML（前端可直接渲染）
     */
    String payOrder(Long orderId);

    /**
     * 处理支付宝异步回调通知
     * 验证签名并更新订单状态为已付款
     * 
     * @param params 支付宝回调参数Map
     * @return true-处理成功, false-处理失败
     */
    boolean handleAlipayNotify(java.util.Map<String, String> params);

    /**
     * 更新订单状态
     * 
     * @param orderId 订单ID
     * @param status 目标状态（0-待付款, 1-已付款, 2-已取消）
     */
    void updateOrderStatus(Long orderId, Integer status);

    /**
     * 检查用户是否已购买过某资料（用于下载权限校验）
     * 
     * @param userId 用户ID
     * @param materialId 资料商品ID
     * @return true-已购买, false-未购买
     */
    boolean checkPurchased(Long userId, Long materialId);
}
