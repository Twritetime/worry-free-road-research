package com.yanluwuyou.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.yanluwuyou.entity.Address;

public interface AddressService extends IService<Address> {
    void setDefault(Long userId, Long addressId);
}
