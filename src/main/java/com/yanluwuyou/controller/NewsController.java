package com.yanluwuyou.controller;

import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.yanluwuyou.auth.RequireRoles;
import com.yanluwuyou.common.Result;
import com.yanluwuyou.entity.News;
import com.yanluwuyou.entity.User;
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
                                   @RequestParam(required = false) String type,
                                   @RequestParam(required = false, defaultValue = "createTime") String sortBy,
                                   @RequestParam(required = false, defaultValue = "desc") String sortOrder) {
        Page<News> page = new Page<>(pageNum, pageSize);
        LambdaQueryWrapper<News> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.like(StrUtil.isNotBlank(keyword), News::getTitle, keyword);
        queryWrapper.eq(StrUtil.isNotBlank(type), News::getType, type);
        queryWrapper.eq(News::getStatus, 1); // 仅显示已发布的新闻

        // 根据排序字段和排序方向进行排序
        if ("viewCount".equals(sortBy)) {
            if ("asc".equalsIgnoreCase(sortOrder)) {
                queryWrapper.orderByAsc(News::getViewCount).orderByDesc(News::getCreateTime);
            } else {
                queryWrapper.orderByDesc(News::getViewCount).orderByDesc(News::getCreateTime);
            }
        } else if ("commentCount".equals(sortBy)) {
            if ("asc".equalsIgnoreCase(sortOrder)) {
                queryWrapper.orderByAsc(News::getCommentCount).orderByDesc(News::getCreateTime);
            } else {
                queryWrapper.orderByDesc(News::getCommentCount).orderByDesc(News::getCreateTime);
            }
        } else {
            // 默认按发布时间排序
            if ("asc".equalsIgnoreCase(sortOrder)) {
                queryWrapper.orderByAsc(News::getCreateTime);
            } else {
                queryWrapper.orderByDesc(News::getCreateTime);
            }
        }

        return Result.success(newsService.page(page, queryWrapper));
    }

    /**
     * 获取所有新闻列表 (管理端)
     */
    @GetMapping("/list-all")
    @RequireRoles({User.ROLE_ADMIN, User.ROLE_OPERATOR})
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
        queryWrapper.orderByDesc(News::getSortOrder).orderByDesc(News::getCreateTime);
        
        return Result.success(newsService.page(page, queryWrapper));
    }

    /**
     * 更新新闻状态
     */
    @PutMapping("/{id}/status/{status}")
    @RequireRoles({User.ROLE_ADMIN, User.ROLE_OPERATOR})
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
    @RequireRoles({User.ROLE_ADMIN, User.ROLE_OPERATOR})
    public Result<?> save(@RequestBody News news) {
        if (news.getSortOrder() == null || news.getSortOrder() == 0) {
            news.setSortOrder(nextSortOrder());
        }
        newsService.save(news);
        return Result.success();
    }

    /**
     * 更新新闻
     */
    @PutMapping
    @RequireRoles({User.ROLE_ADMIN, User.ROLE_OPERATOR})
    public Result<?> update(@RequestBody News news) {
        ensureSortOrder(news);
        newsService.updateById(news);
        return Result.success();
    }

    @PutMapping("/swap-order")
    @RequireRoles({User.ROLE_ADMIN, User.ROLE_OPERATOR})
    public Result<?> swapOrder(@RequestParam Long id, @RequestParam Long swapId) {
        if (id.equals(swapId)) {
            return Result.success();
        }
        News current = newsService.getById(id);
        News target = newsService.getById(swapId);
        if (current == null || target == null) {
            return Result.error("新闻不存在");
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
        newsService.updateById(current);
        newsService.updateById(target);
        return Result.success();
    }

    /**
     * 删除新闻
     */
    @DeleteMapping("/{id}")
    @RequireRoles({User.ROLE_ADMIN, User.ROLE_OPERATOR})
    public Result<?> delete(@PathVariable Long id) {
        newsService.removeById(id);
        return Result.success();
    }

    private Integer nextSortOrder() {
        News top = newsService.lambdaQuery()
                .orderByDesc(News::getSortOrder)
                .orderByDesc(News::getId)
                .last("limit 1")
                .one();
        int base = 0;
        if (top != null && top.getSortOrder() != null) {
            base = top.getSortOrder();
        }
        return base + 1;
    }

    private void ensureSortOrder(News news) {
        if (news.getSortOrder() != null && news.getSortOrder() != 0) {
            return;
        }
        News existing = newsService.getById(news.getId());
        if (existing != null && existing.getSortOrder() != null && existing.getSortOrder() != 0) {
            news.setSortOrder(existing.getSortOrder());
            return;
        }
        news.setSortOrder(nextSortOrder());
    }
}
