package com.yanluwuyou.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.yanluwuyou.entity.Favorite;
import com.yanluwuyou.mapper.FavoriteMapper;
import com.yanluwuyou.service.FavoriteService;
import org.springframework.stereotype.Service;

@Service
public class FavoriteServiceImpl extends ServiceImpl<FavoriteMapper, Favorite> implements FavoriteService {

    @Override
    public boolean isFavorite(Long userId, Long targetId, Integer type) {
        QueryWrapper<Favorite> query = new QueryWrapper<>();
        query.eq("user_id", userId).eq("target_id", targetId).eq("target_type", type);
        return this.count(query) > 0;
    }

    @Override
    public void toggleFavorite(Long userId, Long targetId, Integer type, String title, String cover) {
        // Use custom select to find any record (including deleted ones) to avoid duplicate entry errors
        Favorite exist = baseMapper.selectRaw(userId, targetId, type);
        
        if (exist != null) {
            if (exist.getDeleted() != null && exist.getDeleted() == 1) {
                // If it was logically deleted, restore it
                baseMapper.restore(exist.getId());
            } else {
                // If it exists and is active, delete it (toggle off)
                this.removeById(exist.getId());
            }
        } else {
            // If it doesn't exist, create a new one
            Favorite favorite = new Favorite();
            favorite.setUserId(userId);
            favorite.setTargetId(targetId);
            favorite.setTargetType(type);
            favorite.setTargetTitle(title);
            favorite.setTargetCover(cover);
            this.save(favorite);
        }
    }
}
