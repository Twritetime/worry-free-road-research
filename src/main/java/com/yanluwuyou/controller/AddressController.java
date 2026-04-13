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

@RestController
@RequestMapping("/address")
@RequireLogin
public class AddressController {

    @Autowired
    private AddressService addressService;

    @GetMapping("/list")
    public Result<List<Address>> list(@RequestParam Long userId) {
        AuthGuard.assertOwnerOrAdmin(userId);
        QueryWrapper<Address> query = new QueryWrapper<>();
        query.eq("user_id", userId).orderByDesc("is_default");
        return Result.success(addressService.list(query));
    }

    @PostMapping
    public Result<?> save(@RequestBody Address address) {
        address.setUserId(AuthGuard.currentUserId());
        addressService.save(address);
        if (address.getIsDefault() != null && address.getIsDefault()) {
            addressService.setDefault(address.getUserId(), address.getId());
        }
        return Result.success();
    }

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
