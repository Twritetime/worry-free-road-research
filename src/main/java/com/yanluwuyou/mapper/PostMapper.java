package com.yanluwuyou.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.yanluwuyou.entity.Post;
import org.apache.ibatis.annotations.Mapper;

/**
 * 论坛帖子数据访问层接口
 * 继承MyBatis-Plus的BaseMapper，提供帖子实体的数据库操作
 * 对应数据库表: yl_post
 */
@Mapper
public interface PostMapper extends BaseMapper<Post> {
}
