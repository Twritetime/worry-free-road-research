package com.yanluwuyou.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.yanluwuyou.entity.Comment;
import org.apache.ibatis.annotations.Mapper;

/**
 * 评论数据访问层接口
 * 继承MyBatis-Plus的BaseMapper，提供评论实体的数据库操作
 * 对应数据库表: yl_comment
 */
@Mapper
public interface CommentMapper extends BaseMapper<Comment> {
}
