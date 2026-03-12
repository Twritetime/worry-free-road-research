package com.yanluwuyou.dto;

import com.yanluwuyou.entity.Order;
import com.yanluwuyou.entity.OrderItem;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.List;

/**
 * 订单详情DTO
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class OrderDTO extends Order {
    
    private String username; // 用户名/昵称
    private List<OrderItem> items;
}
