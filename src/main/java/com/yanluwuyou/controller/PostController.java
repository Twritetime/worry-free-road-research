package com.yanluwuyou.controller;

import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.yanluwuyou.common.Result;
import com.yanluwuyou.entity.Post;
import com.yanluwuyou.service.PostService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

/**
 * 论坛帖子控制器
 */
@RestController
@RequestMapping("/post")
public class PostController {

    @Autowired
    private PostService postService;

    /**
     * 获取帖子列表
     */
    @GetMapping("/list")
    public Result<Page<Post>> list(@RequestParam(defaultValue = "1") Integer pageNum,
                                   @RequestParam(defaultValue = "10") Integer pageSize,
                                   @RequestParam(required = false) String keyword,
                                   @RequestParam(required = false) Long userId,
                                   @RequestParam(required = false) Integer category) {
        Page<Post> page = new Page<>(pageNum, pageSize);
        LambdaQueryWrapper<Post> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.like(StrUtil.isNotBlank(keyword), Post::getTitle, keyword);
        if (category != null) {
            queryWrapper.eq(Post::getCategory, category);
        }
        if (userId != null) {
            queryWrapper.eq(Post::getUserId, userId);
        } else {
            queryWrapper.eq(Post::getStatus, 1); // 仅显示正常的帖子
        }
        queryWrapper.orderByDesc(Post::getIsTop).orderByDesc(Post::getCreateTime);
        
        return Result.success(postService.page(page, queryWrapper));
    }

    /**
     * 获取所有帖子列表 (管理端)
     */
    @GetMapping("/list-all")
    public Result<Page<Post>> listAll(@RequestParam(defaultValue = "1") Integer pageNum,
                                   @RequestParam(defaultValue = "10") Integer pageSize,
                                   @RequestParam(required = false) String keyword,
                                   @RequestParam(required = false) Integer category,
                                   @RequestParam(required = false) Integer status) {
        Page<Post> page = new Page<>(pageNum, pageSize);
        LambdaQueryWrapper<Post> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.like(StrUtil.isNotBlank(keyword), Post::getTitle, keyword);
        if (category != null) {
            queryWrapper.eq(Post::getCategory, category);
        }
        if (status != null) {
            queryWrapper.eq(Post::getStatus, status);
        }
        // 置顶优先，然后按时间倒序
        queryWrapper.orderByDesc(Post::getIsTop).orderByDesc(Post::getCreateTime);
        
        return Result.success(postService.page(page, queryWrapper));
    }

    /**
     * 审核帖子
     */
    @PutMapping("/{id}/audit/{status}")
    public Result<?> audit(@PathVariable Long id, @PathVariable Integer status) {
        Post post = postService.getById(id);
        if (post == null) {
            return Result.error("帖子不存在");
        }
        post.setStatus(status);
        postService.updateById(post);
        return Result.success();
    }

    /**
     * 置顶/取消置顶
     */
    @PutMapping("/{id}/top/{isTop}")
    public Result<?> toggleTop(@PathVariable Long id, @PathVariable Integer isTop) {
        Post post = postService.getById(id);
        if (post == null) {
            return Result.error("帖子不存在");
        }
        post.setIsTop(isTop);
        postService.updateById(post);
        return Result.success();
    }

    /**
     * 根据ID获取帖子详情
     */
    @GetMapping("/{id}")
    public Result<Post> getById(@PathVariable Long id) {
        Post post = postService.getById(id);
        if (post != null) {
            // 增加浏览量
            post.setViewCount(post.getViewCount() == null ? 1 : post.getViewCount() + 1);
            postService.updateById(post);
        }
        return Result.success(post);
    }

    /**
     * 发布帖子
     */
    @PostMapping
    public Result<?> save(@RequestBody Post post) {
        if (post.getViewCount() == null) post.setViewCount(0);
        if (post.getLikeCount() == null) post.setLikeCount(0);
        if (post.getCommentCount() == null) post.setCommentCount(0);
        if (post.getStatus() == null) post.setStatus(1);
        
        postService.save(post);
        return Result.success();
    }

    /**
     * 更新帖子
     */
    @PutMapping
    public Result<?> update(@RequestBody Post post) {
        postService.updateById(post);
        return Result.success();
    }

    /**
     * 删除帖子
     */
    @DeleteMapping("/{id}")
    public Result<?> delete(@PathVariable Long id) {
        postService.removeById(id);
        return Result.success();
    }
}
