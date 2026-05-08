package com.yanluwuyou.controller;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.yanluwuyou.auth.AuthGuard;
import com.yanluwuyou.auth.RequireLogin;
import com.yanluwuyou.common.Result;
import com.yanluwuyou.entity.Address;
import com.yanluwuyou.service.AddressService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 收货地址控制器
 * 提供用户收货地址的增删改查功能
 */
@RestController
@RequestMapping("/address")
@RequireLogin
public class AddressController {

    @Autowired
    private AddressService addressService;

    /**
     * 获取用户收货地址列表
     * 
     * @param userId 用户ID
     * @return 地址列表（按默认地址排序）
     */
    @GetMapping("/list")
    public Result<List<Address>> list(@RequestParam Long userId) {
        AuthGuard.assertOwnerOrAdmin(userId);
        QueryWrapper<Address> query = new QueryWrapper<>();
        query.eq("user_id", userId).orderByDesc("is_default");
        return Result.success(addressService.list(query));
    }

    /**
     * 新增收货地址
     * 
     * @param address 地址信息
     * @return 操作结果
     */
    @PostMapping
    public Result<?> save(@RequestBody Address address) {
        address.setUserId(AuthGuard.currentUserId());
        addressService.save(address);
        if (address.getIsDefault() != null && address.getIsDefault()) {
            addressService.setDefault(address.getUserId(), address.getId());
        }
        return Result.success();
    }

    /**
     * 更新收货地址
     * 
     * @param address 地址信息
     * @return 操作结果
     */
    @PutMapping
    public Result<?> update(@RequestBody Address address) {
        Address origin = addressService.getById(address.getId());
        if (origin == null) {
            return Result.error("地址不存在");
        }
        AuthGuard.assertOwnerOrAdmin(origin.getUserId());
        address.setUserId(origin.getUserId());
        addressService.updateById(address);
        if (address.getIsDefault() != null && address.getIsDefault()) {
            addressService.setDefault(address.getUserId(), address.getId());
        }
        return Result.success();
    }

    @PutMapping("/default/{id}")
    public Result<?> setDefault(@PathVariable Long id) {
        Address origin = addressService.getById(id);
        if (origin == null) {
            return Result.error("地址不存在");
        }
        AuthGuard.assertOwnerOrAdmin(origin.getUserId());
        addressService.setDefault(origin.getUserId(), id);
        return Result.success();
    }

    /**
     * 删除收货地址
     *
     * @param id 地址ID
     * @return 操作结果
     */
    @DeleteMapping("/{id}")
    public Result<?> delete(@PathVariable Long id) {
        Address origin = addressService.getById(id);
        if (origin == null) {
            return Result.error("地址不存在");
        }
        AuthGuard.assertOwnerOrAdmin(origin.getUserId());
        addressService.removeById(id);
        return Result.success();
    }
}
