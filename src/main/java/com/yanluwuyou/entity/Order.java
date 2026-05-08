package com.yanluwuyou.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.math.BigDecimal;

/**
 * 订单实体类
 * 对应数据库表 yl_order
 * 存储用户购买资料商城商品的订单信息
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("yl_order")
public class Order extends BaseEntity {

    /**
     * 订单ID（主键，自增）
     */
    @TableId(type = IdType.AUTO)
    private Long id;

    /**
     * 订单编号（唯一业务编号）
     */
    private String orderNo;

    /**
     * 用户ID（外键，关联sys_user表）
     */
    private Long userId;

    /**
     * 订单总金额
     */
    private BigDecimal totalAmount;

    /**
     * 订单状态: 0-待付款, 1-已付款, 2-已取消
     */
    private Integer status;
}
