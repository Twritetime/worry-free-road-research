package com.yanluwuyou.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.yanluwuyou.entity.Post;

/**
 * 论坛帖子服务接口
 * 提供论坛帖子的发布、查询、审核等业务功能
 * 继承MyBatis-Plus的IService接口，获得基础CRUD能力
 */
public interface PostService extends IService<Post> {
}
