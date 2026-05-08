package com.yanluwuyou.service.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.crypto.digest.DigestUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.yanluwuyou.dto.UserLoginDTO;
import com.yanluwuyou.dto.UserRegisterDTO;
import com.yanluwuyou.entity.User;
import com.yanluwuyou.mapper.UserMapper;
import com.yanluwuyou.service.UserService;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

/**
 * 用户服务实现类
 * 实现用户注册、登录等核心业务逻辑
 * 继承ServiceImpl获得MyBatis-Plus的基础CRUD能力
 */
@Service
public class UserServiceImpl extends ServiceImpl<UserMapper, User> implements UserService {

    /**
     * 用户注册实现
     * 检查用户名唯一性，加密密码后保存到数据库
     * 
     * @param userRegisterDTO 注册信息
     * @return 注册成功的用户（密码已置空）
     * @throws RuntimeException 当用户名已存在时抛出异常
     */
    @Override
    public User register(UserRegisterDTO userRegisterDTO) {
        
        User existUser = getOne(new LambdaQueryWrapper<User>()
                .eq(User::getUsername, userRegisterDTO.getUsername()));
        if (existUser != null) {
            throw new RuntimeException("用户名已存在");
        }

        User user = new User();
        BeanUtil.copyProperties(userRegisterDTO, user);
        
        user.setPassword(DigestUtil.md5Hex(userRegisterDTO.getPassword()));
        user.setRole(User.ROLE_STUDENT);
        user.setStatus(1);
        
        save(user);
        user.setPassword(null);
        return user;
    }

    /**
     * 用户登录验证实现
     * 验证用户名、密码和账户状态
     * 
     * @param userLoginDTO 登录信息
     * @return 登录成功的用户（密码已置空）
     * @throws RuntimeException 当用户不存在、密码错误或账号被禁用时抛出异常
     */
    @Override
    public User login(UserLoginDTO userLoginDTO) {
        User user = getOne(new LambdaQueryWrapper<User>()
                .eq(User::getUsername, userLoginDTO.getUsername()));
        
        if (user == null) {
            throw new RuntimeException("用户不存在");
        }
        
        String inputPwd = userLoginDTO.getPassword();
        String dbPwd = user.getPassword();
        if (!dbPwd.equals(DigestUtil.md5Hex(inputPwd)) && !dbPwd.equals(inputPwd)) {
            throw new RuntimeException("密码错误");
        }

        if (user.getStatus() != null && user.getStatus() == 0) {
            throw new RuntimeException("账号已被禁用");
        }

        user.setLastLoginTime(LocalDateTime.now());
        updateById(user);
        user.setPassword(null);
        return user;
    }
}
