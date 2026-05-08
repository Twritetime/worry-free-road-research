package com.yanluwuyou.service;

import com.yanluwuyou.entity.Material;
import java.util.List;

/**
 * 推荐服务接口
 * 提供基于用户行为的资料推荐功能
 */
public interface RecommendService {
    /**
     * 获取个性化推荐资料（基于用户行为）
     */
    List<Material> getRecommendedMaterials(Long userId, int limit);
    
    /**
     * 获取热门资料（按销量/浏览量排序）
     */
    List<Material> getPopularMaterials(int limit);
    
    /**
     * 获取用户最近浏览的资料
     */
    List<Material> getRecentlyViewedMaterials(Long userId, int limit);
}
