package com.yanluwuyou.controller;

import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.yanluwuyou.common.Result;
import com.yanluwuyou.entity.Guide;
import com.yanluwuyou.service.GuideService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 报考指南控制器
 */
@RestController
@RequestMapping("/guide")
public class GuideController {

    @Autowired
    private GuideService guideService;

    @Autowired
    private com.yanluwuyou.service.CrawlerService crawlerService;

    /**
     * 爬取指南数据
     */
    @PostMapping("/crawl")
    public Result<?> crawl(@RequestParam String url, @RequestParam String category) {
        int count = crawlerService.crawlGuides(url, category);
        if (count >= 0) {
            return Result.success("成功爬取 " + count + " 条数据");
        } else {
            return Result.error("爬取失败，请检查URL或网络");
        }
    }

    /**
     * 获取指南列表
     */
    @GetMapping("/list")
    public Result<Page<Guide>> list(@RequestParam(defaultValue = "1") Integer pageNum,
                                    @RequestParam(defaultValue = "10") Integer pageSize,
                                    @RequestParam(required = false) String keyword,
                                    @RequestParam(required = false) String category,
                                    @RequestParam(required = false) String institution,
                                    @RequestParam(required = false) String major) {
        Page<Guide> page = new Page<>(pageNum, pageSize);
        LambdaQueryWrapper<Guide> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.like(StrUtil.isNotBlank(keyword), Guide::getTitle, keyword);
        queryWrapper.eq(StrUtil.isNotBlank(category), Guide::getCategory, category);
        queryWrapper.like(StrUtil.isNotBlank(institution), Guide::getInstitution, institution);
        queryWrapper.like(StrUtil.isNotBlank(major), Guide::getMajor, major);
        queryWrapper.eq(Guide::getStatus, 1); // 仅显示已发布的指南
        queryWrapper.orderByDesc(Guide::getCreateTime);
        
        return Result.success(guideService.page(page, queryWrapper));
    }

    /**
     * 获取所有指南列表 (管理端)
     */
    @GetMapping("/list-all")
    public Result<Page<Guide>> listAll(@RequestParam(defaultValue = "1") Integer pageNum,
                                    @RequestParam(defaultValue = "10") Integer pageSize,
                                    @RequestParam(required = false) String keyword,
                                    @RequestParam(required = false) String category,
                                    @RequestParam(required = false) Integer status,
                                    @RequestParam(required = false) String institution,
                                    @RequestParam(required = false) String major) {
        Page<Guide> page = new Page<>(pageNum, pageSize);
        LambdaQueryWrapper<Guide> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.like(StrUtil.isNotBlank(keyword), Guide::getTitle, keyword);
        queryWrapper.eq(StrUtil.isNotBlank(category), Guide::getCategory, category);
        queryWrapper.like(StrUtil.isNotBlank(institution), Guide::getInstitution, institution);
        queryWrapper.like(StrUtil.isNotBlank(major), Guide::getMajor, major);
        if (status != null) {
            queryWrapper.eq(Guide::getStatus, status);
        }
        queryWrapper.orderByDesc(Guide::getCreateTime);
        
        return Result.success(guideService.page(page, queryWrapper));
    }

    /**
     * 更新指南状态
     */
    @PutMapping("/{id}/status/{status}")
    public Result<?> updateStatus(@PathVariable Long id, @PathVariable Integer status) {
        Guide guide = guideService.getById(id);
        if (guide == null) {
            return Result.error("指南不存在");
        }
        guide.setStatus(status);
        guideService.updateById(guide);
        return Result.success();
    }

    /**
     * 根据ID获取指南详情
     */
    @GetMapping("/{id}")
    public Result<Guide> getById(@PathVariable Long id) {
        Guide guide = guideService.getById(id);
        if (guide != null) {
            // 增加浏览量
            guide.setViewCount(guide.getViewCount() == null ? 1 : guide.getViewCount() + 1);
            guideService.updateById(guide);
        }
        return Result.success(guide);
    }

    /**
     * 获取所有院校列表
     */
    @GetMapping("/institutions")
    public Result<List<String>> getInstitutions() {
        return Result.success(guideService.getDistinctInstitutions());
    }

    /**
     * 获取所有专业列表
     */
    @GetMapping("/majors")
    public Result<List<String>> getMajors() {
        return Result.success(guideService.getDistinctMajors());
    }

    /**
     * 新增指南
     */
    @PostMapping
    public Result<?> save(@RequestBody Guide guide) {
        guideService.save(guide);
        return Result.success();
    }

    /**
     * 更新指南
     */
    @PutMapping
    public Result<?> update(@RequestBody Guide guide) {
        guideService.updateById(guide);
        return Result.success();
    }

    /**
     * 删除指南
     */
    @DeleteMapping("/{id}")
    public Result<?> delete(@PathVariable Long id) {
        guideService.removeById(id);
        return Result.success();
    }
}
