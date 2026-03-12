package com.yanluwuyou.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.yanluwuyou.entity.Favorite;

public interface FavoriteService extends IService<Favorite> {
    boolean isFavorite(Long userId, Long targetId, Integer type);
    void toggleFavorite(Long userId, Long targetId, Integer type, String title, String cover);
}
