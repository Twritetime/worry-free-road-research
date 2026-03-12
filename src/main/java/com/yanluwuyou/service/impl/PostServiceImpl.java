package com.yanluwuyou.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.yanluwuyou.entity.Post;
import com.yanluwuyou.mapper.PostMapper;
import com.yanluwuyou.service.PostService;
import org.springframework.stereotype.Service;

/**
 * 论坛帖子服务实现类
 */
@Service
public class PostServiceImpl extends ServiceImpl<PostMapper, Post> implements PostService {
}
