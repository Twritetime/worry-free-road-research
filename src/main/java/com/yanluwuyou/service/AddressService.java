package com.yanluwuyou.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.yanluwuyou.entity.Address;

/**
 * 收货地址服务接口
 * 提供地址的增删改查及默认地址设置功能
 */
public interface AddressService extends IService<Address> {
    /**
     * 设置默认地址
     */
    void setDefault(Long userId, Long addressId);
}
