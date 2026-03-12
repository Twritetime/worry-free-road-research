package com.yanluwuyou.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.yanluwuyou.entity.OrderItem;
import com.yanluwuyou.mapper.OrderItemMapper;
import com.yanluwuyou.service.OrderItemService;
import org.springframework.stereotype.Service;

/**
 * 订单项服务实现类
 */
@Service
public class OrderItemServiceImpl extends ServiceImpl<OrderItemMapper, OrderItem> implements OrderItemService {
}
