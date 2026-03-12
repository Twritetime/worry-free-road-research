package com.yanluwuyou.controller;

import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.yanluwuyou.common.Result;
import com.yanluwuyou.entity.Material;
import com.yanluwuyou.service.MaterialService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

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
                                       @RequestParam(required = false) String category) {
        Page<Material> page = new Page<>(pageNum, pageSize);
        LambdaQueryWrapper<Material> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.like(StrUtil.isNotBlank(keyword), Material::getName, keyword);
        queryWrapper.eq(StrUtil.isNotBlank(category), Material::getCategory, category);
        queryWrapper.eq(Material::getStatus, 1); // Only show on-shelf items
        queryWrapper.orderByDesc(Material::getCreateTime);
        
        return Result.success(materialService.page(page, queryWrapper));
    }

    /**
     * 改进版 list: 支持 status 筛选
     */
    @GetMapping("/list-all")
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
        queryWrapper.orderByDesc(Material::getCreateTime);
        return Result.success(materialService.page(page, queryWrapper));
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
    public Result<?> save(@RequestBody Material material) {
        if (material.getSales() == null) {
            material.setSales(0);
        }
        materialService.save(material);
        return Result.success();
    }

    /**
     * 更新资料
     */
    @PutMapping
    public Result<?> update(@RequestBody Material material) {
        materialService.updateById(material);
        return Result.success();
    }

    /**
     * 更新资料状态 (上架/下架)
     */
    @PutMapping("/{id}/status/{status}")
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
    public Result<?> delete(@PathVariable Long id) {
        materialService.removeById(id);
        return Result.success();
    }
}
