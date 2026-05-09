package com.yanluwuyou.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.yanluwuyou.entity.OrderItem;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;
import java.util.Map;

/**
 * 订单项数据访问层接口
 * 继承MyBatis-Plus的BaseMapper，提供订单项实体的数据库操作
 * 对应数据库表: yl_order_item
 */
@Mapper
public interface OrderItemMapper extends BaseMapper<OrderItem> {

    /**
     * 获取资料销售排名
     * @param limit 限制数量
     * @return 排名列表
     */
    @Select("SELECT material_id, material_name, SUM(quantity) as totalQuantity " +
            "FROM yl_order_item " +
            "GROUP BY material_id, material_name " +
            "ORDER BY totalQuantity DESC " +
            "LIMIT #{limit}")
    List<Map<String, Object>> getMaterialSalesRanking(@Param("limit") int limit);
}
