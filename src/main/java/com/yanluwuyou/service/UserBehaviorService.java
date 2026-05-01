package com.yanluwuyou.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.yanluwuyou.entity.UserBehavior;

public interface UserBehaviorService extends IService<UserBehavior> {
    void recordBehavior(Long userId, Integer behaviorType, Long targetType, Long targetId, String targetTitle);
}
