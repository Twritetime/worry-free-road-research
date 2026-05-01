package com.yanluwuyou.service;

import com.yanluwuyou.dto.StudyPlanDTO;
import com.yanluwuyou.dto.UserStudyProfileDTO;

/**
 * AI学习助手服务接口
 */
public interface AiStudyAssistantService {

    /**
     * 生成个性化学习计划
     *
     * @param userId       用户ID
     * @param targetSchool 目标院校
     * @param targetMajor  目标专业
     * @param examYear     考研年份
     * @param dailyHours   每日可学习时长
     * @return 学习计划
     */
    StudyPlanDTO generateStudyPlan(Long userId, String targetSchool, String targetMajor, Integer examYear, Integer dailyHours);

    /**
     * 分析用户学习画像
     *
     * @param userId 用户ID
     * @return 学习画像
     */
    UserStudyProfileDTO analyzeStudyProfile(Long userId);

    /**
     * 获取学习提醒/建议
     *
     * @param userId 用户ID
     * @return AI建议文本
     */
    String getDailyStudyAdvice(Long userId);
}
