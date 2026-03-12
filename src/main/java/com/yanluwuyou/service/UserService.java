package com.yanluwuyou.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.yanluwuyou.dto.UserLoginDTO;
import com.yanluwuyou.dto.UserRegisterDTO;
import com.yanluwuyou.entity.User;

/**
 * 用户服务接口
 */
public interface UserService extends IService<User> {

    /**
     * 注册
     * @param userRegisterDTO 注册信息
     * @return 注册成功的用户
     */
    User register(UserRegisterDTO userRegisterDTO);

    /**
     * 登录
     * @param userLoginDTO 登录信息
     * @return 登录成功的用户
     */
    User login(UserLoginDTO userLoginDTO);
}
