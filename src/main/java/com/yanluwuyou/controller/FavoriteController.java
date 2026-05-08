package com.yanluwuyou.controller;

import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.yanluwuyou.auth.AuthGuard;
import com.yanluwuyou.auth.RequireLogin;
import com.yanluwuyou.common.Result;
import com.yanluwuyou.entity.Favorite;
import com.yanluwuyou.service.FavoriteService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

/**
 * 收藏控制器
 * 提供收藏、取消收藏、查询收藏列表等功能
 */
@RestController
@RequestMapping("/favorite")
@RequireLogin
public class FavoriteController {

    @Autowired
    private FavoriteService favoriteService;

    /**
     * 获取用户收藏列表
     * 
     * @param userId 用户ID
     * @param pageNum 页码
     * @param pageSize 每页数量
     * @param type 收藏类型（可选）
     * @param keyword 搜索关键词（可选）
     * @return 收藏分页列表
     */
    @GetMapping("/list")
    public Result<Page<Favorite>> list(@RequestParam Long userId,
                                       @RequestParam(defaultValue = "1") Integer pageNum,
                                       @RequestParam(defaultValue = "10") Integer pageSize,
                                       @RequestParam(required = false) Integer type,
                                       @RequestParam(required = false) String keyword) {
        AuthGuard.assertOwnerOrAdmin(userId);
        Page<Favorite> page = new Page<>(pageNum, pageSize);
        QueryWrapper<Favorite> query = new QueryWrapper<>();
        query.eq("user_id", userId);
        if (type != null) {
            query.eq("target_type", type);
        }
        query.like(StrUtil.isNotBlank(keyword), "target_title", keyword);
        query.orderByDesc("create_time");
        return Result.success(favoriteService.page(page, query));
    }

    /**
     * 切换收藏状态（收藏/取消收藏）
     * 
     * @param favorite 收藏信息
     * @return 操作结果
     */
    @PostMapping("/toggle")
    public Result<?> toggle(@RequestBody Favorite favorite) {
        favorite.setUserId(AuthGuard.currentUserId());
        favoriteService.toggleFavorite(
            favorite.getUserId(), 
            favorite.getTargetId(), 
            favorite.getTargetType(), 
            favorite.getTargetTitle(),
            favorite.getTargetCover()
        );
        return Result.success();
    }

    /**
     * 检查用户是否已收藏指定目标
     * 
     * @param userId 用户ID
     * @param targetId 目标ID
     * @param type 收藏类型
     * @return true-已收藏, false-未收藏
     */
    @GetMapping("/check")
    public Result<Boolean> check(@RequestParam Long userId, 
                                 @RequestParam Long targetId, 
                                 @RequestParam Integer type) {
        AuthGuard.assertOwnerOrAdmin(userId);
        return Result.success(favoriteService.isFavorite(userId, targetId, type));
    }
}
