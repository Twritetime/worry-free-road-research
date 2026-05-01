package com.yanluwuyou.service.impl;

import cn.hutool.core.util.StrUtil;
import cn.hutool.http.HttpRequest;
import cn.hutool.http.HttpResponse;
import cn.hutool.json.JSONArray;
import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.yanluwuyou.dto.AiSearchResultDTO;
import com.yanluwuyou.dto.AiTagResultDTO;
import com.yanluwuyou.entity.Material;
import com.yanluwuyou.entity.UserBehavior;
import com.yanluwuyou.mapper.MaterialMapper;
import com.yanluwuyou.mapper.UserBehaviorMapper;
import com.yanluwuyou.service.AiMaterialService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

@Service
public class AiMaterialServiceImpl implements AiMaterialService {

    @Autowired
    private MaterialMapper materialMapper;

    @Autowired
    private UserBehaviorMapper userBehaviorMapper;

    @Value("${ai.deepseek.enabled:false}")
    private boolean aiEnabled;

    @Value("${ai.deepseek.api-key:}")
    private String apiKey;

    @Value("${ai.deepseek.base-url:https://api.deepseek.com}")
    private String baseUrl;

    @Value("${ai.deepseek.model:deepseek-chat}")
    private String model;

    private static final String TAG_GENERATE_PROMPT = "你是教育内容分析专家。根据资料名称和描述，自动生成标签、分类、摘要等信息。\n" +
            "要求：\n" +
            "1. 标签要精准，涵盖科目、类型、难度、适用阶段等维度\n" +
            "2. 分类要符合考研资料的标准分类体系\n" +
            "3. 摘要简洁明了\n" +
            "4. 输出JSON格式：{\"tags\":[\"标签1\",\"标签2\"],\"recommendedCategory\":\"推荐分类\",\"summary\":\"摘要\",\"targetAudience\":[\"适用人群1\"],\"difficultyLevel\":\"难度\"}";

    private static final String SEARCH_INTENT_PROMPT = "你是搜索意图理解专家。分析用户的搜索关键词，理解其真实意图。\n" +
            "要求：\n" +
            "1. 分析用户可能在找什么\n" +
            "2. 给出相关的搜索建议\n" +
            "3. 推荐可能相关的分类\n" +
            "4. 输出JSON格式：{\"understoodIntent\":\"理解的意图\",\"searchSuggestions\":[\"建议1\"],\"relatedCategories\":[\"分类1\"]}";

    @Override
    public AiSearchResultDTO smartSearch(String keyword, Long userId) {
        AiSearchResultDTO result = new AiSearchResultDTO();
        result.setSemanticMatch(false);

        if (StrUtil.isBlank(keyword)) {
            result.setMaterials(new ArrayList<>());
            result.setUnderstoodIntent("请输入搜索关键词");
            result.setSearchSuggestions(new ArrayList<>());
            result.setRelatedCategories(new ArrayList<>());
            return result;
        }

        String understoodIntent = keyword;
        List<String> searchSuggestions = new ArrayList<>();
        List<String> relatedCategories = new ArrayList<>();

        if (aiEnabled && StrUtil.isNotBlank(apiKey)) {
            String aiResult = callAiForSearchIntent(keyword);
            if (StrUtil.isNotBlank(aiResult)) {
                try {
                    JSONObject json = JSONUtil.parseObj(aiResult);
                    understoodIntent = json.getStr("understoodIntent", keyword);

                    JSONArray suggestionsArray = json.getJSONArray("searchSuggestions");
                    if (suggestionsArray != null) {
                        for (int i = 0; i < suggestionsArray.size(); i++) {
                            searchSuggestions.add(suggestionsArray.getStr(i));
                        }
                    }

                    JSONArray categoriesArray = json.getJSONArray("relatedCategories");
                    if (categoriesArray != null) {
                        for (int i = 0; i < categoriesArray.size(); i++) {
                            relatedCategories.add(categoriesArray.getStr(i));
                        }
                    }
                    result.setSemanticMatch(true);
                } catch (Exception e) {
                    // 解析失败，使用默认搜索
                }
            }
        }

        result.setUnderstoodIntent(understoodIntent);
        result.setSearchSuggestions(searchSuggestions);
        result.setRelatedCategories(relatedCategories);

        List<Material> materials = performSmartSearch(keyword, understoodIntent, relatedCategories);

        if (userId != null) {
            recordSearchBehavior(userId, keyword);
        }

        result.setMaterials(materials);
        return result;
    }

    @Override
    public AiTagResultDTO autoGenerateTags(Long materialId) {
        Material material = materialMapper.selectById(materialId);
        if (material == null) {
            return null;
        }

        AiTagResultDTO result = new AiTagResultDTO();

        if (aiEnabled && StrUtil.isNotBlank(apiKey)) {
            String aiResult = callAiForTagGeneration(material);
            if (StrUtil.isNotBlank(aiResult)) {
                try {
                    JSONObject json = JSONUtil.parseObj(aiResult);

                    JSONArray tagsArray = json.getJSONArray("tags");
                    if (tagsArray != null) {
                        List<String> tags = new ArrayList<>();
                        for (int i = 0; i < tagsArray.size(); i++) {
                            tags.add(tagsArray.getStr(i));
                        }
                        result.setTags(tags);
                    }

                    result.setRecommendedCategory(json.getStr("recommendedCategory", material.getCategory()));
                    result.setSummary(json.getStr("summary", material.getDescription()));

                    JSONArray audienceArray = json.getJSONArray("targetAudience");
                    if (audienceArray != null) {
                        List<String> audience = new ArrayList<>();
                        for (int i = 0; i < audienceArray.size(); i++) {
                            audience.add(audienceArray.getStr(i));
                        }
                        result.setTargetAudience(audience);
                    }

                    result.setDifficultyLevel(json.getStr("difficultyLevel", "中等"));
                    return result;
                } catch (Exception e) {
                    // 解析失败，使用本地生成
                }
            }
        }

        return generateLocalTags(material);
    }

    @Override
    public List<AiTagResultDTO> batchGenerateTags(List<Long> materialIds) {
        List<AiTagResultDTO> results = new ArrayList<>();
        for (Long id : materialIds) {
            AiTagResultDTO dto = autoGenerateTags(id);
            if (dto != null) {
                results.add(dto);
            }
        }
        return results;
    }

    @Override
    public List<Material> getSimilarMaterials(Long materialId, int limit) {
        Material material = materialMapper.selectById(materialId);
        if (material == null) {
            return new ArrayList<>();
        }

        LambdaQueryWrapper<Material> wrapper = new LambdaQueryWrapper<>();
        wrapper.ne(Material::getId, materialId)
                .eq(Material::getStatus, 1)
                .gt(Material::getStock, 0);

        if (material.getCategory() != null) {
            wrapper.and(w -> w.eq(Material::getCategory, material.getCategory())
                    .or().like(Material::getTags, material.getCategory()));
        }

        List<Material> candidates = materialMapper.selectList(wrapper);

        candidates = candidates.stream()
                .sorted((m1, m2) -> {
                    int score1 = calculateSimilarityScore(material, m1);
                    int score2 = calculateSimilarityScore(material, m2);
                    return Integer.compare(score2, score1);
                })
                .limit(limit)
                .collect(Collectors.toList());

        return candidates;
    }

    private List<Material> performSmartSearch(String keyword, String understoodIntent, List<String> relatedCategories) {
        String lowerKeyword = keyword.toLowerCase();
        Set<Material> results = new LinkedHashSet<>();

        LambdaQueryWrapper<Material> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Material::getStatus, 1).gt(Material::getStock, 0);

        wrapper.and(w -> w.like(Material::getName, keyword)
                .or().like(Material::getDescription, keyword)
                .or().like(Material::getCategory, keyword)
                .or().like(Material::getTags, keyword));
        List<Material> directMatches = materialMapper.selectList(wrapper);
        results.addAll(directMatches);

        if (results.size() < 20) {
            LambdaQueryWrapper<Material> semanticWrapper = new LambdaQueryWrapper<>();
            semanticWrapper.eq(Material::getStatus, 1).gt(Material::getStock, 0);

            List<String> semanticKeywords = extractSemanticKeywords(understoodIntent);
            if (!semanticKeywords.isEmpty()) {
                semanticWrapper.and(w -> {
                    for (int i = 0; i < semanticKeywords.size(); i++) {
                        String sk = semanticKeywords.get(i);
                        if (i == 0) {
                            w.like(Material::getName, sk)
                                    .or().like(Material::getDescription, sk)
                                    .or().like(Material::getTags, sk);
                        } else {
                            w.or().like(Material::getName, sk)
                                    .or().like(Material::getDescription, sk)
                                    .or().like(Material::getTags, sk);
                        }
                    }
                });
            }

            if (!relatedCategories.isEmpty()) {
                semanticWrapper.or(w -> {
                    for (int i = 0; i < relatedCategories.size(); i++) {
                        if (i == 0) {
                            w.like(Material::getCategory, relatedCategories.get(i));
                        } else {
                            w.or().like(Material::getCategory, relatedCategories.get(i));
                        }
                    }
                });
            }

            List<Material> semanticMatches = materialMapper.selectList(semanticWrapper.last("LIMIT 20"));
            results.addAll(semanticMatches);
        }

        List<Material> finalResults = new ArrayList<>(results);

        finalResults.sort((m1, m2) -> {
            int score1 = calculateSearchRelevanceScore(m1, keyword, understoodIntent);
            int score2 = calculateSearchRelevanceScore(m2, keyword, understoodIntent);
            return Integer.compare(score2, score1);
        });

        return finalResults.stream().limit(20).collect(Collectors.toList());
    }

    private List<String> extractSemanticKeywords(String intent) {
        List<String> keywords = new ArrayList<>();
        String lower = intent.toLowerCase();

        Map<String, List<String>> semanticMap = new HashMap<>();
        semanticMap.put("数学", Arrays.asList("数学", "高数", "线代", "概率", "微积分", "数一", "数二", "数三"));
        semanticMap.put("英语", Arrays.asList("英语", "词汇", "阅读", "作文", "翻译", "完形"));
        semanticMap.put("政治", Arrays.asList("政治", "马原", "毛概", "思修", "史纲", "时政"));
        semanticMap.put("真题", Arrays.asList("真题", "历年", "试卷", "模拟"));
        semanticMap.put("基础", Arrays.asList("基础", "入门", "教材", "讲义"));
        semanticMap.put("冲刺", Arrays.asList("冲刺", "押题", "密卷", "预测"));

        for (Map.Entry<String, List<String>> entry : semanticMap.entrySet()) {
            if (lower.contains(entry.getKey())) {
                keywords.addAll(entry.getValue());
            }
        }

        return keywords.stream().distinct().collect(Collectors.toList());
    }

    private int calculateSearchRelevanceScore(Material material, String keyword, String understoodIntent) {
        int score = 0;
        String lowerKeyword = keyword.toLowerCase();
        String lowerIntent = understoodIntent.toLowerCase();
        String name = material.getName() != null ? material.getName().toLowerCase() : "";
        String desc = material.getDescription() != null ? material.getDescription().toLowerCase() : "";
        String category = material.getCategory() != null ? material.getCategory().toLowerCase() : "";
        String tags = material.getTags() != null ? material.getTags().toLowerCase() : "";

        if (name.contains(lowerKeyword)) score += 10;
        if (name.contains(lowerIntent) && !lowerIntent.equals(lowerKeyword)) score += 8;
        if (category.contains(lowerKeyword)) score += 6;
        if (tags.contains(lowerKeyword)) score += 5;
        if (desc.contains(lowerKeyword)) score += 3;

        score += material.getSales() != null ? Math.min(material.getSales() / 10, 5) : 0;

        return score;
    }

    private int calculateSimilarityScore(Material source, Material target) {
        int score = 0;

        if (source.getCategory() != null && target.getCategory() != null
                && source.getCategory().equals(target.getCategory())) {
            score += 10;
        }

        if (source.getTags() != null && target.getTags() != null) {
            Set<String> sourceTags = new HashSet<>(Arrays.asList(source.getTags().split(",")));
            Set<String> targetTags = new HashSet<>(Arrays.asList(target.getTags().split(",")));
            sourceTags.retainAll(targetTags);
            score += sourceTags.size() * 3;
        }

        if (source.getApplyYear() != null && target.getApplyYear() != null
                && source.getApplyYear().equals(target.getApplyYear())) {
            score += 2;
        }

        score += target.getSales() != null ? Math.min(target.getSales() / 20, 3) : 0;

        return score;
    }

    private AiTagResultDTO generateLocalTags(Material material) {
        AiTagResultDTO result = new AiTagResultDTO();
        List<String> tags = new ArrayList<>();

        String name = material.getName() != null ? material.getName() : "";
        String desc = material.getDescription() != null ? material.getDescription() : "";
        String category = material.getCategory() != null ? material.getCategory() : "";

        if (name.contains("数学") || desc.contains("数学") || category.contains("数学")) {
            tags.addAll(Arrays.asList("数学", "考研数学"));
            if (name.contains("高数") || desc.contains("高等数学")) tags.add("高等数学");
            if (name.contains("线代") || desc.contains("线性代数")) tags.add("线性代数");
            if (name.contains("概率") || desc.contains("概率论")) tags.add("概率论");
        }

        if (name.contains("英语") || desc.contains("英语") || category.contains("英语")) {
            tags.addAll(Arrays.asList("英语", "考研英语"));
            if (name.contains("词汇") || name.contains("单词")) tags.add("词汇");
            if (name.contains("阅读")) tags.add("阅读理解");
            if (name.contains("作文") || name.contains("写作")) tags.add("作文");
        }

        if (name.contains("政治") || desc.contains("政治") || category.contains("政治")) {
            tags.addAll(Arrays.asList("政治", "考研政治"));
            if (name.contains("肖") || desc.contains("肖秀荣")) tags.add("肖秀荣");
        }

        if (name.contains("真题") || desc.contains("真题")) tags.add("真题");
        if (name.contains("模拟") || desc.contains("模拟")) tags.add("模拟题");
        if (name.contains("基础") || desc.contains("基础")) tags.add("基础");
        if (name.contains("冲刺") || desc.contains("冲刺")) tags.add("冲刺");
        if (name.contains("强化") || desc.contains("强化")) tags.add("强化");
        if (name.contains("PDF") || material.getFileFormat() != null && material.getFileFormat().contains("PDF")) tags.add("PDF");
        if (name.contains("视频") || material.getFileFormat() != null && material.getFileFormat().contains("视频")) tags.add("视频");

        if (tags.isEmpty()) {
            tags.add(category);
            tags.add("考研资料");
        }

        result.setTags(tags.stream().distinct().collect(Collectors.toList()));
        result.setRecommendedCategory(category.isEmpty() ? "综合" : category);
        result.setSummary(desc.length() > 100 ? desc.substring(0, 100) + "..." : desc);
        result.setTargetAudience(Arrays.asList("考研学生", category + "考生"));
        result.setDifficultyLevel("中等");

        return result;
    }

    private void recordSearchBehavior(Long userId, String keyword) {
        try {
            UserBehavior behavior = new UserBehavior();
            behavior.setUserId(userId);
            behavior.setBehaviorType(UserBehavior.TYPE_SEARCH);
            behavior.setTargetType(1L);
            behavior.setTargetId(0L);
            behavior.setTargetTitle(keyword);
            behavior.setCreateTime(java.time.LocalDateTime.now());
            userBehaviorMapper.insert(behavior);
        } catch (Exception e) {
            // 记录搜索行为失败不影响主流程
        }
    }

    private String callAiForSearchIntent(String keyword) {
        try {
            List<Map<String, String>> messages = new ArrayList<>();
            messages.add(Map.of("role", "system", "content", SEARCH_INTENT_PROMPT));
            messages.add(Map.of("role", "user", "content", "搜索关键词：" + keyword));

            Map<String, Object> requestBody = new HashMap<>();
            requestBody.put("model", model);
            requestBody.put("messages", messages);
            requestBody.put("max_tokens", 500);
            requestBody.put("temperature", 0.3);
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
            // AI调用失败
        }
        return null;
    }

    private String callAiForTagGeneration(Material material) {
        try {
            String content = String.format("资料名称：%s\n描述：%s\n现有分类：%s",
                    material.getName(),
                    material.getDescription() != null ? material.getDescription() : "",
                    material.getCategory() != null ? material.getCategory() : "");

            List<Map<String, String>> messages = new ArrayList<>();
            messages.add(Map.of("role", "system", "content", TAG_GENERATE_PROMPT));
            messages.add(Map.of("role", "user", "content", content));

            Map<String, Object> requestBody = new HashMap<>();
            requestBody.put("model", model);
            requestBody.put("messages", messages);
            requestBody.put("max_tokens", 800);
            requestBody.put("temperature", 0.3);
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
            // AI调用失败
        }
        return null;
    }
}
