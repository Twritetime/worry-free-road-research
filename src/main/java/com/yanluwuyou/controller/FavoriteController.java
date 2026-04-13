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

@RestController
@RequestMapping("/favorite")
@RequireLogin
public class FavoriteController {

    @Autowired
    private FavoriteService favoriteService;

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

    @GetMapping("/check")
    public Result<Boolean> check(@RequestParam Long userId, 
                                 @RequestParam Long targetId, 
                                 @RequestParam Integer type) {
        AuthGuard.assertOwnerOrAdmin(userId);
        return Result.success(favoriteService.isFavorite(userId, targetId, type));
    }
}
