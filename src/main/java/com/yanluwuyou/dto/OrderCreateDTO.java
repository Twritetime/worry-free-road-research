package com.yanluwuyou.dto;

import lombok.Data;
import java.util.List;

/**
 * 创建订单DTO
 */
@Data
public class OrderCreateDTO {
    
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
