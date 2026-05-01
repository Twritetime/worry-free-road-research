package com.yanluwuyou.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.yanluwuyou.entity.UserBehavior;
import com.yanluwuyou.mapper.UserBehaviorMapper;
import com.yanluwuyou.service.UserBehaviorService;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
public class UserBehaviorServiceImpl extends ServiceImpl<UserBehaviorMapper, UserBehavior> implements UserBehaviorService {

    @Override
    public void recordBehavior(Long userId, Integer behaviorType, Long targetType, Long targetId, String targetTitle) {
        if (userId == null || behaviorType == null || targetId == null) {
            return;
        }
        UserBehavior behavior = new UserBehavior();
        behavior.setUserId(userId);
        behavior.setBehaviorType(behaviorType);
        behavior.setTargetType(targetType);
        behavior.setTargetId(targetId);
        behavior.setTargetTitle(targetTitle);
        behavior.setCreateTime(LocalDateTime.now());
        this.save(behavior);
    }
}
