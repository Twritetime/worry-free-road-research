package com.yanluwuyou.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.EqualsAndHashCode;
import java.math.BigDecimal;

/**
 * 资料商城实体类
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("yl_material")
public class Material extends BaseEntity {

    @TableId(type = IdType.AUTO)
    private Long id;

    /**
     * 资料名称
     */
    private String name;

    /**
     * 描述
     */
    private String description;

    /**
     * 价格
     */
    private BigDecimal price;

    /**
     * 库存
     */
    private Integer stock;

    /**
     * 分类
     */
    private String category;

    /**
     * 封面图片
     */
    private String coverImg;

    /**
     * 资料文件路径
     */
    private String fileUrl;

    /**
     * 规格
     */
    private String specs;

    /**
     * 销量
     */
    private Integer sales;

    /**
     * 状态: 1:上架, 0:下架
     */
    private Integer status;
}
