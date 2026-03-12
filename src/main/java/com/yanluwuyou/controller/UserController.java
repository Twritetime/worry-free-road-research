package com.yanluwuyou.controller;

import com.yanluwuyou.common.Result;
import com.yanluwuyou.dto.UserLoginDTO;
import com.yanluwuyou.dto.UserRegisterDTO;
import com.yanluwuyou.entity.User;
import com.yanluwuyou.service.UserService;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import cn.hutool.core.util.StrUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

/**
 * 用户控制器
 */
@RestController
@RequestMapping("/user")
public class UserController {

    @Autowired
    private UserService userService;

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
        return Result.success(userService.login(userLoginDTO));
    }

    /**
     * 获取用户信息
     */
    @GetMapping("/{id}")
    public Result<User> getById(@PathVariable Long id) {
        return Result.success(userService.getById(id));
    }

    /**
     * 更新用户信息
     */
    @PutMapping
    public Result<?> update(@RequestBody User user) {
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
    public Result<?> updatePassword(@RequestBody java.util.Map<String, Object> params) {
        Long userId = Long.valueOf(params.get("userId").toString());
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
    public Result<Page<User>> listAll(@RequestParam(defaultValue = "1") Integer pageNum,
                                      @RequestParam(defaultValue = "10") Integer pageSize,
                                      @RequestParam(required = false) String keyword) {
        Page<User> page = new Page<>(pageNum, pageSize);
        LambdaQueryWrapper<User> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.like(StrUtil.isNotBlank(keyword), User::getUsername, keyword)
                .or().like(StrUtil.isNotBlank(keyword), User::getNickname, keyword);
        queryWrapper.orderByDesc(User::getCreateTime);
        return Result.success(userService.page(page, queryWrapper));
    }

    /**
     * 删除用户
     */
    @DeleteMapping("/{id}")
    public Result<?> delete(@PathVariable Long id) {
        userService.removeById(id);
        return Result.success();
    }
}
