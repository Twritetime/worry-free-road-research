package com.yanluwuyou.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.yanluwuyou.entity.Post;
import com.yanluwuyou.mapper.PostMapper;
import com.yanluwuyou.service.PostService;
import org.springframework.stereotype.Service;

/**
 * 论坛帖子服务实现类
 * 实现论坛帖子的发布、查询、审核等业务功能
 */
@Service
public class PostServiceImpl extends ServiceImpl<PostMapper, Post> implements PostService {
}
