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

@RestController
@RequestMapping("/behavior")
@RequireLogin
public class UserBehaviorController {

    @Autowired
    private UserBehaviorService userBehaviorService;

    @Autowired
    private MaterialService materialService;

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
