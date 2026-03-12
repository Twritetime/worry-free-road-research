package com.yanluwuyou.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.yanluwuyou.entity.Post;
import org.apache.ibatis.annotations.Mapper;

/**
 * 论坛帖子Mapper接口
 */
@Mapper
public interface PostMapper extends BaseMapper<Post> {
}
