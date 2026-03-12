package com.yanluwuyou.controller;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.yanluwuyou.common.Result;
import com.yanluwuyou.entity.Address;
import com.yanluwuyou.service.AddressService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/address")
public class AddressController {

    @Autowired
    private AddressService addressService;

    @GetMapping("/list")
    public Result<List<Address>> list(@RequestParam Long userId) {
        QueryWrapper<Address> query = new QueryWrapper<>();
        query.eq("user_id", userId).orderByDesc("is_default");
        return Result.success(addressService.list(query));
    }

    @PostMapping
    public Result<?> save(@RequestBody Address address) {
        addressService.save(address);
        if (address.getIsDefault() != null && address.getIsDefault()) {
            addressService.setDefault(address.getUserId(), address.getId());
        }
        return Result.success();
    }

    @PutMapping
    public Result<?> update(@RequestBody Address address) {
        addressService.updateById(address);
        if (address.getIsDefault() != null && address.getIsDefault()) {
            addressService.setDefault(address.getUserId(), address.getId());
        }
        return Result.success();
    }

    @DeleteMapping("/{id}")
    public Result<?> delete(@PathVariable Long id) {
        addressService.removeById(id);
        return Result.success();
    }
}
