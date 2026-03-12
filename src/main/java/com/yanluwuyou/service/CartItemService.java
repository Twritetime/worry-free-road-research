package com.yanluwuyou.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.yanluwuyou.entity.CartItem;

import java.util.List;

/**
 * 购物车服务接口
 */
public interface CartItemService extends IService<CartItem> {
    
    /**
     * 获取用户购物车列表（包含商品信息）
     */
    List<CartItem> getUserCart(Long userId);
}
