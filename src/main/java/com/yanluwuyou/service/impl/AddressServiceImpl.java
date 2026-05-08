package com.yanluwuyou.service.impl;

import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.yanluwuyou.entity.Address;
import com.yanluwuyou.mapper.AddressMapper;
import com.yanluwuyou.service.AddressService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * 收货地址服务实现类
 * 实现地址的增删改查及默认地址设置功能
 */
@Service
public class AddressServiceImpl extends ServiceImpl<AddressMapper, Address> implements AddressService {

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void setDefault(Long userId, Long addressId) {
        // Clear default
        UpdateWrapper<Address> clearWrapper = new UpdateWrapper<>();
        clearWrapper.eq("user_id", userId).set("is_default", false);
        this.update(clearWrapper);

        // Set new default
        UpdateWrapper<Address> setWrapper = new UpdateWrapper<>();
        setWrapper.eq("id", addressId).eq("user_id", userId).set("is_default", true);
        this.update(setWrapper);
    }
}
