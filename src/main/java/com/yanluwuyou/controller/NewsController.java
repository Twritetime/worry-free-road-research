package com.yanluwuyou.controller;

import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.yanluwuyou.common.Result;
import com.yanluwuyou.entity.News;
import com.yanluwuyou.service.NewsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

/**
 * 新闻资讯控制器
 */
@RestController
@RequestMapping("/news")
public class NewsController {

    @Autowired
    private NewsService newsService;

    /**
     * 获取新闻列表
     */
    @GetMapping("/list")
    public Result<Page<News>> list(@RequestParam(defaultValue = "1") Integer pageNum,
                                   @RequestParam(defaultValue = "10") Integer pageSize,
                                   @RequestParam(required = false) String keyword,
                                   @RequestParam(required = false) String type) {
        Page<News> page = new Page<>(pageNum, pageSize);
        LambdaQueryWrapper<News> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.like(StrUtil.isNotBlank(keyword), News::getTitle, keyword);
        queryWrapper.eq(StrUtil.isNotBlank(type), News::getType, type);
        queryWrapper.eq(News::getStatus, 1); // 仅显示已发布的新闻
        queryWrapper.orderByDesc(News::getCreateTime);
        
        return Result.success(newsService.page(page, queryWrapper));
    }

    /**
     * 获取所有新闻列表 (管理端)
     */
    @GetMapping("/list-all")
    public Result<Page<News>> listAll(@RequestParam(defaultValue = "1") Integer pageNum,
                                   @RequestParam(defaultValue = "10") Integer pageSize,
                                   @RequestParam(required = false) String keyword,
                                   @RequestParam(required = false) String type,
                                   @RequestParam(required = false) Integer status) {
        Page<News> page = new Page<>(pageNum, pageSize);
        LambdaQueryWrapper<News> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.like(StrUtil.isNotBlank(keyword), News::getTitle, keyword);
        queryWrapper.eq(StrUtil.isNotBlank(type), News::getType, type);
        if (status != null) {
            queryWrapper.eq(News::getStatus, status);
        }
        queryWrapper.orderByDesc(News::getCreateTime);
        
        return Result.success(newsService.page(page, queryWrapper));
    }

    /**
     * 更新新闻状态
     */
    @PutMapping("/{id}/status/{status}")
    public Result<?> updateStatus(@PathVariable Long id, @PathVariable Integer status) {
        News news = newsService.getById(id);
        if (news == null) {
            return Result.error("新闻不存在");
        }
        news.setStatus(status);
        newsService.updateById(news);
        return Result.success();
    }

    /**
     * 根据ID获取新闻详情
     */
    @GetMapping("/{id}")
    public Result<News> getById(@PathVariable Long id) {
        News news = newsService.getById(id);
        if (news != null) {
            // 增加浏览量（简单实现，非线程安全，Demo级别）
            news.setViewCount(news.getViewCount() + 1);
            newsService.updateById(news);
        }
        return Result.success(news);
    }

    /**
     * 新增新闻
     */
    @PostMapping
    public Result<?> save(@RequestBody News news) {
        newsService.save(news);
        return Result.success();
    }

    /**
     * 更新新闻
     */
    @PutMapping
    public Result<?> update(@RequestBody News news) {
        newsService.updateById(news);
        return Result.success();
    }

    /**
     * 删除新闻
     */
    @DeleteMapping("/{id}")
    public Result<?> delete(@PathVariable Long id) {
        newsService.removeById(id);
        return Result.success();
    }
}
