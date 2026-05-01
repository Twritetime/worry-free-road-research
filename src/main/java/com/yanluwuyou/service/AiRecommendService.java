package com.yanluwuyou.service;

import com.yanluwuyou.dto.AiRecommendDTO;
import com.yanluwuyou.entity.Material;

import java.util.List;

/**
 * AI智能推荐服务接口
 */
public interface AiRecommendService {

    /**
     * 获取AI智能推荐（结合用户画像+AI分析）
     */
    AiRecommendDTO getAiSmartRecommend(Long userId, int limit);

    /**
     * 获取"猜你喜欢"推荐
     */
    List<Material> getGuessYouLike(Long userId, int limit);

    /**
     * 获取场景化推荐（如：冲刺阶段推荐、基础薄弱推荐等）
     */
    AiRecommendDTO getSceneRecommend(Long userId, String scene, int limit);

    /**
     * 获取搭配购买推荐（买了A的人还买了B）
     */
    List<Material> getBundleRecommend(Long materialId, int limit);

    /**
     * 获取新用户冷启动推荐
     */
    AiRecommendDTO getColdStartRecommend(int limit);
}
