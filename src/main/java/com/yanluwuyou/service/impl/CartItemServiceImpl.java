package com.yanluwuyou.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.yanluwuyou.entity.CartItem;
import com.yanluwuyou.entity.Material;
import com.yanluwuyou.mapper.CartItemMapper;
import com.yanluwuyou.service.CartItemService;
import com.yanluwuyou.service.MaterialService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * 购物车服务实现类
 */
@Service
public class CartItemServiceImpl extends ServiceImpl<CartItemMapper, CartItem> implements CartItemService {

    @Autowired
    private MaterialService materialService;

    @Override
    public List<CartItem> getUserCart(Long userId) {
        List<CartItem> cartItems = this.list(new LambdaQueryWrapper<CartItem>()
                .eq(CartItem::getUserId, userId)
                .orderByDesc(CartItem::getCreateTime));
        
        for (CartItem item : cartItems) {
            Material material = materialService.getById(item.getMaterialId());
            item.setMaterial(material);
        }
        return cartItems;
    }
}
