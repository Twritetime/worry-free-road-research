package com.yanluwuyou.controller;

import com.yanluwuyou.auth.AuthGuard;
import com.yanluwuyou.auth.RequireLogin;
import com.yanluwuyou.auth.RequireRoles;
import com.yanluwuyou.auth.TokenService;
import com.yanluwuyou.common.Result;
import com.yanluwuyou.dto.UserLoginDTO;
import com.yanluwuyou.dto.UserRegisterDTO;
import com.yanluwuyou.entity.User;
import com.yanluwuyou.service.UserService;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import cn.hutool.core.util.StrUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;

/**
 * 用户控制器
 */
@RestController
@RequestMapping("/user")
public class UserController {

    @Autowired
    private UserService userService;

    @Autowired
    private TokenService tokenService;

    /**
     * 注册
     */
    @PostMapping("/register")
    public Result<User> register(@RequestBody UserRegisterDTO userRegisterDTO) {
        return Result.success(userService.register(userRegisterDTO));
    }

    /**
     * 登录
     */
    @PostMapping("/login")
    public Result<User> login(@RequestBody UserLoginDTO userLoginDTO) {
        User user = userService.login(userLoginDTO);
        user.setToken(tokenService.createToken(user));
        return Result.success(user);
    }

    /**
     * 获取用户信息
     */
    @GetMapping("/{id}")
    @RequireLogin
    public Result<User> getById(@PathVariable Long id) {
        AuthGuard.assertOwnerOrAdmin(id);
        User user = userService.getById(id);
        if (user != null) {
            user.setPassword(null);
        }
        return Result.success(user);
    }

    /**
     * 更新用户信息
     */
    @PutMapping
    @RequireLogin
    public Result<?> update(@RequestBody User user) {
        AuthGuard.assertOwnerOrAdmin(user.getId());
        // 防止修改密码
        user.setPassword(null);
        user.setUsername(null); // 用户名也不允许修改
        userService.updateById(user);
        return Result.success(userService.getById(user.getId()));
    }

    /**
     * 修改密码
     */
    @PutMapping("/password")
    @RequireLogin
    public Result<?> updatePassword(@RequestBody java.util.Map<String, Object> params) {
        Long userId = AuthGuard.currentUserId();
        String oldPassword = (String) params.get("oldPassword");
        String newPassword = (String) params.get("newPassword");

        User user = userService.getById(userId);
        if (user == null) {
            return Result.error("用户不存在");
        }
        if (!user.getPassword().equals(cn.hutool.crypto.digest.DigestUtil.md5Hex(oldPassword))) {
            return Result.error("原密码错误");
        }
        user.setPassword(cn.hutool.crypto.digest.DigestUtil.md5Hex(newPassword));
        userService.updateById(user);
        return Result.success();
    }

    /**
     * 获取用户列表（管理端）
     */
    @GetMapping("/list-all")
    @RequireRoles(User.ROLE_ADMIN)
    public Result<Page<User>> listAll(@RequestParam(defaultValue = "1") Integer pageNum,
                                      @RequestParam(defaultValue = "10") Integer pageSize,
                                      @RequestParam(required = false) String keyword,
                                      @RequestParam(required = false) String username,
                                      @RequestParam(required = false) String phone,
                                      @RequestParam(required = false) Integer status,
                                      @RequestParam(required = false) @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss") LocalDateTime registerStartTime,
                                      @RequestParam(required = false) @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss") LocalDateTime registerEndTime) {
        Page<User> page = new Page<>(pageNum, pageSize);
        LambdaQueryWrapper<User> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.like(StrUtil.isNotBlank(keyword), User::getUsername, keyword);
        queryWrapper.like(StrUtil.isNotBlank(username), User::getUsername, username);
        queryWrapper.like(StrUtil.isNotBlank(phone), User::getPhone, phone);
        queryWrapper.eq(status != null, User::getStatus, status);
        queryWrapper.ge(registerStartTime != null, User::getCreateTime, registerStartTime);
        queryWrapper.le(registerEndTime != null, User::getCreateTime, registerEndTime);
        queryWrapper.orderByDesc(User::getCreateTime);
        Page<User> userPage = userService.page(page, queryWrapper);
        userPage.getRecords().forEach(item -> item.setPassword(null));
        return Result.success(userPage);
    }

    /**
     * 删除用户
     */
    @DeleteMapping("/{id}")
    @RequireRoles(User.ROLE_ADMIN)
    public Result<?> delete(@PathVariable Long id) {
        if (id.equals(AuthGuard.currentUserId())) {
            return Result.error("不允许删除当前登录账号");
        }
        userService.removeById(id);
        return Result.success();
    }
}
