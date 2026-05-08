package com.yanluwuyou.dto;

import lombok.Data;

import java.util.List;
import java.util.Map;

/**
 * 用户学习画像DTO
 * 基于用户行为数据分析生成的用户学习特征画像
 * 用于AI个性化推荐和学习计划生成
 */
@Data
public class UserStudyProfileDTO {

    /**
     * 用户ID
     */
    private Long userId;

    /**
     * 用户昵称
     */
    private String nickname;

    /**
     * 学习风格标签（如：视觉型、听觉型、阅读型等）
     */
    private List<String> studyStyleTags;

    /**
     * 偏好科目列表（按权重降序排序）
     * 每个元素包含科目名称和权重值
     */
    private List<Map<String, Object>> preferredSubjects;

    /**
     * 活跃时间段分布（按小时统计）
     * key: 时间段(如"上午"、"下午"), value: 活跃次数
     */
    private Map<String, Integer> activeTimeDistribution;

    /**
     * 学习进度评估（如：基础阶段、强化阶段、冲刺阶段）
     */
    private String progressAssessment;

    /**
     * 薄弱科目建议列表
     */
    private List<String> weakSubjectSuggestions;

    /**
     * AI个性化学习建议
     */
    private String aiPersonalizedAdvice;

    /**
     * 消费能力评估（如：高、中、低）
     */
    private String spendingLevel;

    /**
     * 资料偏好类型列表（如：视频、PDF、题库等）
     */
    private List<String> materialTypePreferences;
}
