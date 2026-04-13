package com.yanluwuyou.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.yanluwuyou.auth.AuthGuard;
import com.yanluwuyou.auth.RequireLogin;
import com.yanluwuyou.common.Result;
import com.yanluwuyou.entity.CartItem;
import com.yanluwuyou.service.CartItemService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 购物车控制器
 */
@RestController
@RequestMapping("/cart")
@RequireLogin
public class CartItemController {

    @Autowired
    private CartItemService cartItemService;

    /**
     * 获取用户购物车
     */
    @GetMapping("/list")
    public Result<List<CartItem>> list(@RequestParam Long userId) {
        AuthGuard.assertOwnerOrAdmin(userId);
        return Result.success(cartItemService.getUserCart(userId));
    }

    /**
     * 添加到购物车
     */
    @PostMapping
    public Result<?> add(@RequestBody CartItem cartItem) {
        cartItem.setUserId(AuthGuard.currentUserId());
        // 检查是否已存在
        CartItem exist = cartItemService.getOne(new LambdaQueryWrapper<CartItem>()
                .eq(CartItem::getUserId, cartItem.getUserId())
                .eq(CartItem::getMaterialId, cartItem.getMaterialId()));
        
        if (exist != null) {
            exist.setQuantity(exist.getQuantity() + cartItem.getQuantity());
            cartItemService.updateById(exist);
        } else {
            cartItemService.save(cartItem);
        }
        return Result.success();
    }

    /**
     * 更新数量
     */
    @PutMapping
    public Result<?> update(@RequestBody CartItem cartItem) {
        CartItem origin = cartItemService.getById(cartItem.getId());
        if (origin == null) {
            return Result.error("购物车项不存在");
        }
        AuthGuard.assertOwnerOrAdmin(origin.getUserId());
        cartItem.setUserId(origin.getUserId());
        cartItemService.updateById(cartItem);
        return Result.success();
    }

    /**
     * 删除购物车项
     */
    @DeleteMapping("/{id}")
    public Result<?> delete(@PathVariable Long id) {
        CartItem origin = cartItemService.getById(id);
        if (origin == null) {
            return Result.error("购物车项不存在");
        }
        AuthGuard.assertOwnerOrAdmin(origin.getUserId());
        cartItemService.removeById(id);
        return Result.success();
    }

    /**
     * 清空购物车 (指定用户)
     */
    @DeleteMapping("/clear")
    public Result<?> clear(@RequestParam Long userId) {
        AuthGuard.assertOwnerOrAdmin(userId);
        cartItemService.remove(new LambdaQueryWrapper<CartItem>().eq(CartItem::getUserId, userId));
        return Result.success();
    }
}
