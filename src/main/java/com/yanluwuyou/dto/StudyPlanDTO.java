package com.yanluwuyou.dto;

import lombok.Data;

import java.util.List;

/**
 * AI学习计划DTO
 */
@Data
public class StudyPlanDTO {

    /**
     * 计划标题
     */
    private String title;

    /**
     * 计划总天数
     */
    private Integer totalDays;

    /**
     * 每日建议学习时长（小时）
     */
    private Integer dailyHours;

    /**
     * 计划阶段列表
     */
    private List<StudyPhase> phases;

    /**
     * 推荐资料ID列表
     */
    private List<Long> recommendMaterialIds;

    /**
     * AI建议总结
     */
    private String aiAdvice;

    @Data
    public static class StudyPhase {
        /**
         * 阶段名称
         */
        private String name;

        /**
         * 阶段时长（天）
         */
        private Integer durationDays;

        /**
         * 阶段目标
         */
        private String goal;

        /**
         * 每日任务列表
         */
        private List<String> dailyTasks;

        /**
         * 推荐资料
         */
        private List<String> recommendMaterials;
    }
}
