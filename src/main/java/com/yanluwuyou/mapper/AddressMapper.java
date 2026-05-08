package com.yanluwuyou.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.yanluwuyou.entity.Address;
import org.apache.ibatis.annotations.Mapper;

/**
 * 收货地址数据访问层接口
 * 继承MyBatis-Plus的BaseMapper，提供地址实体的数据库操作
 * 对应数据库表: address
 */
@Mapper
public interface AddressMapper extends BaseMapper<Address> {
}
