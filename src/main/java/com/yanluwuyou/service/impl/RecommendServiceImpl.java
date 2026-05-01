package com.yanluwuyou.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.yanluwuyou.entity.Material;
import com.yanluwuyou.entity.OrderItem;
import com.yanluwuyou.entity.UserBehavior;
import com.yanluwuyou.mapper.MaterialMapper;
import com.yanluwuyou.mapper.UserBehaviorMapper;
import com.yanluwuyou.service.OrderItemService;
import com.yanluwuyou.service.RecommendService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class RecommendServiceImpl implements RecommendService {

    @Autowired
    private MaterialMapper materialMapper;

    @Autowired
    private UserBehaviorMapper userBehaviorMapper;

    @Autowired
    private OrderItemService orderItemService;

    @Override
    public List<Material> getRecommendedMaterials(Long userId, int limit) {
        List<Material> recommendations = new ArrayList<>();

        if (userId != null) {
            Set<String> userPreferredCategories = getUserPreferredCategories(userId);
            List<Long> purchasedIds = getUserPurchasedMaterialIds(userId);

            if (!userPreferredCategories.isEmpty()) {
                LambdaQueryWrapper<Material> queryWrapper = new LambdaQueryWrapper<>();
                if (!purchasedIds.isEmpty()) {
                    queryWrapper.in(Material::getCategory, userPreferredCategories)
                            .notIn(Material::getId, purchasedIds)
                            .orderByDesc(Material::getCreateTime)
                            .last("LIMIT " + limit);
                } else {
                    queryWrapper.in(Material::getCategory, userPreferredCategories)
                            .orderByDesc(Material::getCreateTime)
                            .last("LIMIT " + limit);
                }
                recommendations = materialMapper.selectList(queryWrapper);
            }

            if (recommendations.size() < limit) {
                List<Material> popular = getPopularMaterials(limit - recommendations.size());
                for (Material popularMaterial : popular) {
                    if (recommendations.size() >= limit) break;
                    final Long materialId = popularMaterial.getId();
                    boolean alreadyAdded = recommendations.stream()
                            .anyMatch(m -> m.getId().equals(materialId));
                    if (!alreadyAdded) {
                        recommendations.add(popularMaterial);
                    }
                }
            }
        } else {
            recommendations = getPopularMaterials(limit);
        }

        return recommendations;
    }

    @Override
    public List<Material> getPopularMaterials(int limit) {
        List<OrderItem> orderItems = orderItemService.list(
                new LambdaQueryWrapper<OrderItem>()
                        .select(OrderItem::getMaterialId)
                        .groupBy(OrderItem::getMaterialId)
                        .orderByDesc(OrderItem::getMaterialId)
                        .last("LIMIT " + limit)
        );

        if (orderItems.isEmpty()) {
            LambdaQueryWrapper<Material> wrapper = new LambdaQueryWrapper<>();
            wrapper.orderByDesc(Material::getCreateTime).last("LIMIT " + limit);
            return materialMapper.selectList(wrapper);
        }

        List<Long> topMaterialIds = orderItems.stream()
                .map(OrderItem::getMaterialId)
                .collect(Collectors.toList());

        LambdaQueryWrapper<Material> materialQuery = new LambdaQueryWrapper<>();
        materialQuery.in(Material::getId, topMaterialIds);
        List<Material> materials = materialMapper.selectList(materialQuery);

        Map<Long, Integer> orderIndex = new HashMap<>();
        for (int i = 0; i < topMaterialIds.size(); i++) {
            orderIndex.put(topMaterialIds.get(i), i);
        }
        materials.sort(Comparator.comparingInt((Material m) -> orderIndex.getOrDefault(m.getId(), Integer.MAX_VALUE)));

        return materials;
    }

    @Override
    public List<Material> getRecentlyViewedMaterials(Long userId, int limit) {
        if (userId == null) {
            return Collections.emptyList();
        }

        LambdaQueryWrapper<UserBehavior> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(UserBehavior::getUserId, userId)
                .eq(UserBehavior::getBehaviorType, UserBehavior.TYPE_VIEW)
                .orderByDesc(UserBehavior::getCreateTime)
                .last("LIMIT " + limit);

        List<UserBehavior> behaviors = userBehaviorMapper.selectList(queryWrapper);

        if (behaviors.isEmpty()) {
            return Collections.emptyList();
        }

        List<Long> materialIds = behaviors.stream()
                .map(UserBehavior::getTargetId)
                .distinct()
                .collect(Collectors.toList());

        LambdaQueryWrapper<Material> materialQuery = new LambdaQueryWrapper<>();
        materialQuery.in(Material::getId, materialIds);
        List<Material> materials = materialMapper.selectList(materialQuery);

        Map<Long, LocalDateTime> viewTimeMap = behaviors.stream()
                .collect(Collectors.toMap(
                        UserBehavior::getTargetId,
                        UserBehavior::getCreateTime,
                        (t1, t2) -> t1
                ));

        materials.sort(Comparator.comparing((Material m) -> viewTimeMap.get(m.getId())).reversed());

        return materials;
    }

    private Set<String> getUserPreferredCategories(Long userId) {
        LambdaQueryWrapper<UserBehavior> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(UserBehavior::getUserId, userId)
                .in(UserBehavior::getBehaviorType,
                        UserBehavior.TYPE_VIEW,
                        UserBehavior.TYPE_FAVORITE,
                        UserBehavior.TYPE_PURCHASE)
                .orderByDesc(UserBehavior::getCreateTime)
                .last("LIMIT 100");

        List<UserBehavior> behaviors = userBehaviorMapper.selectList(queryWrapper);

        Set<Long> targetIds = behaviors.stream()
                .map(UserBehavior::getTargetId)
                .collect(Collectors.toSet());

        if (targetIds.isEmpty()) {
            return new HashSet<>();
        }

        LambdaQueryWrapper<Material> materialQuery = new LambdaQueryWrapper<>();
        materialQuery.in(Material::getId, targetIds);
        List<Material> materials = materialMapper.selectList(materialQuery);

        Map<Long, Integer> behaviorScore = new HashMap<>();
        for (UserBehavior behavior : behaviors) {
            int score = behavior.getBehaviorType().equals(UserBehavior.TYPE_PURCHASE) ? 3 :
                         behavior.getBehaviorType().equals(UserBehavior.TYPE_FAVORITE) ? 2 : 1;
            behaviorScore.merge(behavior.getTargetId(), score, Integer::sum);
        }

        final Map<Long, Integer> finalBehaviorScore = behaviorScore;
        materials.sort(Comparator.comparingInt((Material m) -> finalBehaviorScore.getOrDefault(m.getId(), 0)).reversed());

        Set<String> topCategories = new HashSet<>();
        for (Material material : materials) {
            if (material.getCategory() != null) {
                topCategories.add(material.getCategory());
            }
            if (topCategories.size() >= 3) break;
        }

        return topCategories;
    }

    private List<Long> getUserPurchasedMaterialIds(Long userId) {
        return Collections.emptyList();
    }
}
