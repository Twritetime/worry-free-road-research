package com.yanluwuyou.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.yanluwuyou.entity.UserBehavior;

/**
 * 用户行为记录服务接口
 * 记录用户的浏览、收藏、购买等行为，用于推荐系统
 */
public interface UserBehaviorService extends IService<UserBehavior> {
    /**
     * 记录用户行为
     * 
     * @param userId 用户ID
     * @param behaviorType 行为类型
     * @param targetType 目标类型
     * @param targetId 目标ID
     * @param targetTitle 目标标题
     */
    void recordBehavior(Long userId, Integer behaviorType, Long targetType, Long targetId, String targetTitle);
}
