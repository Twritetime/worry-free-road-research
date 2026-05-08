package com.yanluwuyou.controller;

import com.yanluwuyou.auth.AuthGuard;
import com.yanluwuyou.auth.RequireLogin;
import com.yanluwuyou.common.Result;
import com.yanluwuyou.entity.Material;
import com.yanluwuyou.service.RecommendService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * 推荐控制器
 * 提供基于用户行为的资料推荐功能
 */
@RestController
@RequestMapping("/recommend")
public class RecommendController {

    @Autowired
    private RecommendService recommendService;

    /**
     * 获取个性化推荐资料（基于用户行为）
     * 
     * @param limit 推荐数量
     * @return 推荐资料列表
     */
    @GetMapping("/materials")
    @RequireLogin
    public Result<List<Material>> getRecommendedMaterials(
            @RequestParam(defaultValue = "10") int limit) {
        Long userId = AuthGuard.currentUserId();
        List<Material> recommendations = recommendService.getRecommendedMaterials(userId, limit);
        return Result.success(recommendations);
    }

    /**
     * 获取热门资料（按销量/浏览量排序）
     * 
     * @param limit 返回数量
     * @return 热门资料列表
     */
    @GetMapping("/popular")
    public Result<List<Material>> getPopularMaterials(
            @RequestParam(defaultValue = "10") int limit) {
        List<Material> popular = recommendService.getPopularMaterials(limit);
        return Result.success(popular);
    }

    /**
     * 获取用户最近浏览的资料
     * 
     * @param limit 返回数量
     * @return 最近浏览资料列表
     */
    @GetMapping("/recently-viewed")
    @RequireLogin
    public Result<List<Material>> getRecentlyViewedMaterials(
            @RequestParam(defaultValue = "10") int limit) {
        Long userId = AuthGuard.currentUserId();
        List<Material> recentlyViewed = recommendService.getRecentlyViewedMaterials(userId, limit);
        return Result.success(recentlyViewed);
    }
}
