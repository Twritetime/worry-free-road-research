package com.yanluwuyou.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.yanluwuyou.entity.UserBehavior;
import org.apache.ibatis.annotations.Mapper;

/**
 * 用户行为记录数据访问层接口
 * 继承MyBatis-Plus的BaseMapper，提供用户行为实体的数据库操作
 * 对应数据库表: yl_user_behavior
 */
@Mapper
public interface UserBehaviorMapper extends BaseMapper<UserBehavior> {
}
