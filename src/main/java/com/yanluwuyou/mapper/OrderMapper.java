package com.yanluwuyou.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.yanluwuyou.entity.Order;
import org.apache.ibatis.annotations.Mapper;

/**
 * 订单数据访问层接口
 * 继承MyBatis-Plus的BaseMapper，提供订单实体的数据库操作
 * 对应数据库表: yl_order
 */
@Mapper
public interface OrderMapper extends BaseMapper<Order> {
}
