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
 * 提供购物车商品的增删改查功能
 */
@RestController
@RequestMapping("/cart")
@RequireLogin
public class CartItemController {

    @Autowired
    private CartItemService cartItemService;

    /**
     * 获取用户购物车列表
     * 
     * @param userId 用户ID
     * @return 购物车商品列表
     */
    @GetMapping("/list")
    public Result<List<CartItem>> list(@RequestParam Long userId) {
        AuthGuard.assertOwnerOrAdmin(userId);
        return Result.success(cartItemService.getUserCart(userId));
    }

    /**
     * 添加商品到购物车
     * 如果商品已存在则增加数量
     * 
     * @param cartItem 购物车项信息
     * @return 操作结果
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
     * 更新购物车商品数量
     * 
     * @param cartItem 购物车项信息
     * @return 操作结果
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
     * 删除购物车中的商品
     * 
     * @param id 购物车项ID
     * @return 操作结果
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
     * 清空用户购物车
     * 
     * @param userId 用户ID
     * @return 操作结果
     */
    @DeleteMapping("/clear")
    public Result<?> clear(@RequestParam Long userId) {
        AuthGuard.assertOwnerOrAdmin(userId);
        cartItemService.remove(new LambdaQueryWrapper<CartItem>().eq(CartItem::getUserId, userId));
        return Result.success();
    }
}
