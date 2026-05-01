package com.yanluwuyou.controller;

import com.yanluwuyou.auth.AuthGuard;
import com.yanluwuyou.auth.RequireLogin;
import com.yanluwuyou.common.Result;
import com.yanluwuyou.dto.StudyPlanDTO;
import com.yanluwuyou.dto.UserStudyProfileDTO;
import com.yanluwuyou.service.AiStudyAssistantService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

/**
 * AI学习助手控制器
 */
@RestController
@RequestMapping("/ai-study")
public class AiStudyAssistantController {

    @Autowired
    private AiStudyAssistantService aiStudyAssistantService;

    /**
     * 生成个性化学习计划
     */
    @PostMapping("/plan")
    @RequireLogin
    public Result<StudyPlanDTO> generateStudyPlan(
            @RequestParam(required = false) String targetSchool,
            @RequestParam(required = false) String targetMajor,
            @RequestParam(required = false) Integer examYear,
            @RequestParam(required = false, defaultValue = "8") Integer dailyHours) {
        Long userId = AuthGuard.currentUserId();
        StudyPlanDTO plan = aiStudyAssistantService.generateStudyPlan(userId, targetSchool, targetMajor, examYear, dailyHours);
        return Result.success(plan);
    }

    /**
     * 分析用户学习画像
     */
    @GetMapping("/profile")
    @RequireLogin
    public Result<UserStudyProfileDTO> analyzeStudyProfile() {
        Long userId = AuthGuard.currentUserId();
        UserStudyProfileDTO profile = aiStudyAssistantService.analyzeStudyProfile(userId);
        return Result.success(profile);
    }

    /**
     * 获取每日学习建议
     */
    @GetMapping("/daily-advice")
    @RequireLogin
    public Result<String> getDailyStudyAdvice() {
        Long userId = AuthGuard.currentUserId();
        String advice = aiStudyAssistantService.getDailyStudyAdvice(userId);
        return Result.success(advice);
    }
}
