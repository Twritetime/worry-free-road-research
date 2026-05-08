package com.yanluwuyou.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.math.BigDecimal;

/**
 * 订单项实体类
 * 对应数据库表 yl_order_item
 * 存储订单中的商品明细信息，一个订单可包含多个订单项
 */
@Data
@TableName("yl_order_item")
public class OrderItem {

    /**
     * 订单项ID（主键，自增）
     */
    @TableId(type = IdType.AUTO)
    private Long id;

    /**
     * 订单ID（外键，关联yl_order表）
     */
    private Long orderId;

    /**
     * 资料商品ID（外键，关联yl_material表）
     */
    private Long materialId;

    /**
     * 资料商品名称（冗余存储）
     */
    private String materialName;

    /**
     * 商品单价（下单时的价格快照）
     */
    private BigDecimal price;

    /**
     * 购买数量
     */
    private Integer quantity;
}
