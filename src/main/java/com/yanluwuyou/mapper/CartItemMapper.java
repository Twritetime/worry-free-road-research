package com.yanluwuyou.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.yanluwuyou.entity.CartItem;
import org.apache.ibatis.annotations.Mapper;

/**
 * 购物车数据访问层接口
 * 继承MyBatis-Plus的BaseMapper，提供购物车项实体的数据库操作
 * 对应数据库表: yl_cart_item
 */
@Mapper
public interface CartItemMapper extends BaseMapper<CartItem> {
}
