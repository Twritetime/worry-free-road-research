package com.yanluwuyou.dto;

import lombok.Data;

import java.util.List;

/**
 * AI标签生成结果DTO
 * 封装AI自动分析资料后生成的标签、分类和摘要信息
 * 用于资料上传时的自动化处理
 */
@Data
public class AiTagResultDTO {

    /**
     * 自动生成的标签列表（用于资料检索和推荐）
     */
    private List<String> tags;

    /**
     * AI推荐的资料分类（如：政治、英语、数学等）
     */
    private String recommendedCategory;

    /**
     * 资料内容摘要（由AI自动生成）
     */
    private String summary;

    /**
     * 适用人群列表（如：基础薄弱、冲刺阶段等）
     */
    private List<String> targetAudience;

    /**
     * 难度评估（如：入门、进阶、高级）
     */
    private String difficultyLevel;
}
