package com.yanluwuyou.service;

import com.yanluwuyou.entity.Material;
import java.util.List;

public interface RecommendService {
    List<Material> getRecommendedMaterials(Long userId, int limit);
    List<Material> getPopularMaterials(int limit);
    List<Material> getRecentlyViewedMaterials(Long userId, int limit);
}
