package com.yanluwuyou.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.yanluwuyou.entity.OrderItem;
import org.apache.ibatis.annotations.Mapper;

/**
 * 订单项数据访问层接口
 * 继承MyBatis-Plus的BaseMapper，提供订单项实体的数据库操作
 * 对应数据库表: yl_order_item
 */
@Mapper
public interface OrderItemMapper extends BaseMapper<OrderItem> {
}
