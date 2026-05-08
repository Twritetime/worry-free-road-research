package com.yanluwuyou.controller;

import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.yanluwuyou.auth.AuthGuard;
import com.yanluwuyou.auth.RequireLogin;
import com.yanluwuyou.common.ContentSecurityUtil;
import com.yanluwuyou.common.Result;
import com.yanluwuyou.entity.Comment;
import com.yanluwuyou.entity.Guide;
import com.yanluwuyou.entity.News;
import com.yanluwuyou.entity.Post;
import com.yanluwuyou.service.CommentService;
import com.yanluwuyou.service.GuideService;
import com.yanluwuyou.service.NewsService;
import com.yanluwuyou.service.PostService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * 评论控制器
 * 提供评论的查询、发布、删除等功能，支持多级评论
 */
@RestController
@RequestMapping("/comment")
public class CommentController {

    @Autowired
    private CommentService commentService;

    @Autowired
    private PostService postService;

    @Autowired
    private GuideService guideService;

    @Autowired
    private NewsService newsService;

    @Autowired
    private ContentSecurityUtil securityUtil;

    /**
     * 获取评论列表（支持分页和子评论）
     * 
     * @param targetType 目标类型（1-帖子, 2-指南, 3-新闻）
     * @param targetId 目标ID
     * @param pageNum 页码
     * @param pageSize 每页数量
     * @return 评论分页列表（包含子评论）
     */
    @GetMapping("/list")
    public Result<Page<Comment>> list(@RequestParam Integer targetType,
                                      @RequestParam Long targetId,
                                      @RequestParam(defaultValue = "1") Integer pageNum,
                                      @RequestParam(defaultValue = "10") Integer pageSize) {
        Page<Comment> page = new Page<>(pageNum, pageSize);
        LambdaQueryWrapper<Comment> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(Comment::getTargetType, targetType);
        queryWrapper.eq(Comment::getTargetId, targetId);
        queryWrapper.and(wrapper -> wrapper.isNull(Comment::getParentId).or().eq(Comment::getParentId, 0));
        queryWrapper.orderByDesc(Comment::getCreateTime);

        Page<Comment> commentPage = commentService.page(page, queryWrapper);

        if (!commentPage.getRecords().isEmpty()) {
            List<Long> parentIds = commentPage.getRecords().stream().map(Comment::getId).collect(Collectors.toList());
            List<Comment> children = commentService.list(new LambdaQueryWrapper<Comment>()
                    .in(Comment::getParentId, parentIds)
                    .orderByAsc(Comment::getCreateTime));

            Map<Long, List<Comment>> childrenMap = children.stream().collect(Collectors.groupingBy(Comment::getParentId));
            commentPage.getRecords().forEach(c -> c.setChildren(childrenMap.get(c.getId())));
        }

        return Result.success(commentPage);
    }

    /**
     * 获取帖子评论列表（旧版接口，兼容用）
     * 
     * @param postId 帖子ID
     * @param pageNum 页码
     * @param pageSize 每页数量
     * @return 评论分页列表
     */
    @GetMapping("/list/{postId}")
    public Result<Page<Comment>> listOld(@PathVariable Long postId,
                                      @RequestParam(defaultValue = "1") Integer pageNum,
                                      @RequestParam(defaultValue = "10") Integer pageSize) {
        return list(1, postId, pageNum, pageSize);
    }

    /**
     * 发布评论
     * 需要进行敏感词检测和内容过滤
     * 
     * @param comment 评论信息
     * @return 操作结果
     */
    @PostMapping
    @RequireLogin
    public Result<?> save(@RequestBody Comment comment) {
        if (StrUtil.isBlank(comment.getContent())) {
            return Result.error("评论内容不能为空");
        }

        ContentSecurityUtil.SecurityResult contentCheck = securityUtil.checkContent(comment.getContent());
        if (!contentCheck.isPassed()) {
            return Result.error("评论内容包含敏感词，请修改后重试");
        }

        comment.setContent(securityUtil.sanitizeHtml(comment.getContent()));

        if (comment.getTargetType() == null) {
            comment.setTargetType(1);
        }
        if (comment.getTargetId() == null && comment.getPostId() != null) {
            comment.setTargetId(comment.getPostId());
        }
        if (comment.getTargetType() == 1 && comment.getPostId() == null) {
            comment.setPostId(comment.getTargetId());
        }

        comment.setUserId(AuthGuard.currentUserId());
        commentService.save(comment);

        if (comment.getTargetType() == 1) {
            Post post = postService.getById(comment.getTargetId());
            if (post != null) {
                post.setCommentCount((post.getCommentCount() == null ? 0 : post.getCommentCount()) + 1);
                postService.updateById(post);
            }
        } else if (comment.getTargetType() == 2) {
            Guide guide = guideService.getById(comment.getTargetId());
            if (guide != null) {
                guide.setCommentCount((guide.getCommentCount() == null ? 0 : guide.getCommentCount()) + 1);
                guideService.updateById(guide);
            }
        } else if (comment.getTargetType() == 3) {
            News news = newsService.getById(comment.getTargetId());
            if (news != null) {
                news.setCommentCount((news.getCommentCount() == null ? 0 : news.getCommentCount()) + 1);
                newsService.updateById(news);
            }
        }

        return Result.success();
    }

    /**
     * 删除评论
     * 同时删除子评论并更新目标对象的评论数
     * 
     * @param id 评论ID
     * @return 操作结果
     */
    @DeleteMapping("/{id}")
    @RequireLogin
    public Result<?> delete(@PathVariable Long id) {
        Comment comment = commentService.getById(id);
        if (comment != null) {
            AuthGuard.assertOwnerOrAdmin(comment.getUserId());

            if (comment.getTargetType() != null) {
                if (comment.getTargetType() == 1) {
                    Post post = postService.getById(comment.getTargetId());
                    if (post != null) {
                        post.setCommentCount(Math.max(0, (post.getCommentCount() == null ? 0 : post.getCommentCount()) - 1));
                        postService.updateById(post);
                    }
                } else if (comment.getTargetType() == 2) {
                    Guide guide = guideService.getById(comment.getTargetId());
                    if (guide != null) {
                        guide.setCommentCount(Math.max(0, (guide.getCommentCount() == null ? 0 : guide.getCommentCount()) - 1));
                        guideService.updateById(guide);
                    }
                } else if (comment.getTargetType() == 3) {
                    News news = newsService.getById(comment.getTargetId());
                    if (news != null) {
                        news.setCommentCount(Math.max(0, (news.getCommentCount() == null ? 0 : news.getCommentCount()) - 1));
                        newsService.updateById(news);
                    }
                }
            } else if (comment.getPostId() != null) {
                Post post = postService.getById(comment.getPostId());
                if (post != null) {
                    post.setCommentCount(Math.max(0, post.getCommentCount() - 1));
                    postService.updateById(post);
                }
            }
            long childrenCount = commentService.count(new LambdaQueryWrapper<Comment>().eq(Comment::getParentId, id));
            if (childrenCount > 0) {
                 if (comment.getTargetType() == 1) {
                    Post post = postService.getById(comment.getTargetId());
                    if (post != null) {
                        post.setCommentCount(Math.max(0, post.getCommentCount() - (int)childrenCount));
                        postService.updateById(post);
                    }
                } else if (comment.getTargetType() == 2) {
                    Guide guide = guideService.getById(comment.getTargetId());
                    if (guide != null) {
                        guide.setCommentCount(Math.max(0, guide.getCommentCount() - (int)childrenCount));
                        guideService.updateById(guide);
                    }
                } else if (comment.getTargetType() == 3) {
                    News news = newsService.getById(comment.getTargetId());
                    if (news != null) {
                        news.setCommentCount(Math.max(0, news.getCommentCount() - (int)childrenCount));
                        newsService.updateById(news);
                    }
                }
            }
            commentService.remove(new LambdaQueryWrapper<Comment>().eq(Comment::getParentId, id));
            commentService.removeById(id);
        }
        return Result.success();
    }
}
