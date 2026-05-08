package com.yanluwuyou.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.EqualsAndHashCode;
import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 资料商城实体类
 * 对应数据库表 yl_material
 * 存储考研资料商品信息，支持购买和下载
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("yl_material")
public class Material extends BaseEntity {

    /**
     * 资料ID（主键，自增）
     */
    @TableId(type = IdType.AUTO)
    private Long id;

    /**
     * 资料名称（商品标题）
     */
    private String name;

    /**
     * 资料描述详情
     */
    private String description;

    /**
     * 现价（销售价格）
     */
    private BigDecimal price;

    /**
     * 原价（划线价格，用于展示折扣）
     */
    private BigDecimal originalPrice;

    /**
     * 库存数量
     */
    private Integer stock;

    /**
     * 资料分类: 如政治、英语、数学、专业课等
     */
    private String category;

    /**
     * 封面图片URL
     */
    private String coverImg;

    /**
     * 资料文件下载路径
     */
    private String fileUrl;

    /**
     * 规格说明（如PDF格式、页数等）
     */
    private String specs;

    /**
     * 销售数量
     */
    private Integer sales;

    /**
     * 文件大小（如 15.2MB, 2.3GB）
     */
    private String fileSize;

    /**
     * 累计下载次数
     */
    private Integer downloadCount;

    /**
     * 标签（空格分隔，如：政治 英语 数学）
     */
    private String tags;

    /**
     * 适用年份（如 2026, 2027）
     */
    private String applyYear;

    /**
     * 作者或上传者名称
     */
    private String author;

    /**
     * 用户评分（1-5分制）
     */
    private Double rating;

    /**
     * 预览内容 (目录或部分预览)
     */
    private String previewContent;

    /**
     * 资料格式 (PDF, 视频, 音频, 压缩包等)
     */
    private String fileFormat;

    /**
     * 活动开始时间
     */
    private LocalDateTime flashStartTime;

    /**
     * 活动结束时间
     */
    private LocalDateTime flashEndTime;

    /**
     * 状态: 1:上架, 0:下架
     */
    private Integer status;

    @TableField("sort_order")
    private Integer sortOrder;
}
