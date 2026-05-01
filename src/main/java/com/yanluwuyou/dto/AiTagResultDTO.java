package com.yanluwuyou.dto;

import lombok.Data;

import java.util.List;

/**
 * AI标签生成结果DTO
 */
@Data
public class AiTagResultDTO {

    /**
     * 生成的标签列表
     */
    private List<String> tags;

    /**
     * 推荐分类
     */
    private String recommendedCategory;

    /**
     * 资料摘要
     */
    private String summary;

    /**
     * 适用人群
     */
    private List<String> targetAudience;

    /**
     * 难度评估
     */
    private String difficultyLevel;
}
