package com.yanluwuyou.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.yanluwuyou.entity.User;
import org.apache.ibatis.annotations.Mapper;

/**
 * 用户数据访问层接口
 * 继承MyBatis-Plus的BaseMapper，提供用户实体的数据库操作
 * 对应数据库表: sys_user
 */
@Mapper
public interface UserMapper extends BaseMapper<User> {
}
