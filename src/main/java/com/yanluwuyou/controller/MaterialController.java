package com.yanluwuyou.controller;

import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.yanluwuyou.auth.RequireRoles;
import com.yanluwuyou.common.Result;
import com.yanluwuyou.entity.Material;
import com.yanluwuyou.entity.User;
import com.yanluwuyou.service.MaterialService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

/**
 * 资料商城控制器
 */
@RestController
@RequestMapping("/material")
public class MaterialController {

    @Autowired
    private MaterialService materialService;

    /**
     * 获取资料列表
     */
    @GetMapping("/list")
    public Result<Page<Material>> list(@RequestParam(defaultValue = "1") Integer pageNum,
                                       @RequestParam(defaultValue = "10") Integer pageSize,
                                       @RequestParam(required = false) String keyword,
                                       @RequestParam(required = false) String category,
                                       @RequestParam(required = false) String tags,
                                       @RequestParam(required = false) String applyYear,
                                       @RequestParam(defaultValue = "comprehensive") String sortBy,
                                       @RequestParam(defaultValue = "desc") String sortOrder) {
        Page<Material> page = new Page<>(pageNum, pageSize);
        LambdaQueryWrapper<Material> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.like(StrUtil.isNotBlank(keyword), Material::getName, keyword);
        queryWrapper.eq(StrUtil.isNotBlank(category), Material::getCategory, category);
        queryWrapper.like(StrUtil.isNotBlank(tags), Material::getTags, tags);
        queryWrapper.eq(StrUtil.isNotBlank(applyYear), Material::getApplyYear, applyYear);
        queryWrapper.eq(Material::getStatus, 1);
        applySort(queryWrapper, sortBy, sortOrder);
        
        return Result.success(materialService.page(page, queryWrapper));
    }

    /**
     * 改进版 list: 支持 status 筛选
     */
    @GetMapping("/list-all")
    @RequireRoles({User.ROLE_ADMIN, User.ROLE_OPERATOR})
    public Result<Page<Material>> listAll(@RequestParam(defaultValue = "1") Integer pageNum,
                                       @RequestParam(defaultValue = "10") Integer pageSize,
                                       @RequestParam(required = false) String keyword,
                                       @RequestParam(required = false) String category,
                                       @RequestParam(required = false) Integer status) {
        Page<Material> page = new Page<>(pageNum, pageSize);
        LambdaQueryWrapper<Material> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.like(StrUtil.isNotBlank(keyword), Material::getName, keyword);
        queryWrapper.eq(StrUtil.isNotBlank(category), Material::getCategory, category);
        if (status != null) {
            queryWrapper.eq(Material::getStatus, status);
        }
        queryWrapper.orderByDesc(Material::getSortOrder).orderByDesc(Material::getCreateTime);
        return Result.success(materialService.page(page, queryWrapper));
    }

    @GetMapping("/flash-list")
    public Result<List<Material>> flashList(@RequestParam(defaultValue = "6") Integer limit) {
        int safeLimit = Math.max(1, Math.min(limit, 20));
        LambdaQueryWrapper<Material> queryWrapper = new LambdaQueryWrapper<>();
        LocalDateTime now = LocalDateTime.now();
        queryWrapper.eq(Material::getStatus, 1);
        queryWrapper.gt(Material::getStock, 0);
        queryWrapper.isNotNull(Material::getOriginalPrice);
        queryWrapper.gt(Material::getOriginalPrice, 0);
        queryWrapper.apply("price < original_price");
        queryWrapper.isNotNull(Material::getFlashStartTime);
        queryWrapper.isNotNull(Material::getFlashEndTime);
        queryWrapper.le(Material::getFlashStartTime, now);
        queryWrapper.ge(Material::getFlashEndTime, now);
        queryWrapper.orderByAsc(Material::getFlashEndTime);
        queryWrapper.last("limit " + safeLimit);
        return Result.success(materialService.list(queryWrapper));
    }


    /**
     * 根据ID获取资料详情
     */
    @GetMapping("/{id}")
    public Result<Material> getById(@PathVariable Long id) {
        return Result.success(materialService.getById(id));
    }

    /**
     * 新增资料
     */
    @PostMapping
    @RequireRoles({User.ROLE_ADMIN, User.ROLE_OPERATOR})
    public Result<?> save(@RequestBody Material material) {
        String validateMsg = validateFlashActivity(material);
        if (StrUtil.isNotBlank(validateMsg)) {
            return Result.error(validateMsg);
        }
        normalizePriceFields(material);
        if (material.getSales() == null) {
            material.setSales(0);
        }
        if (material.getSortOrder() == null || material.getSortOrder() == 0) {
            material.setSortOrder(nextSortOrder());
        }
        materialService.save(material);
        return Result.success();
    }

    /**
     * 更新资料
     */
    @PutMapping
    @RequireRoles({User.ROLE_ADMIN, User.ROLE_OPERATOR})
    public Result<?> update(@RequestBody Material material) {
        String validateMsg = validateFlashActivity(material);
        if (StrUtil.isNotBlank(validateMsg)) {
            return Result.error(validateMsg);
        }
        normalizePriceFields(material);
        ensureSortOrder(material);
        materialService.updateById(material);
        return Result.success();
    }

    @PutMapping("/swap-order")
    @RequireRoles({User.ROLE_ADMIN, User.ROLE_OPERATOR})
    public Result<?> swapOrder(@RequestParam Long id, @RequestParam Long swapId) {
        if (id.equals(swapId)) {
            return Result.success();
        }
        Material current = materialService.getById(id);
        Material target = materialService.getById(swapId);
        if (current == null || target == null) {
            return Result.error("资料不存在");
        }
        if (current.getSortOrder() == null || current.getSortOrder() == 0) {
            current.setSortOrder(current.getId().intValue());
        }
        if (target.getSortOrder() == null || target.getSortOrder() == 0) {
            target.setSortOrder(target.getId().intValue());
        }
        Integer tempSort = current.getSortOrder();
        current.setSortOrder(target.getSortOrder());
        target.setSortOrder(tempSort);
        materialService.updateById(current);
        materialService.updateById(target);
        return Result.success();
    }

    /**
     * 更新资料状态 (上架/下架)
     */
    @PutMapping("/{id}/status/{status}")
    @RequireRoles({User.ROLE_ADMIN, User.ROLE_OPERATOR})
    public Result<?> updateStatus(@PathVariable Long id, @PathVariable Integer status) {
        Material material = materialService.getById(id);
        if (material == null) {
            return Result.error("资料不存在");
        }
        material.setStatus(status);
        materialService.updateById(material);
        return Result.success();
    }

    /**
     * 删除资料
     */
    @DeleteMapping("/{id}")
    @RequireRoles({User.ROLE_ADMIN, User.ROLE_OPERATOR})
    public Result<?> delete(@PathVariable Long id) {
        materialService.removeById(id);
        return Result.success();
    }

    /**
     * 增加下载次数
     */
    @PostMapping("/{id}/download")
    public Result<?> incrementDownload(@PathVariable Long id) {
        Material material = materialService.getById(id);
        if (material == null) {
            return Result.error("资料不存在");
        }
        if (material.getDownloadCount() == null) {
            material.setDownloadCount(0);
        }
        material.setDownloadCount(material.getDownloadCount() + 1);
        materialService.updateById(material);
        return Result.success();
    }

    /**
     * 更新评分
     */
    @PutMapping("/{id}/rating")
    public Result<?> updateRating(@PathVariable Long id, @RequestParam Double rating) {
        if (rating < 1 || rating > 5) {
            return Result.error("评分必须在1-5之间");
        }
        Material material = materialService.getById(id);
        if (material == null) {
            return Result.error("资料不存在");
        }
        Double currentRating = material.getRating();
        if (currentRating == null || currentRating == 0) {
            material.setRating(rating);
        } else {
            material.setRating((currentRating + rating) / 2);
        }
        materialService.updateById(material);
        return Result.success();
    }

    private void applySort(LambdaQueryWrapper<Material> queryWrapper, String sortBy, String sortOrder) {
        boolean asc = "asc".equalsIgnoreCase(sortOrder);
        if ("sales".equalsIgnoreCase(sortBy)) {
            if (asc) {
                queryWrapper.orderByAsc(Material::getSales);
            } else {
                queryWrapper.orderByDesc(Material::getSales);
            }
            return;
        }
        if ("price".equalsIgnoreCase(sortBy)) {
            if (asc) {
                queryWrapper.orderByAsc(Material::getPrice);
            } else {
                queryWrapper.orderByDesc(Material::getPrice);
            }
            return;
        }
        if (asc) {
            queryWrapper.orderByAsc(Material::getCreateTime);
        } else {
            queryWrapper.orderByDesc(Material::getCreateTime);
        }
    }

    private Integer nextSortOrder() {
        Material top = materialService.lambdaQuery()
                .orderByDesc(Material::getSortOrder)
                .orderByDesc(Material::getId)
                .last("limit 1")
                .one();
        int base = 0;
        if (top != null && top.getSortOrder() != null) {
            base = top.getSortOrder();
        }
        return base + 1;
    }

    private void ensureSortOrder(Material material) {
        if (material.getSortOrder() != null && material.getSortOrder() != 0) {
            return;
        }
        Material existing = materialService.getById(material.getId());
        if (existing != null && existing.getSortOrder() != null && existing.getSortOrder() != 0) {
            material.setSortOrder(existing.getSortOrder());
            return;
        }
        material.setSortOrder(nextSortOrder());
    }

    private void normalizePriceFields(Material material) {
        if (material.getPrice() == null) {
            material.setPrice(BigDecimal.ZERO);
        }
        if (material.getOriginalPrice() == null || material.getOriginalPrice().compareTo(BigDecimal.ZERO) <= 0) {
            material.setOriginalPrice(material.getPrice());
        }
    }

    private String validateFlashActivity(Material material) {
        LocalDateTime start = material.getFlashStartTime();
        LocalDateTime end = material.getFlashEndTime();
        if ((start == null) != (end == null)) {
            return "限时活动开始和结束时间需要同时填写";
        }
        if (start != null && end != null && !end.isAfter(start)) {
            return "限时活动结束时间必须晚于开始时间";
        }
        BigDecimal price = material.getPrice();
        BigDecimal originalPrice = material.getOriginalPrice();
        if (price != null && originalPrice != null && originalPrice.compareTo(price) < 0) {
            return "划线价不能低于销售价";
        }
        return null;
    }
}
