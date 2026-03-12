package com.yanluwuyou.service.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.crypto.digest.DigestUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.yanluwuyou.dto.UserLoginDTO;
import com.yanluwuyou.dto.UserRegisterDTO;
import com.yanluwuyou.entity.User;
import com.yanluwuyou.mapper.UserMapper;
import com.yanluwuyou.service.UserService;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;

/**
 * 用户服务实现类
 */
@Service
public class UserServiceImpl extends ServiceImpl<UserMapper, User> implements UserService {

    @Override
    public User register(UserRegisterDTO userRegisterDTO) {
        // 检查用户名是否存在
        User existUser = getOne(new LambdaQueryWrapper<User>()
                .eq(User::getUsername, userRegisterDTO.getUsername()));
        if (existUser != null) {
            throw new RuntimeException("用户名已存在");
        }

        User user = new User();
        BeanUtil.copyProperties(userRegisterDTO, user);
        
        // 密码加密 (这里使用简单的MD5，实际生产应用BCrypt)
        user.setPassword(DigestUtil.md5Hex(userRegisterDTO.getPassword()));
        user.setRole(User.ROLE_STUDENT); // 默认角色
        
        save(user);
        return user;
    }

    @Override
    public User login(UserLoginDTO userLoginDTO) {
        User user = getOne(new LambdaQueryWrapper<User>()
                .eq(User::getUsername, userLoginDTO.getUsername()));
        
        if (user == null) {
            throw new RuntimeException("用户不存在");
        }
        
        // 验证密码
        String inputPwd = userLoginDTO.getPassword();
        String dbPwd = user.getPassword();
        // 兼容明文密码和MD5加密密码
        if (!dbPwd.equals(DigestUtil.md5Hex(inputPwd)) && !dbPwd.equals(inputPwd)) {
            throw new RuntimeException("密码错误");
        }
        
        return user;
    }
}
