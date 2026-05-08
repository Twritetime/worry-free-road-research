package com.yanluwuyou.controller;

import com.yanluwuyou.auth.AuthGuard;
import com.yanluwuyou.auth.RequireLogin;
import com.yanluwuyou.common.Result;
import com.yanluwuyou.entity.Material;
import com.yanluwuyou.entity.UserBehavior;
import com.yanluwuyou.service.MaterialService;
import com.yanluwuyou.service.UserBehaviorService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

/**
 * 用户行为记录控制器
 * 记录用户的浏览、收藏、购买等行为，用于推荐系统
 */
@RestController
@RequestMapping("/behavior")
@RequireLogin
public class UserBehaviorController {

    @Autowired
    private UserBehaviorService userBehaviorService;

    @Autowired
    private MaterialService materialService;

    /**
     * 记录用户行为
     * 
     * @param behavior 行为信息（行为类型、目标类型、目标ID等）
     * @return 操作结果
     */
    @PostMapping("/record")
    public Result<?> recordBehavior(@RequestBody UserBehavior behavior) {
        Long userId = AuthGuard.currentUserId();
        behavior.setUserId(userId);

        if (behavior.getTargetType() != null && behavior.getTargetType() == 1 && behavior.getTargetId() != null) {
            Material material = materialService.getById(behavior.getTargetId());
            if (material != null) {
                behavior.setTargetTitle(material.getName());
            }
        }

        userBehaviorService.recordBehavior(
                behavior.getUserId(),
                behavior.getBehaviorType(),
                behavior.getTargetType(),
                behavior.getTargetId(),
                behavior.getTargetTitle()
        );
        return Result.success();
    }

    /**
     * 获取行为类型字典
     * 
     * @return 行为类型映射表
     */
    @GetMapping("/types")
    public Result<?> getBehaviorTypes() {
        return Result.success(java.util.Map.of(
                "VIEW", UserBehavior.TYPE_VIEW,
                "FAVORITE", UserBehavior.TYPE_FAVORITE,
                "PURCHASE", UserBehavior.TYPE_PURCHASE,
                "SEARCH", UserBehavior.TYPE_SEARCH,
                "COMMENT", UserBehavior.TYPECOMMENT
        ));
    }
}
