package com.yanluwuyou.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.yanluwuyou.entity.Favorite;

/**
 * 收藏服务接口
 * 提供收藏、取消收藏、查询收藏状态等功能
 */
public interface FavoriteService extends IService<Favorite> {
    /**
     * 检查用户是否已收藏指定目标
     */
    boolean isFavorite(Long userId, Long targetId, Integer type);
    
    /**
     * 切换收藏状态（收藏/取消收藏）
     */
    void toggleFavorite(Long userId, Long targetId, Integer type, String title, String cover);
}
