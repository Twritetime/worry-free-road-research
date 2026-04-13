package com.yanluwuyou.service.impl;

import cn.hutool.core.date.DateUtil;
import cn.hutool.core.util.IdUtil;
import com.alipay.api.AlipayApiException;
import com.alipay.api.AlipayClient;
import com.alipay.api.internal.util.AlipaySignature;
import com.alipay.api.request.AlipayTradePagePayRequest;
import com.alipay.api.request.AlipayTradeQueryRequest;
import com.alipay.api.response.AlipayTradePagePayResponse;
import com.alipay.api.response.AlipayTradeQueryResponse;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.yanluwuyou.config.AlipayConfig;
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
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * 订单服务实现类
 */
@Slf4j
@Service
public class OrderServiceImpl extends ServiceImpl<OrderMapper, Order> implements OrderService {

    @Autowired
    private OrderItemMapper orderItemMapper;
    
    @Autowired
    private MaterialService materialService;
    
    @Autowired
    private CartItemService cartItemService;

    @Autowired
    private AlipayClient alipayClient;

    @Autowired
    private AlipayConfig alipayConfig;

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
    @Transactional(rollbackFor = Exception.class)
    public OrderDTO getByOrderNo(String orderNo) {
        Order order = this.getOne(new LambdaQueryWrapper<Order>().eq(Order::getOrderNo, orderNo));
        if (order == null) {
            return null;
        }
        refreshOrderStatus(order);
        OrderDTO orderDTO = new OrderDTO();
        BeanUtils.copyProperties(order, orderDTO);
        List<OrderItem> items = orderItemMapper.selectList(new LambdaQueryWrapper<OrderItem>().eq(OrderItem::getOrderId, order.getId()));
        orderDTO.setItems(items);
        return orderDTO;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public List<OrderDTO> getUserOrders(Long userId) {
        List<Order> orders = this.list(new LambdaQueryWrapper<Order>()
                .eq(Order::getUserId, userId)
                .orderByDesc(Order::getCreateTime));

        for (Order order : orders) {
            refreshOrderStatus(order);
        }
        
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
    public String payOrder(Long orderId) {
        Order order = this.getById(orderId);
        if (order == null) {
            throw new RuntimeException("订单不存在");
        }
        if (order.getStatus() != 0) {
            throw new RuntimeException("订单已支付或已取消");
        }

        String tradeStatus = queryTradeStatus(order.getOrderNo());
        if ("TRADE_SUCCESS".equals(tradeStatus) || "TRADE_FINISHED".equals(tradeStatus)) {
            // 注意：这里不要在同一个抛异常的事务里更新状态，否则会回滚
            // 抛出特定的消息让前端捕获
            throw new RuntimeException("订单已支付");
        }

        BigDecimal totalAmount = order.getTotalAmount();
        if (totalAmount == null || totalAmount.compareTo(BigDecimal.ZERO) <= 0) {
            List<OrderItem> items = orderItemMapper.selectList(new LambdaQueryWrapper<OrderItem>()
                    .eq(OrderItem::getOrderId, order.getId()));
            totalAmount = items.stream()
                    .map(item -> item.getPrice().multiply(BigDecimal.valueOf(item.getQuantity())))
                    .reduce(BigDecimal.ZERO, BigDecimal::add);
        }

        if (totalAmount.compareTo(BigDecimal.ZERO) <= 0) {
            throw new RuntimeException("订单金额异常");
        }

        totalAmount = totalAmount.setScale(2, java.math.RoundingMode.HALF_UP);
        if (order.getTotalAmount() == null || order.getTotalAmount().compareTo(totalAmount) != 0) {
            order.setTotalAmount(totalAmount);
            this.updateById(order);
        }

        String subject = "研路无忧资料购买";
        List<OrderItem> orderItems = orderItemMapper.selectList(new LambdaQueryWrapper<OrderItem>()
                .eq(OrderItem::getOrderId, order.getId()));
        if (!orderItems.isEmpty()) {
            subject = orderItems.get(0).getMaterialName();
        }

        AlipayTradePagePayRequest request = new AlipayTradePagePayRequest();
        request.setNotifyUrl(alipayConfig.getNotifyUrl());
        request.setReturnUrl(alipayConfig.getReturnUrl());

        request.setBizContent("{" +
                "\"out_trade_no\":\"" + order.getOrderNo() + "\"," +
                "\"total_amount\":\"" + totalAmount + "\"," +
                "\"subject\":\"" + subject + "\"," +
                "\"product_code\":\"FAST_INSTANT_TRADE_PAY\"" +
                "}");

        try {
            AlipayTradePagePayResponse response = alipayClient.pageExecute(request);
            if (response.isSuccess()) {
                return response.getBody();
            } else {
                throw new RuntimeException("调用支付宝接口失败: " + response.getMsg());
            }
        } catch (AlipayApiException e) {
            throw new RuntimeException("支付宝支付异常", e);
        }
    }

    private String queryTradeStatus(String orderNo) {
        AlipayTradeQueryRequest request = new AlipayTradeQueryRequest();
        request.setBizContent("{\"out_trade_no\":\"" + orderNo + "\"}");
        try {
            AlipayTradeQueryResponse response = alipayClient.execute(request);
            if (response.isSuccess()) {
                return response.getTradeStatus();
            }
        } catch (AlipayApiException e) {
            log.error("支付宝交易查询异常: {}", orderNo, e);
        }
        return null;
    }

    private void refreshOrderStatus(Order order) {
        if (order.getStatus() == null || order.getStatus() != 0) {
            return;
        }
        String tradeStatus = queryTradeStatus(order.getOrderNo());
        if ("TRADE_SUCCESS".equals(tradeStatus) || "TRADE_FINISHED".equals(tradeStatus)) {
            updateOrderStatus(order.getId(), 1);
            order.setStatus(1);
        }
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean handleAlipayNotify(Map<String, String> params) {
        try {
            // 验证签名
            boolean signVerified = AlipaySignature.rsaCheckV1(params, alipayConfig.getAlipayPublicKey(), 
                    alipayConfig.getCharset(), alipayConfig.getSignType());
            
            if (!signVerified) {
                log.error("支付宝异步回调签名验证失败，尝试主动查询状态并挽救更新");
                // 即使签名验证失败，我们也可以通过 orderNo 主动向支付宝查询订单状态
                // 只要主动查询的结果是支付成功，就可以信任该笔订单
                String orderNo = params.get("out_trade_no");
                if (orderNo != null) {
                    String actualTradeStatus = queryTradeStatus(orderNo);
                    if ("TRADE_SUCCESS".equals(actualTradeStatus) || "TRADE_FINISHED".equals(actualTradeStatus)) {
                        Order order = this.getOne(new LambdaQueryWrapper<Order>().eq(Order::getOrderNo, orderNo));
                        if (order != null && order.getStatus() == 0) {
                            updateOrderStatus(order.getId(), 1);
                            log.info("通过主动查询挽救更新成功: {}", orderNo);
                            return true;
                        }
                    }
                }
                return false;
            }

            String orderNo = params.get("out_trade_no");
            String tradeStatus = params.get("trade_status");

            // 如果是同步跳转，可能没有 trade_status，此时主动查询一次
            if (tradeStatus == null) {
                tradeStatus = queryTradeStatus(orderNo);
            }

            if ("TRADE_SUCCESS".equals(tradeStatus) || "TRADE_FINISHED".equals(tradeStatus)) {
                Order order = this.getOne(new LambdaQueryWrapper<Order>().eq(Order::getOrderNo, orderNo));
                if (order != null && order.getStatus() == 0) {
                    updateOrderStatus(order.getId(), 1);
                    log.info("订单支付成功: {}", orderNo);
                }
            }
            return true;
        } catch (AlipayApiException e) {
            log.error("支付宝异步回调处理异常", e);
            return false;
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
        List<Order> deliveredOrders = this.list(new LambdaQueryWrapper<Order>()
                .eq(Order::getUserId, userId)
                .eq(Order::getStatus, 3));

        if (deliveredOrders.isEmpty()) {
            return false;
        }

        List<Long> orderIds = deliveredOrders.stream().map(Order::getId).collect(Collectors.toList());

        // 查询这些订单中是否包含该资料
        Long count = orderItemMapper.selectCount(new LambdaQueryWrapper<OrderItem>()
                .in(OrderItem::getOrderId, orderIds)
                .eq(OrderItem::getMaterialId, materialId));
        
        return count > 0;
    }
}
