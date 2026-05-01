package com.yanluwuyou.controller;

import com.yanluwuyou.auth.AuthGuard;
import com.yanluwuyou.auth.RequireLogin;
import com.yanluwuyou.common.Result;
import com.yanluwuyou.dto.AiRecommendDTO;
import com.yanluwuyou.entity.Material;
import com.yanluwuyou.service.AiRecommendService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * AI智能推荐控制器
 */
@RestController
@RequestMapping("/ai-recommend")
public class AiRecommendController {

    @Autowired
    private AiRecommendService aiRecommendService;

    /**
     * AI智能推荐（综合用户画像+AI分析）
     */
    @GetMapping("/smart")
    @RequireLogin
    public Result<AiRecommendDTO> getAiSmartRecommend(
            @RequestParam(defaultValue = "10") int limit) {
        Long userId = AuthGuard.currentUserId();
        AiRecommendDTO recommend = aiRecommendService.getAiSmartRecommend(userId, limit);
        return Result.success(recommend);
    }

    /**
     * 猜你喜欢
     */
    @GetMapping("/guess-you-like")
    @RequireLogin
    public Result<List<Material>> getGuessYouLike(
            @RequestParam(defaultValue = "10") int limit) {
        Long userId = AuthGuard.currentUserId();
        List<Material> materials = aiRecommendService.getGuessYouLike(userId, limit);
        return Result.success(materials);
    }

    /**
     * 场景化推荐
     */
    @GetMapping("/scene")
    public Result<AiRecommendDTO> getSceneRecommend(
            @RequestParam String scene,
            @RequestParam(defaultValue = "10") int limit) {
        Long userId = AuthGuard.currentUserId();
        AiRecommendDTO recommend = aiRecommendService.getSceneRecommend(userId, scene, limit);
        return Result.success(recommend);
    }

    /**
     * 搭配购买推荐
     */
    @GetMapping("/bundle/{materialId}")
    public Result<List<Material>> getBundleRecommend(
            @PathVariable Long materialId,
            @RequestParam(defaultValue = "5") int limit) {
        List<Material> materials = aiRecommendService.getBundleRecommend(materialId, limit);
        return Result.success(materials);
    }

    /**
     * 新用户冷启动推荐
     */
    @GetMapping("/cold-start")
    public Result<AiRecommendDTO> getColdStartRecommend(
            @RequestParam(defaultValue = "10") int limit) {
        AiRecommendDTO recommend = aiRecommendService.getColdStartRecommend(limit);
        return Result.success(recommend);
    }
}
