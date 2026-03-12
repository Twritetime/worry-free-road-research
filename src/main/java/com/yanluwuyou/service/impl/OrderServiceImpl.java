package com.yanluwuyou.service.impl;

import cn.hutool.core.date.DateUtil;
import cn.hutool.core.util.IdUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.yanluwuyou.dto.OrderCreateDTO;
import com.yanluwuyou.dto.OrderDTO;
import com.yanluwuyou.entity.CartItem;
import com.yanluwuyou.entity.Material;
import com.yanluwuyou.entity.Order;
import com.yanluwuyou.entity.OrderItem;
import com.yanluwuyou.mapper.OrderItemMapper;
import com.yanluwuyou.mapper.OrderMapper;
import com.yanluwuyou.service.CartItemService;
import com.yanluwuyou.service.MaterialService;
import com.yanluwuyou.service.OrderService;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 订单服务实现类
 */
@Service
public class OrderServiceImpl extends ServiceImpl<OrderMapper, Order> implements OrderService {

    @Autowired
    private OrderItemMapper orderItemMapper;
    
    @Autowired
    private MaterialService materialService;
    
    @Autowired
    private CartItemService cartItemService;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public OrderDTO createOrder(OrderCreateDTO orderCreateDTO) {
        Order order = new Order();
        order.setUserId(orderCreateDTO.getUserId());
        order.setOrderNo(DateUtil.format(new Date(), "yyyyMMddHHmmss") + IdUtil.getSnowflakeNextId());
        order.setStatus(0); // 待付款
        
        List<OrderItem> orderItems = new ArrayList<>();
        BigDecimal totalAmount = BigDecimal.ZERO;
        
        if (orderCreateDTO.getCartItemIds() != null && !orderCreateDTO.getCartItemIds().isEmpty()) {
            // 从购物车创建
            List<CartItem> cartItems = cartItemService.listByIds(orderCreateDTO.getCartItemIds());
            for (CartItem cartItem : cartItems) {
                Material material = materialService.getById(cartItem.getMaterialId());
                OrderItem item = new OrderItem();
                item.setMaterialId(material.getId());
                item.setMaterialName(material.getName());
                item.setPrice(material.getPrice());
                item.setQuantity(cartItem.getQuantity());
                orderItems.add(item);
                
                totalAmount = totalAmount.add(material.getPrice().multiply(BigDecimal.valueOf(cartItem.getQuantity())));
            }
            // 清空购物车
            cartItemService.removeByIds(orderCreateDTO.getCartItemIds());
        } else if (orderCreateDTO.getMaterialId() != null) {
            // 直接购买
            Material material = materialService.getById(orderCreateDTO.getMaterialId());
            OrderItem item = new OrderItem();
            item.setMaterialId(material.getId());
            item.setMaterialName(material.getName());
            item.setPrice(material.getPrice());
            item.setQuantity(orderCreateDTO.getQuantity());
            orderItems.add(item);
            
            totalAmount = totalAmount.add(material.getPrice().multiply(BigDecimal.valueOf(orderCreateDTO.getQuantity())));
        }
        
        order.setTotalAmount(totalAmount);
        this.save(order);
        
        for (OrderItem item : orderItems) {
            item.setOrderId(order.getId());
            orderItemMapper.insert(item);
        }
        
        OrderDTO orderDTO = new OrderDTO();
        BeanUtils.copyProperties(order, orderDTO);
        orderDTO.setItems(orderItems);
        return orderDTO;
    }

    @Override
    public List<OrderDTO> getUserOrders(Long userId) {
        List<Order> orders = this.list(new LambdaQueryWrapper<Order>()
                .eq(Order::getUserId, userId)
                .orderByDesc(Order::getCreateTime));
        
        return orders.stream().map(order -> {
            OrderDTO dto = new OrderDTO();
            BeanUtils.copyProperties(order, dto);
            List<OrderItem> items = orderItemMapper.selectList(new LambdaQueryWrapper<OrderItem>()
                    .eq(OrderItem::getOrderId, order.getId()));
            dto.setItems(items);
            return dto;
        }).collect(Collectors.toList());
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void payOrder(Long orderId) {
        Order order = this.getById(orderId);
        if (order != null && order.getStatus() == 0) {
            updateOrderStatus(orderId, 1);
        }
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void updateOrderStatus(Long orderId, Integer status) {
        Order order = this.getById(orderId);
        if (order == null) {
            throw new RuntimeException("订单不存在");
        }
        
        Integer oldStatus = order.getStatus();
        if (oldStatus.equals(status)) {
            return;
        }

        order.setStatus(status);
        this.updateById(order);

        // 如果变为已付款，增加销量
        if (status == 1 && oldStatus != 1) {
            handleOrderPaid(orderId);
        }
    }

    private void handleOrderPaid(Long orderId) {
        // 更新销量和库存
        List<OrderItem> items = orderItemMapper.selectList(new LambdaQueryWrapper<OrderItem>()
                .eq(OrderItem::getOrderId, orderId));
        for (OrderItem item : items) {
            Material material = materialService.getById(item.getMaterialId());
            if (material != null) {
                material.setSales((material.getSales() == null ? 0 : material.getSales()) + item.getQuantity());
                material.setStock(Math.max(0, material.getStock() - item.getQuantity()));
                materialService.updateById(material);
            }
        }
    }

    @Override
    public boolean checkPurchased(Long userId, Long materialId) {
        // 查询用户的所有已付款订单
        List<Order> paidOrders = this.list(new LambdaQueryWrapper<Order>()
                .eq(Order::getUserId, userId)
                .eq(Order::getStatus, 1)); // 1: 已付款

        if (paidOrders.isEmpty()) {
            return false;
        }

        List<Long> orderIds = paidOrders.stream().map(Order::getId).collect(Collectors.toList());

        // 查询这些订单中是否包含该资料
        Long count = orderItemMapper.selectCount(new LambdaQueryWrapper<OrderItem>()
                .in(OrderItem::getOrderId, orderIds)
                .eq(OrderItem::getMaterialId, materialId));
        
        return count > 0;
    }
}
