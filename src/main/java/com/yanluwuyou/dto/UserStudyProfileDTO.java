package com.yanluwuyou.dto;

import lombok.Data;

import java.util.List;
import java.util.Map;

/**
 * 用户学习画像DTO
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
     * 学习风格标签
     */
    private List<String> studyStyleTags;

    /**
     * 偏好科目（按权重排序）
     */
    private List<Map<String, Object>> preferredSubjects;

    /**
     * 活跃时间段分布
     */
    private Map<String, Integer> activeTimeDistribution;

    /**
     * 学习进度评估
     */
    private String progressAssessment;

    /**
     * 薄弱科目建议
     */
    private List<String> weakSubjectSuggestions;

    /**
     * AI个性化建议
     */
    private String aiPersonalizedAdvice;

    /**
     * 消费能力评估
     */
    private String spendingLevel;

    /**
     * 资料偏好类型
     */
    private List<String> materialTypePreferences;
}
