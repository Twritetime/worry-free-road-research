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

@RestController
@RequestMapping("/recommend")
public class RecommendController {

    @Autowired
    private RecommendService recommendService;

    @GetMapping("/materials")
    @RequireLogin
    public Result<List<Material>> getRecommendedMaterials(
            @RequestParam(defaultValue = "10") int limit) {
        Long userId = AuthGuard.currentUserId();
        List<Material> recommendations = recommendService.getRecommendedMaterials(userId, limit);
        return Result.success(recommendations);
    }

    @GetMapping("/popular")
    public Result<List<Material>> getPopularMaterials(
            @RequestParam(defaultValue = "10") int limit) {
        List<Material> popular = recommendService.getPopularMaterials(limit);
        return Result.success(popular);
    }

    @GetMapping("/recently-viewed")
    @RequireLogin
    public Result<List<Material>> getRecentlyViewedMaterials(
            @RequestParam(defaultValue = "10") int limit) {
        Long userId = AuthGuard.currentUserId();
        List<Material> recentlyViewed = recommendService.getRecentlyViewedMaterials(userId, limit);
        return Result.success(recentlyViewed);
    }
}
