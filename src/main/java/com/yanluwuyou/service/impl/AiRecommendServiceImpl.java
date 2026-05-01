package com.yanluwuyou.service.impl;

import cn.hutool.core.util.StrUtil;
import cn.hutool.http.HttpRequest;
import cn.hutool.http.HttpResponse;
import cn.hutool.json.JSONArray;
import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.yanluwuyou.dto.AiRecommendDTO;
import com.yanluwuyou.entity.Material;
import com.yanluwuyou.entity.OrderItem;
import com.yanluwuyou.entity.UserBehavior;
import com.yanluwuyou.mapper.MaterialMapper;
import com.yanluwuyou.mapper.UserBehaviorMapper;
import com.yanluwuyou.service.AiRecommendService;
import com.yanluwuyou.service.OrderItemService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

@Service
public class AiRecommendServiceImpl implements AiRecommendService {

    @Autowired
    private MaterialMapper materialMapper;

    @Autowired
    private UserBehaviorMapper userBehaviorMapper;

    @Autowired
    private OrderItemService orderItemService;

    @Value("${ai.deepseek.enabled:false}")
    private boolean aiEnabled;

    @Value("${ai.deepseek.api-key:}")
    private String apiKey;

    @Value("${ai.deepseek.base-url:https://api.deepseek.com}")
    private String baseUrl;

    @Value("${ai.deepseek.model:deepseek-chat}")
    private String model;

    private static final String RECOMMEND_PROMPT = "你是考研资料推荐专家。根据用户的兴趣偏好和行为数据，分析并给出精准的推荐理由。\n" +
            "要求：\n" +
            "1. 推荐理由要个性化、有说服力\n" +
            "2. 分析用户的学习阶段和需求\n" +
            "3. 推荐要具体，不要说空话\n" +
            "4. 输出格式为JSON：{\"reason\":\"推荐理由\",\"sceneTag\":\"场景标签\",\"confidence\":0.95}";

    @Override
    public AiRecommendDTO getAiSmartRecommend(Long userId, int limit) {
        AiRecommendDTO result = new AiRecommendDTO();

        List<Material> candidates = getCandidateMaterials(userId, limit * 3);

        if (candidates.isEmpty()) {
            candidates = getPopularMaterials(limit);
            result.setMaterials(candidates);
            result.setRecommendReason("当前暂无足够数据，为你推荐平台热门资料");
            result.setSceneTag("热门推荐");
            result.setConfidence(0.5);
            return result;
        }

        if (userId != null && aiEnabled && StrUtil.isNotBlank(apiKey)) {
            String userProfile = buildUserProfile(userId);
            String aiAnalysis = callAiForRecommend(userProfile, candidates);
            if (StrUtil.isNotBlank(aiAnalysis)) {
                try {
                    JSONObject json = JSONUtil.parseObj(aiAnalysis);
                    result.setRecommendReason(json.getStr("reason", "根据你的学习偏好推荐"));
                    result.setSceneTag(json.getStr("sceneTag", "智能推荐"));
                    result.setConfidence(json.getDouble("confidence", 0.8));
                } catch (Exception e) {
                    result.setRecommendReason("根据你的学习偏好推荐");
                    result.setSceneTag("智能推荐");
                    result.setConfidence(0.8);
                }
            } else {
                result.setRecommendReason("根据你的学习偏好推荐");
                result.setSceneTag("智能推荐");
                result.setConfidence(0.8);
            }
        } else {
            result.setRecommendReason("根据你的学习偏好推荐");
            result.setSceneTag("智能推荐");
            result.setConfidence(0.75);
        }

        List<Material> finalList = candidates.stream().limit(limit).collect(Collectors.toList());
        result.setMaterials(finalList);
        return result;
    }

    @Override
    public List<Material> getGuessYouLike(Long userId, int limit) {
        if (userId == null) {
            return getPopularMaterials(limit);
        }

        Set<String> preferredCategories = getUserPreferredCategories(userId);
        List<Long> purchasedIds = getUserPurchasedMaterialIds(userId);
        List<Long> viewedIds = getUserViewedMaterialIds(userId);

        LambdaQueryWrapper<Material> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Material::getStatus, 1).gt(Material::getStock, 0);

        if (!preferredCategories.isEmpty()) {
            wrapper.in(Material::getCategory, preferredCategories);
        }

        List<Material> candidates = materialMapper.selectList(wrapper);

        candidates = candidates.stream()
                .filter(m -> !purchasedIds.contains(m.getId()))
                .filter(m -> !viewedIds.contains(m.getId()))
                .sorted(Comparator.comparingInt(Material::getSales).reversed())
                .limit(limit)
                .collect(Collectors.toList());

        if (candidates.size() < limit) {
            List<Material> popular = getPopularMaterials(limit - candidates.size());
            for (Material p : popular) {
                if (candidates.size() >= limit) break;
                if (!purchasedIds.contains(p.getId()) && !viewedIds.contains(p.getId())) {
                    boolean exists = candidates.stream().anyMatch(c -> c.getId().equals(p.getId()));
                    if (!exists) {
                        candidates.add(p);
                    }
                }
            }
        }

        return candidates;
    }

    @Override
    public AiRecommendDTO getSceneRecommend(Long userId, String scene, int limit) {
        AiRecommendDTO result = new AiRecommendDTO();
        LambdaQueryWrapper<Material> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Material::getStatus, 1).gt(Material::getStock, 0);

        String reason;
        String sceneTag;

        switch (scene) {
            case "冲刺":
                wrapper.like(Material::getTags, "冲刺")
                        .or().like(Material::getTags, "真题")
                        .or().like(Material::getTags, "模拟")
                        .or().like(Material::getName, "冲刺")
                        .or().like(Material::getName, "真题");
                reason = "距离考试不远啦！这些冲刺资料能帮你快速提分";
                sceneTag = "冲刺必备";
                break;
            case "基础":
                wrapper.like(Material::getTags, "基础")
                        .or().like(Material::getTags, "入门")
                        .or().like(Material::getName, "基础")
                        .or().like(Material::getName, "入门");
                reason = "打好基础是成功的关键！这些资料适合现阶段的你";
                sceneTag = "基础巩固";
                break;
            case "强化":
                wrapper.like(Material::getTags, "强化")
                        .or().like(Material::getTags, "提高")
                        .or().like(Material::getName, "强化")
                        .or().like(Material::getName, "提高");
                reason = "强化阶段需要大量刷题，这些资料助你突破瓶颈";
                sceneTag = "强化提升";
                break;
            case "新品":
                wrapper.orderByDesc(Material::getCreateTime);
                reason = "平台最新上架的资料，第一时间获取优质内容";
                sceneTag = "新品上架";
                break;
            case "热销":
                wrapper.orderByDesc(Material::getSales);
                reason = "大家都在买的资料，口碑之选";
                sceneTag = "热销排行";
                break;
            default:
                wrapper.orderByDesc(Material::getSales);
                reason = "为你精选的优质资料";
                sceneTag = "精选推荐";
        }

        List<Material> materials = materialMapper.selectList(wrapper.last("LIMIT " + limit));
        if (materials.isEmpty()) {
            materials = getPopularMaterials(limit);
        }

        result.setMaterials(materials);
        result.setRecommendReason(reason);
        result.setSceneTag(sceneTag);
        result.setConfidence(0.85);
        return result;
    }

    @Override
    public List<Material> getBundleRecommend(Long materialId, int limit) {
        LambdaQueryWrapper<OrderItem> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(OrderItem::getMaterialId, materialId);
        List<OrderItem> items = orderItemService.list(wrapper);

        if (items.isEmpty()) {
            return getSimilarByCategory(materialId, limit);
        }

        List<Long> orderIds = items.stream()
                .map(OrderItem::getOrderId)
                .distinct()
                .collect(Collectors.toList());

        LambdaQueryWrapper<OrderItem> bundleWrapper = new LambdaQueryWrapper<>();
        bundleWrapper.in(OrderItem::getOrderId, orderIds)
                .ne(OrderItem::getMaterialId, materialId);
        List<OrderItem> bundleItems = orderItemService.list(bundleWrapper);

        Map<Long, Long> materialCount = bundleItems.stream()
                .collect(Collectors.groupingBy(OrderItem::getMaterialId, Collectors.counting()));

        List<Long> topMaterialIds = materialCount.entrySet().stream()
                .sorted(Map.Entry.<Long, Long>comparingByValue().reversed())
                .limit(limit)
                .map(Map.Entry::getKey)
                .collect(Collectors.toList());

        if (topMaterialIds.isEmpty()) {
            return getSimilarByCategory(materialId, limit);
        }

        LambdaQueryWrapper<Material> materialWrapper = new LambdaQueryWrapper<>();
        materialWrapper.in(Material::getId, topMaterialIds)
                .eq(Material::getStatus, 1)
                .gt(Material::getStock, 0);
        List<Material> materials = materialMapper.selectList(materialWrapper);

        Map<Long, Integer> orderMap = new HashMap<>();
        for (int i = 0; i < topMaterialIds.size(); i++) {
            orderMap.put(topMaterialIds.get(i), i);
        }
        materials.sort(Comparator.comparingInt(m -> orderMap.getOrDefault(m.getId(), Integer.MAX_VALUE)));

        return materials;
    }

    @Override
    public AiRecommendDTO getColdStartRecommend(int limit) {
        AiRecommendDTO result = new AiRecommendDTO();
        List<Material> materials = getPopularMaterials(limit);
        result.setMaterials(materials);
        result.setRecommendReason("新用户专享！这些热门资料是考研学子的首选");
        result.setSceneTag("新用户推荐");
        result.setConfidence(0.9);
        return result;
    }

    private List<Material> getCandidateMaterials(Long userId, int limit) {
        if (userId == null) {
            return getPopularMaterials(limit);
        }

        Set<String> categories = getUserPreferredCategories(userId);
        List<Long> purchasedIds = getUserPurchasedMaterialIds(userId);

        LambdaQueryWrapper<Material> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Material::getStatus, 1).gt(Material::getStock, 0);

        if (!categories.isEmpty()) {
            wrapper.in(Material::getCategory, categories);
        }

        if (!purchasedIds.isEmpty()) {
            wrapper.notIn(Material::getId, purchasedIds);
        }

        wrapper.orderByDesc(Material::getSales);
        List<Material> result = materialMapper.selectList(wrapper.last("LIMIT " + limit));

        if (result.isEmpty()) {
            return getPopularMaterials(limit);
        }
        return result;
    }

    private List<Material> getPopularMaterials(int limit) {
        LambdaQueryWrapper<Material> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Material::getStatus, 1)
                .gt(Material::getStock, 0)
                .orderByDesc(Material::getSales)
                .last("LIMIT " + limit);
        return materialMapper.selectList(wrapper);
    }

    private List<Material> getSimilarByCategory(Long materialId, int limit) {
        Material material = materialMapper.selectById(materialId);
        if (material == null || material.getCategory() == null) {
            return getPopularMaterials(limit);
        }

        LambdaQueryWrapper<Material> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Material::getCategory, material.getCategory())
                .ne(Material::getId, materialId)
                .eq(Material::getStatus, 1)
                .gt(Material::getStock, 0)
                .orderByDesc(Material::getSales)
                .last("LIMIT " + limit);
        return materialMapper.selectList(wrapper);
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

        materials.sort(Comparator.comparingInt((Material m) -> behaviorScore.getOrDefault(m.getId(), 0)).reversed());

        Set<String> topCategories = new LinkedHashSet<>();
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

    private List<Long> getUserViewedMaterialIds(Long userId) {
        LambdaQueryWrapper<UserBehavior> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(UserBehavior::getUserId, userId)
                .eq(UserBehavior::getBehaviorType, UserBehavior.TYPE_VIEW);
        List<UserBehavior> behaviors = userBehaviorMapper.selectList(wrapper);
        return behaviors.stream().map(UserBehavior::getTargetId).distinct().collect(Collectors.toList());
    }

    private String buildUserProfile(Long userId) {
        LambdaQueryWrapper<UserBehavior> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(UserBehavior::getUserId, userId)
                .orderByDesc(UserBehavior::getCreateTime)
                .last("LIMIT 50");
        List<UserBehavior> behaviors = userBehaviorMapper.selectList(wrapper);

        if (behaviors.isEmpty()) {
            return "新用户，暂无行为数据";
        }

        StringBuilder sb = new StringBuilder();
        sb.append("用户最近行为：\n");

        Set<Long> targetIds = behaviors.stream()
                .map(UserBehavior::getTargetId)
                .collect(Collectors.toSet());

        if (!targetIds.isEmpty()) {
            LambdaQueryWrapper<Material> mq = new LambdaQueryWrapper<>();
            mq.in(Material::getId, targetIds);
            List<Material> materials = materialMapper.selectList(mq);
            Map<Long, Material> materialMap = materials.stream()
                    .collect(Collectors.toMap(Material::getId, m -> m));

            Map<String, Integer> categoryCount = new HashMap<>();
            for (UserBehavior b : behaviors) {
                Material m = materialMap.get(b.getTargetId());
                if (m != null && m.getCategory() != null) {
                    categoryCount.merge(m.getCategory(), 1, Integer::sum);
                }
            }

            sb.append("偏好分类：").append(categoryCount).append("\n");
        }

        long viewCount = behaviors.stream().filter(b -> b.getBehaviorType() == UserBehavior.TYPE_VIEW).count();
        long favCount = behaviors.stream().filter(b -> b.getBehaviorType() == UserBehavior.TYPE_FAVORITE).count();
        long buyCount = behaviors.stream().filter(b -> b.getBehaviorType() == UserBehavior.TYPE_PURCHASE).count();

        sb.append("浏览次数：").append(viewCount).append("\n");
        sb.append("收藏次数：").append(favCount).append("\n");
        sb.append("购买次数：").append(buyCount).append("\n");

        return sb.toString();
    }

    private String callAiForRecommend(String userProfile, List<Material> candidates) {
        try {
            StringBuilder materialInfo = new StringBuilder("候选资料：\n");
            for (Material m : candidates) {
                materialInfo.append(String.format("- %s (分类:%s, 价格:%s, 销量:%d)\n",
                        m.getName(), m.getCategory(), m.getPrice(), m.getSales()));
            }

            List<Map<String, String>> messages = new ArrayList<>();
            messages.add(Map.of("role", "system", "content", RECOMMEND_PROMPT));
            messages.add(Map.of("role", "user", "content", userProfile + "\n\n" + materialInfo + "\n请分析并输出JSON格式的推荐理由。"));

            Map<String, Object> requestBody = new HashMap<>();
            requestBody.put("model", model);
            requestBody.put("messages", messages);
            requestBody.put("max_tokens", 500);
            requestBody.put("temperature", 0.5);
            requestBody.put("response_format", Map.of("type", "json_object"));

            HttpResponse response = HttpRequest.post(baseUrl + "/chat/completions")
                    .header("Content-Type", "application/json")
                    .header("Authorization", "Bearer " + apiKey)
                    .body(JSONUtil.toJsonStr(requestBody))
                    .timeout(10000)
                    .execute();

            if (response.isOk()) {
                JSONObject jsonResponse = JSONUtil.parseObj(response.body());
                JSONArray choices = jsonResponse.getJSONArray("choices");
                if (choices != null && choices.size() > 0) {
                    JSONObject firstChoice = choices.getJSONObject(0);
                    JSONObject message = firstChoice.getJSONObject("message");
                    if (message != null) {
                        return message.getStr("content");
                    }
                }
            }
        } catch (Exception e) {
            // AI调用失败，使用默认推荐
        }
        return null;
    }
}
