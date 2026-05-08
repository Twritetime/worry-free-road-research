package com.yanluwuyou.dto;

import com.yanluwuyou.entity.Order;
import com.yanluwuyou.entity.OrderItem;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.List;

/**
 * 订单详情DTO
 * 继承Order实体类，扩展订单详情展示所需的额外信息
 * 用于返回给前端的订单完整数据
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class OrderDTO extends Order {
    
    /**
     * 用户名/昵称（用于列表展示）
     */
    private String username;
    
    /**
     * 订单项明细列表
     */
    private List<OrderItem> items;
}
