package com.yanluwuyou.controller;

import cn.hutool.core.util.StrUtil;
import com.yanluwuyou.auth.RequireRoles;
import com.yanluwuyou.common.Result;
import com.yanluwuyou.entity.HomeConfig;
import com.yanluwuyou.entity.User;
import com.yanluwuyou.service.HomeConfigService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

/**
 * 首页配置控制器
 * 提供首页轮播图、通知公告等配置项的管理接口
 */
@RestController
@RequestMapping("/home-config")
public class HomeConfigController {

    @Autowired
    private HomeConfigService homeConfigService;

    /**
     * 获取首页配置（分组返回）
     * 前台展示用
     */
    @GetMapping
    public Result<Map<String, List<HomeConfig>>> getHomeConfigs() {
        return Result.success(homeConfigService.getHomeConfigsGrouped());
    }

    /**
     * 根据类型获取配置列表
     * 前台展示用
     */
    @GetMapping("/type/{type}")
    public Result<List<HomeConfig>> getConfigsByType(@PathVariable String type) {
        return Result.success(homeConfigService.getConfigsByType(type));
    }

    /**
     * 获取所有配置列表（管理后台用）
     */
    @GetMapping("/list")
    @RequireRoles({User.ROLE_ADMIN, User.ROLE_OPERATOR})
    public Result<List<HomeConfig>> listAll(@RequestParam(required = false) String type) {
        List<HomeConfig> configs;
        if (StrUtil.isNotBlank(type)) {
            configs = homeConfigService.getAllConfigsByType(type);
        } else {
            configs = homeConfigService.list();
        }
        return Result.success(configs);
    }

    /**
     * 获取单个配置详情
     */
    @GetMapping("/{id}")
    @RequireRoles({User.ROLE_ADMIN, User.ROLE_OPERATOR})
    public Result<HomeConfig> getById(@PathVariable Long id) {
        return Result.success(homeConfigService.getById(id));
    }

    /**
     * 创建配置
     */
    @PostMapping
    @RequireRoles({User.ROLE_ADMIN, User.ROLE_OPERATOR})
    public Result<?> save(@RequestBody HomeConfig config) {
        if (config.getStatus() == null) {
            config.setStatus(1);
        }
        if (config.getSortOrder() == null || config.getSortOrder() == 0) {
            config.setSortOrder(nextSortOrder(config.getType()));
        }
        homeConfigService.save(config);
        return Result.success();
    }

    /**
     * 更新配置
     */
    @PutMapping
    @RequireRoles({User.ROLE_ADMIN, User.ROLE_OPERATOR})
    public Result<?> update(@RequestBody HomeConfig config) {
        ensureSortOrder(config);
        homeConfigService.updateById(config);
        return Result.success();
    }

    /**
     * 更新配置状态
     */
    @PutMapping("/{id}/status/{status}")
    @RequireRoles({User.ROLE_ADMIN, User.ROLE_OPERATOR})
    public Result<?> updateStatus(@PathVariable Long id, @PathVariable Integer status) {
        boolean success = homeConfigService.updateStatus(id, status);
        if (!success) {
            return Result.error("配置不存在");
        }
        return Result.success();
    }

    /**
     * 交换排序顺序
     */
    @PutMapping("/swap-order")
    @RequireRoles({User.ROLE_ADMIN, User.ROLE_OPERATOR})
    public Result<?> swapOrder(@RequestParam Long id, @RequestParam Long swapId) {
        boolean success = homeConfigService.swapOrder(id, swapId);
        if (!success) {
            return Result.error("配置不存在");
        }
        return Result.success();
    }

    /**
     * 删除配置
     */
    @DeleteMapping("/{id}")
    @RequireRoles({User.ROLE_ADMIN, User.ROLE_OPERATOR})
    public Result<?> delete(@PathVariable Long id) {
        homeConfigService.removeById(id);
        return Result.success();
    }

    private Integer nextSortOrder(String type) {
        HomeConfig top = homeConfigService.lambdaQuery()
                .eq(StrUtil.isNotBlank(type), HomeConfig::getType, type)
                .orderByDesc(HomeConfig::getSortOrder)
                .orderByDesc(HomeConfig::getId)
                .last("limit 1")
                .one();
        int base = 0;
        if (top != null && top.getSortOrder() != null) {
            base = top.getSortOrder();
        }
        return base + 1;
    }

    private void ensureSortOrder(HomeConfig config) {
        if (config.getSortOrder() != null && config.getSortOrder() != 0) {
            return;
        }
        HomeConfig existing = homeConfigService.getById(config.getId());
        if (existing != null && existing.getSortOrder() != null && existing.getSortOrder() != 0) {
            config.setSortOrder(existing.getSortOrder());
            return;
        }
        config.setSortOrder(nextSortOrder(config.getType()));
    }
}