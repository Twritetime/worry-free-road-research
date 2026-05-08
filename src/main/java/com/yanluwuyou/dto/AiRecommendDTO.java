package com.yanluwuyou.dto;

import com.yanluwuyou.entity.Material;
import lombok.Data;

import java.util.List;

/**
 * AI推荐结果DTO
 * 封装AI智能推荐的结果数据，包含推荐资料列表和推荐理由
 */
@Data
public class AiRecommendDTO {

    /**
     * 推荐资料列表
     */
    private List<Material> materials;

    /**
     * AI推荐理由
     */
    private String recommendReason;

    /**
     * 推荐场景标签
     */
    private String sceneTag;

    /**
     * 置信度 (0-1)
     */
    private Double confidence;
}
