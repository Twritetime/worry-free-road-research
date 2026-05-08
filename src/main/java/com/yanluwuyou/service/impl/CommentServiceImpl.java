package com.yanluwuyou.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.yanluwuyou.entity.Comment;
import com.yanluwuyou.mapper.CommentMapper;
import com.yanluwuyou.service.CommentService;
import org.springframework.stereotype.Service;

/**
 * 评论服务实现类
 * 实现评论的增删改查及子评论查询功能
 */
@Service
public class CommentServiceImpl extends ServiceImpl<CommentMapper, Comment> implements CommentService {
}
