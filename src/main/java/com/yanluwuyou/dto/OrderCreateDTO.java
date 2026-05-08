package com.yanluwuyou.dto;

import lombok.Data;
import java.util.List;

/**
 * 创建订单请求DTO
 * 用于接收前端传递的订单创建信息，支持购物车批量下单和直接购买两种方式
 */
@Data
public class OrderCreateDTO {
    
    /**
     * 用户ID
     */
    private Long userId;
    
    /**
     * 购物车项ID列表，如果为空则表示直接购买单个商品
     */
    private List<Long> cartItemIds;
    
    /**
     * 直接购买时的商品ID
     */
    private Long materialId;
    
    /**
     * 直接购买时的数量
     */
    private Integer quantity;
}
