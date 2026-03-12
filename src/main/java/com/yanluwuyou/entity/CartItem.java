package com.yanluwuyou.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 购物车项实体类
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("yl_cart_item")
public class CartItem extends BaseEntity {

    @TableId(type = IdType.AUTO)
    private Long id;

    /**
     * 用户ID
     */
    private Long userId;

    /**
     * 资料ID
     */
    private Long materialId;

    /**
     * 数量
     */
    private Integer quantity;

    /**
     * 资料信息 (非数据库字段，用于前端展示)
     */
    @TableField(exist = false)
    private Material material;
}
