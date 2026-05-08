package com.yanluwuyou.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.yanluwuyou.entity.Feedback;
import org.apache.ibatis.annotations.Mapper;

/**
 * 用户反馈数据访问层接口
 * 继承MyBatis-Plus的BaseMapper，提供反馈实体的数据库操作
 * 对应数据库表: feedback
 */
@Mapper
public interface FeedbackMapper extends BaseMapper<Feedback> {
}
