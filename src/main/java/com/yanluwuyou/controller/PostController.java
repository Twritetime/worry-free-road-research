package com.yanluwuyou.controller;

import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.yanluwuyou.auth.AuthGuard;
import com.yanluwuyou.auth.RequireLogin;
import com.yanluwuyou.auth.RequireRoles;
import com.yanluwuyou.common.Result;
import com.yanluwuyou.entity.Favorite;
import com.yanluwuyou.entity.Post;
import com.yanluwuyou.entity.User;
import com.yanluwuyou.service.FavoriteService;
import com.yanluwuyou.service.PostService;
import com.yanluwuyou.service.UserService;
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

    @Autowired
    private UserService userService;

    @Autowired
    private FavoriteService favoriteService;

    /**
     * 获取帖子列表
     */
    @GetMapping("/list")
    public Result<Page<Post>> list(@RequestParam(defaultValue = "1") Integer pageNum,
                                   @RequestParam(defaultValue = "10") Integer pageSize,
                                   @RequestParam(required = false) String keyword,
                                   @RequestParam(required = false) Long userId,
                                   @RequestParam(required = false) Integer category,
                                   @RequestParam(required = false, defaultValue = "createTime") String sortBy,
                                   @RequestParam(required = false, defaultValue = "desc") String sortOrder) {
        Page<Post> page = new Page<>(pageNum, pageSize);
        LambdaQueryWrapper<Post> queryWrapper = new LambdaQueryWrapper<>();
        if (StrUtil.isNotBlank(keyword)) {
            queryWrapper.and(w -> w.like(Post::getTitle, keyword).or().like(Post::getNickname, keyword));
        }
        if (category != null) {
            queryWrapper.eq(Post::getCategory, category);
        }
        if (userId != null) {
            queryWrapper.eq(Post::getUserId, userId);
        } else {
            queryWrapper.eq(Post::getStatus, 1); // 仅显示正常的帖子
        }

        if ("viewCount".equals(sortBy)) {
            if ("asc".equalsIgnoreCase(sortOrder)) {
                queryWrapper.orderByDesc(Post::getIsTop).orderByAsc(Post::getViewCount);
            } else {
                queryWrapper.orderByDesc(Post::getIsTop).orderByDesc(Post::getViewCount);
            }
        } else if ("commentCount".equals(sortBy)) {
            if ("asc".equalsIgnoreCase(sortOrder)) {
                queryWrapper.orderByDesc(Post::getIsTop).orderByAsc(Post::getCommentCount);
            } else {
                queryWrapper.orderByDesc(Post::getIsTop).orderByDesc(Post::getCommentCount);
            }
        } else {
            if ("asc".equalsIgnoreCase(sortOrder)) {
                queryWrapper.orderByDesc(Post::getIsTop).orderByAsc(Post::getCreateTime);
            } else {
                queryWrapper.orderByDesc(Post::getIsTop).orderByDesc(Post::getCreateTime);
            }
        }

        return Result.success(postService.page(page, queryWrapper));
    }

    /**
     * 获取所有帖子列表 (管理端)
     */
    @GetMapping("/list-all")
    @RequireRoles({User.ROLE_ADMIN, User.ROLE_OPERATOR})
    public Result<Page<Post>> listAll(@RequestParam(defaultValue = "1") Integer pageNum,
                                   @RequestParam(defaultValue = "10") Integer pageSize,
                                   @RequestParam(required = false) String keyword,
                                   @RequestParam(required = false) Integer category,
                                   @RequestParam(required = false) Integer status) {
        Page<Post> page = new Page<>(pageNum, pageSize);
        LambdaQueryWrapper<Post> queryWrapper = new LambdaQueryWrapper<>();
        if (StrUtil.isNotBlank(keyword)) {
            queryWrapper.and(w -> w.like(Post::getTitle, keyword).or().like(Post::getNickname, keyword));
        }
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
    @RequireRoles({User.ROLE_ADMIN, User.ROLE_OPERATOR})
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
    @RequireRoles({User.ROLE_ADMIN, User.ROLE_OPERATOR})
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

    @PostMapping("/{id}/like")
    @RequireLogin
    public Result<?> like(@PathVariable Long id) {
        Post post = postService.getById(id);
        if (post == null) {
            return Result.error("帖子不存在");
        }
        Long currentUserId = AuthGuard.currentUserId();
        if (favoriteService.isFavorite(currentUserId, id, 5)) {
            return Result.error("您已经点过赞了");
        }
        Favorite likeRecord = new Favorite();
        likeRecord.setUserId(currentUserId);
        likeRecord.setTargetId(id);
        likeRecord.setTargetType(5);
        favoriteService.save(likeRecord);
        post.setLikeCount((post.getLikeCount() == null ? 0 : post.getLikeCount()) + 1);
        postService.updateById(post);
        return Result.success();
    }

    /**
     * 发布帖子
     */
    @PostMapping
    @RequireLogin
    public Result<?> save(@RequestBody Post post) {
        if (post.getViewCount() == null) post.setViewCount(0);
        if (post.getLikeCount() == null) post.setLikeCount(0);
        if (post.getCommentCount() == null) post.setCommentCount(0);
        if (post.getStatus() == null) post.setStatus(1);
        post.setUserId(AuthGuard.currentUserId());
        User user = userService.getById(post.getUserId());
        if (user != null) {
            post.setNickname(user.getNickname() != null ? user.getNickname() : user.getUsername());
            post.setAvatar(user.getAvatar());
        }
        postService.save(post);
        return Result.success();
    }

    /**
     * 更新帖子
     */
    @PutMapping
    @RequireLogin
    public Result<?> update(@RequestBody Post post) {
        Post origin = postService.getById(post.getId());
        if (origin == null) {
            return Result.error("帖子不存在");
        }
        AuthGuard.assertOwnerOrAdmin(origin.getUserId());
        post.setUserId(origin.getUserId());
        User user = userService.getById(post.getUserId());
        if (user != null) {
            post.setNickname(user.getNickname() != null ? user.getNickname() : user.getUsername());
            post.setAvatar(user.getAvatar());
        }
        postService.updateById(post);
        return Result.success();
    }

    /**
     * 删除帖子
     */
    @DeleteMapping("/{id}")
    @RequireLogin
    public Result<?> delete(@PathVariable Long id) {
        Post origin = postService.getById(id);
        if (origin == null) {
            return Result.error("帖子不存在");
        }
        AuthGuard.assertOwnerOrAdmin(origin.getUserId());
        postService.removeById(id);
        return Result.success();
    }
}
