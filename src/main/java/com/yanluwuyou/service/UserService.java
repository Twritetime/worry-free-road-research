package com.yanluwuyou.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.yanluwuyou.dto.UserLoginDTO;
import com.yanluwuyou.dto.UserRegisterDTO;
import com.yanluwuyou.entity.User;

/**
 * 用户服务接口
 * 提供用户注册、登录、信息管理等业务功能
 * 继承MyBatis-Plus的IService接口，获得基础CRUD能力
 */
public interface UserService extends IService<User> {

    /**
     * 用户注册
     * 
     * @param userRegisterDTO 注册信息（用户名、密码、昵称等）
     * @return 注册成功的用户实体（包含生成的ID等信息）
     */
    User register(UserRegisterDTO userRegisterDTO);

    /**
     * 用户登录验证
     * 
     * @param userLoginDTO 登录信息（用户名和密码）
     * @return 登录成功的用户实体（如果验证失败则抛出异常）
     */
    User login(UserLoginDTO userLoginDTO);
}
