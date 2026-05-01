package com.yanluwuyou.service.impl;

import cn.hutool.core.util.StrUtil;
import cn.hutool.http.HttpRequest;
import cn.hutool.http.HttpResponse;
import cn.hutool.json.JSONArray;
import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.yanluwuyou.dto.StudyPlanDTO;
import com.yanluwuyou.dto.UserStudyProfileDTO;
import com.yanluwuyou.entity.Material;
import com.yanluwuyou.entity.User;
import com.yanluwuyou.entity.UserBehavior;
import com.yanluwuyou.mapper.MaterialMapper;
import com.yanluwuyou.mapper.UserBehaviorMapper;
import com.yanluwuyou.mapper.UserMapper;
import com.yanluwuyou.service.AiStudyAssistantService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class AiStudyAssistantServiceImpl implements AiStudyAssistantService {

    @Autowired
    private UserMapper userMapper;

    @Autowired
    private UserBehaviorMapper userBehaviorMapper;

    @Autowired
    private MaterialMapper materialMapper;

    @Value("${ai.deepseek.enabled:false}")
    private boolean aiEnabled;

    @Value("${ai.deepseek.api-key:}")
    private String apiKey;

    @Value("${ai.deepseek.base-url:https://api.deepseek.com}")
    private String baseUrl;

    @Value("${ai.deepseek.model:deepseek-chat}")
    private String model;

    private static final String STUDY_PLAN_PROMPT = "你是考研规划专家。根据用户的目标院校、专业、备考时间和每日学习时长，生成详细的个性化学习计划。\n" +
            "要求：\n" +
            "1. 计划要具体、可执行\n" +
            "2. 分阶段制定目标\n" +
            "3. 每个阶段列出具体任务\n" +
            "4. 输出JSON格式：{\"title\":\"计划标题\",\"totalDays\":180,\"dailyHours\":8,\"phases\":[{\"name\":\"阶段名\",\"durationDays\":60,\"goal\":\"阶段目标\",\"dailyTasks\":[\"任务1\",\"任务2\"],\"recommendMaterials\":[\"资料1\",\"资料2\"]}],\"recommendMaterialIds\":[],\"aiAdvice\":\"总结建议\"}";

    private static final String PROFILE_PROMPT = "你是学习行为分析专家。根据用户的学习行为数据，分析用户的学习画像。\n" +
            "要求：\n" +
            "1. 分析学习风格和偏好\n" +
            "2. 识别优势和薄弱科目\n" +
            "3. 给出个性化建议\n" +
            "4. 输出JSON格式：{\"studyStyleTags\":[\"标签1\",\"标签2\"],\"preferredSubjects\":[{\"subject\":\"科目\",\"weight\":0.8}],\"activeTimeDistribution\":{\"上午\":30,\"下午\":40,\"晚上\":30},\"progressAssessment\":\"进度评估\",\"weakSubjectSuggestions\":[\"建议1\"],\"aiPersonalizedAdvice\":\"个性化建议\",\"spendingLevel\":\"消费水平\",\"materialTypePreferences\":[\"偏好类型1\"]}";

    @Override
    public StudyPlanDTO generateStudyPlan(Long userId, String targetSchool, String targetMajor, Integer examYear, Integer dailyHours) {
        StudyPlanDTO plan = new StudyPlanDTO();

        if (aiEnabled && StrUtil.isNotBlank(apiKey)) {
            String aiResult = callAiForStudyPlan(targetSchool, targetMajor, examYear, dailyHours);
            if (StrUtil.isNotBlank(aiResult)) {
                try {
                    JSONObject json = JSONUtil.parseObj(aiResult);
                    plan.setTitle(json.getStr("title", "个性化考研学习计划"));
                    plan.setTotalDays(json.getInt("totalDays", 180));
                    plan.setDailyHours(json.getInt("dailyHours", dailyHours != null ? dailyHours : 8));

                    JSONArray phasesArray = json.getJSONArray("phases");
                    if (phasesArray != null) {
                        List<StudyPlanDTO.StudyPhase> phases = new ArrayList<>();
                        for (int i = 0; i < phasesArray.size(); i++) {
                            JSONObject phaseJson = phasesArray.getJSONObject(i);
                            StudyPlanDTO.StudyPhase phase = new StudyPlanDTO.StudyPhase();
                            phase.setName(phaseJson.getStr("name", "阶段" + (i + 1)));
                            phase.setDurationDays(phaseJson.getInt("durationDays", 30));
                            phase.setGoal(phaseJson.getStr("goal", ""));

                            JSONArray tasksArray = phaseJson.getJSONArray("dailyTasks");
                            if (tasksArray != null) {
                                List<String> tasks = new ArrayList<>();
                                for (int j = 0; j < tasksArray.size(); j++) {
                                    tasks.add(tasksArray.getStr(j));
                                }
                                phase.setDailyTasks(tasks);
                            }

                            JSONArray materialsArray = phaseJson.getJSONArray("recommendMaterials");
                            if (materialsArray != null) {
                                List<String> materials = new ArrayList<>();
                                for (int j = 0; j < materialsArray.size(); j++) {
                                    materials.add(materialsArray.getStr(j));
                                }
                                phase.setRecommendMaterials(materials);
                            }

                            phases.add(phase);
                        }
                        plan.setPhases(phases);
                    }

                    JSONArray materialIdsArray = json.getJSONArray("recommendMaterialIds");
                    if (materialIdsArray != null) {
                        List<Long> ids = new ArrayList<>();
                        for (int i = 0; i < materialIdsArray.size(); i++) {
                            ids.add(materialIdsArray.getLong(i));
                        }
                        plan.setRecommendMaterialIds(ids);
                    }

                    plan.setAiAdvice(json.getStr("aiAdvice", "坚持就是胜利！"));
                    return plan;
                } catch (Exception e) {
                    // 解析失败，使用默认计划
                }
            }
        }

        return generateDefaultStudyPlan(targetSchool, targetMajor, examYear, dailyHours);
    }

    @Override
    public UserStudyProfileDTO analyzeStudyProfile(Long userId) {
        UserStudyProfileDTO profile = new UserStudyProfileDTO();
        profile.setUserId(userId);

        User user = userMapper.selectById(userId);
        if (user != null) {
            profile.setNickname(user.getNickname() != null ? user.getNickname() : user.getUsername());
        }

        LambdaQueryWrapper<UserBehavior> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(UserBehavior::getUserId, userId)
                .orderByDesc(UserBehavior::getCreateTime)
                .last("LIMIT 200");
        List<UserBehavior> behaviors = userBehaviorMapper.selectList(wrapper);

        if (behaviors.isEmpty()) {
            profile.setStudyStyleTags(Arrays.asList("新用户", "待探索"));
            profile.setProgressAssessment("暂无足够数据，开始浏览和购买资料以获取个性化分析");
            profile.setAiPersonalizedAdvice("建议先完善个人信息，制定学习计划，平台将为你提供更精准的推荐。");
            profile.setSpendingLevel("未知");
            profile.setPreferredSubjects(new ArrayList<>());
            profile.setActiveTimeDistribution(new HashMap<>());
            profile.setWeakSubjectSuggestions(new ArrayList<>());
            profile.setMaterialTypePreferences(new ArrayList<>());
            return profile;
        }

        if (aiEnabled && StrUtil.isNotBlank(apiKey)) {
            String behaviorData = buildBehaviorData(behaviors);
            String aiResult = callAiForProfile(behaviorData);
            if (StrUtil.isNotBlank(aiResult)) {
                try {
                    JSONObject json = JSONUtil.parseObj(aiResult);

                    JSONArray tagsArray = json.getJSONArray("studyStyleTags");
                    if (tagsArray != null) {
                        List<String> tags = new ArrayList<>();
                        for (int i = 0; i < tagsArray.size(); i++) {
                            tags.add(tagsArray.getStr(i));
                        }
                        profile.setStudyStyleTags(tags);
                    } else {
                        profile.setStudyStyleTags(Arrays.asList("勤奋型", "目标明确"));
                    }

                    JSONArray subjectsArray = json.getJSONArray("preferredSubjects");
                    if (subjectsArray != null) {
                        List<Map<String, Object>> subjects = new ArrayList<>();
                        for (int i = 0; i < subjectsArray.size(); i++) {
                            JSONObject sub = subjectsArray.getJSONObject(i);
                            Map<String, Object> map = new HashMap<>();
                            map.put("subject", sub.getStr("subject", ""));
                            map.put("weight", sub.getDouble("weight", 0.5));
                            subjects.add(map);
                        }
                        profile.setPreferredSubjects(subjects);
                    }

                    JSONObject timeObj = json.getJSONObject("activeTimeDistribution");
                    if (timeObj != null) {
                        Map<String, Integer> timeDist = new HashMap<>();
                        for (String key : timeObj.keySet()) {
                            timeDist.put(key, timeObj.getInt(key, 0));
                        }
                        profile.setActiveTimeDistribution(timeDist);
                    } else {
                        profile.setActiveTimeDistribution(calculateActiveTimeDistribution(behaviors));
                    }

                    profile.setProgressAssessment(json.getStr("progressAssessment", "学习进行中"));

                    JSONArray weakArray = json.getJSONArray("weakSubjectSuggestions");
                    if (weakArray != null) {
                        List<String> weak = new ArrayList<>();
                        for (int i = 0; i < weakArray.size(); i++) {
                            weak.add(weakArray.getStr(i));
                        }
                        profile.setWeakSubjectSuggestions(weak);
                    }

                    profile.setAiPersonalizedAdvice(json.getStr("aiPersonalizedAdvice", "继续加油！"));
                    profile.setSpendingLevel(json.getStr("spendingLevel", "中等"));

                    JSONArray typeArray = json.getJSONArray("materialTypePreferences");
                    if (typeArray != null) {
                        List<String> types = new ArrayList<>();
                        for (int i = 0; i < typeArray.size(); i++) {
                            types.add(typeArray.getStr(i));
                        }
                        profile.setMaterialTypePreferences(types);
                    }

                    return profile;
                } catch (Exception e) {
                    // 解析失败，使用本地分析
                }
            }
        }

        return buildLocalProfile(userId, behaviors);
    }

    @Override
    public String getDailyStudyAdvice(Long userId) {
        LambdaQueryWrapper<UserBehavior> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(UserBehavior::getUserId, userId)
                .orderByDesc(UserBehavior::getCreateTime)
                .last("LIMIT 50");
        List<UserBehavior> behaviors = userBehaviorMapper.selectList(wrapper);

        if (behaviors.isEmpty()) {
            return "📚 今天开始学习吧！建议先制定一个学习计划，明确目标院校和专业。然后浏览平台的资料商城，找到适合自己的复习资料。加油！💪";
        }

        long viewCount = behaviors.stream().filter(b -> b.getBehaviorType() == UserBehavior.TYPE_VIEW).count();
        long favCount = behaviors.stream().filter(b -> b.getBehaviorType() == UserBehavior.TYPE_FAVORITE).count();
        long buyCount = behaviors.stream().filter(b -> b.getBehaviorType() == UserBehavior.TYPE_PURCHASE).count();

        StringBuilder advice = new StringBuilder();
        advice.append("🌟 今日学习建议\n\n");

        if (buyCount == 0 && viewCount > 10) {
            advice.append("你最近浏览了很多资料，建议挑选1-2本最需要的资料入手，开始系统学习。\n\n");
        }

        if (favCount > 0 && favCount > buyCount) {
            advice.append("你收藏了不少资料，别忘了及时购买学习哦！收藏不等于掌握。\n\n");
        }

        Set<Long> targetIds = behaviors.stream()
                .map(UserBehavior::getTargetId)
                .collect(Collectors.toSet());

        if (!targetIds.isEmpty()) {
            LambdaQueryWrapper<Material> mq = new LambdaQueryWrapper<>();
            mq.in(Material::getId, targetIds);
            List<Material> materials = materialMapper.selectList(mq);

            Map<String, Integer> categoryCount = new HashMap<>();
            for (UserBehavior b : behaviors) {
                Material m = materials.stream()
                        .filter(mat -> mat.getId().equals(b.getTargetId()))
                        .findFirst().orElse(null);
                if (m != null && m.getCategory() != null) {
                    categoryCount.merge(m.getCategory(), 1, Integer::sum);
                }
            }

            if (!categoryCount.isEmpty()) {
                String topCategory = categoryCount.entrySet().stream()
                        .max(Map.Entry.comparingByValue())
                        .map(Map.Entry::getKey)
                        .orElse("");
                advice.append("你最近对【").append(topCategory).append("】比较关注，建议保持这个学习节奏。\n\n");

                List<String> lessCategories = categoryCount.entrySet().stream()
                        .sorted(Map.Entry.<String, Integer>comparingByValue())
                        .limit(2)
                        .map(Map.Entry::getKey)
                        .collect(Collectors.toList());

                if (!lessCategories.isEmpty()) {
                    advice.append("同时别忘了兼顾");
                    for (int i = 0; i < lessCategories.size(); i++) {
                        if (i > 0) advice.append("、");
                        advice.append("【").append(lessCategories.get(i)).append("】");
                    }
                    advice.append("的复习哦！\n\n");
                }
            }
        }

        advice.append("💡 今日任务建议：\n");
        advice.append("1. 完成昨日计划的复习内容\n");
        advice.append("2. 做一套真题或模拟题\n");
        advice.append("3. 整理错题笔记\n");
        advice.append("4. 背诵英语单词50个\n\n");
        advice.append("坚持就是胜利！加油！🎯");

        return advice.toString();
    }

    private StudyPlanDTO generateDefaultStudyPlan(String targetSchool, String targetMajor, Integer examYear, Integer dailyHours) {
        StudyPlanDTO plan = new StudyPlanDTO();
        plan.setTitle((targetSchool != null ? targetSchool : "目标院校") + " " + (targetMajor != null ? targetMajor : "目标专业") + " 考研学习计划");
        plan.setTotalDays(180);
        plan.setDailyHours(dailyHours != null ? dailyHours : 8);

        List<StudyPlanDTO.StudyPhase> phases = new ArrayList<>();

        StudyPlanDTO.StudyPhase phase1 = new StudyPlanDTO.StudyPhase();
        phase1.setName("基础阶段");
        phase1.setDurationDays(60);
        phase1.setGoal("打牢基础，全面覆盖考纲知识点");
        phase1.setDailyTasks(Arrays.asList(
                "英语：背单词100个，精读1篇阅读",
                "数学：复习教材章节，做课后习题",
                "政治：暂不开始",
                "专业课：通读教材，整理知识框架"
        ));
        phase1.setRecommendMaterials(Arrays.asList("基础教材", "单词书", "专业课教材"));
        phases.add(phase1);

        StudyPlanDTO.StudyPhase phase2 = new StudyPlanDTO.StudyPhase();
        phase2.setName("强化阶段");
        phase2.setDurationDays(90);
        phase2.setGoal("强化训练，提升解题能力");
        phase2.setDailyTasks(Arrays.asList(
                "英语：做真题阅读，整理作文模板",
                "数学：大量刷题，总结题型",
                "政治：看强化课，做1000题",
                "专业课：做历年真题，整理重点"
        ));
        phase2.setRecommendMaterials(Arrays.asList("真题集", "强化讲义", "1000题"));
        phases.add(phase2);

        StudyPlanDTO.StudyPhase phase3 = new StudyPlanDTO.StudyPhase();
        phase3.setName("冲刺阶段");
        phase3.setDurationDays(30);
        phase3.setGoal("查漏补缺，模拟实战");
        phase3.setDailyTasks(Arrays.asList(
                "英语：背诵作文，保持阅读手感",
                "数学：做模拟题，回顾错题",
                "政治：背肖四肖八，选择题冲刺",
                "专业课：反复背诵重点，模拟考试"
        ));
        phase3.setRecommendMaterials(Arrays.asList("肖四肖八", "模拟试卷", "冲刺讲义"));
        phases.add(phase3);

        plan.setPhases(phases);
        plan.setAiAdvice("考研是一场持久战，贵在坚持。基础阶段不要急于求成，强化阶段要多做题多总结，冲刺阶段要调整心态。祝你考上理想的" + (targetSchool != null ? targetSchool : "院校") + "！");
        plan.setRecommendMaterialIds(new ArrayList<>());

        return plan;
    }

    private UserStudyProfileDTO buildLocalProfile(Long userId, List<UserBehavior> behaviors) {
        UserStudyProfileDTO profile = new UserStudyProfileDTO();
        profile.setUserId(userId);

        User user = userMapper.selectById(userId);
        if (user != null) {
            profile.setNickname(user.getNickname() != null ? user.getNickname() : user.getUsername());
        }

        long viewCount = behaviors.stream().filter(b -> b.getBehaviorType() == UserBehavior.TYPE_VIEW).count();
        long favCount = behaviors.stream().filter(b -> b.getBehaviorType() == UserBehavior.TYPE_FAVORITE).count();
        long buyCount = behaviors.stream().filter(b -> b.getBehaviorType() == UserBehavior.TYPE_PURCHASE).count();

        List<String> tags = new ArrayList<>();
        if (buyCount > 5) tags.add("高消费用户");
        else if (buyCount > 0) tags.add("已购买用户");
        else tags.add("浏览型用户");

        if (viewCount > 50) tags.add("活跃学习者");
        if (favCount > 10) tags.add("收藏爱好者");
        tags.add("目标明确");
        profile.setStudyStyleTags(tags);

        profile.setActiveTimeDistribution(calculateActiveTimeDistribution(behaviors));

        Set<Long> targetIds = behaviors.stream()
                .map(UserBehavior::getTargetId)
                .collect(Collectors.toSet());

        List<Map<String, Object>> preferredSubjects = new ArrayList<>();
        if (!targetIds.isEmpty()) {
            LambdaQueryWrapper<Material> mq = new LambdaQueryWrapper<>();
            mq.in(Material::getId, targetIds);
            List<Material> materials = materialMapper.selectList(mq);

            Map<String, Integer> categoryCount = new HashMap<>();
            for (UserBehavior b : behaviors) {
                Material m = materials.stream()
                        .filter(mat -> mat.getId().equals(b.getTargetId()))
                        .findFirst().orElse(null);
                if (m != null && m.getCategory() != null) {
                    int score = b.getBehaviorType() == UserBehavior.TYPE_PURCHASE ? 3 :
                            b.getBehaviorType() == UserBehavior.TYPE_FAVORITE ? 2 : 1;
                    categoryCount.merge(m.getCategory(), score, Integer::sum);
                }
            }

            int totalScore = categoryCount.values().stream().mapToInt(Integer::intValue).sum();
            for (Map.Entry<String, Integer> entry : categoryCount.entrySet()) {
                Map<String, Object> map = new HashMap<>();
                map.put("subject", entry.getKey());
                map.put("weight", totalScore > 0 ? Math.round(entry.getValue() * 100.0 / totalScore) / 100.0 : 0.0);
                preferredSubjects.add(map);
            }

            preferredSubjects.sort((a, b) -> Double.compare((Double) b.get("weight"), (Double) a.get("weight")));
        }
        profile.setPreferredSubjects(preferredSubjects);

        profile.setProgressAssessment("已产生 " + behaviors.size() + " 次学习行为，包括 " + viewCount + " 次浏览、" + favCount + " 次收藏、" + buyCount + " 次购买");

        List<String> weakSuggestions = new ArrayList<>();
        if (buyCount == 0) weakSuggestions.add("建议购买一些核心资料，系统学习效果更好");
        if (viewCount > 20 && favCount == 0) weakSuggestions.add("遇到好的资料记得收藏，方便以后查找");
        if (preferredSubjects.size() < 3) weakSuggestions.add("建议多关注其他科目，均衡发展");
        if (weakSuggestions.isEmpty()) weakSuggestions.add("学习状态良好，继续保持！");
        profile.setWeakSubjectSuggestions(weakSuggestions);

        profile.setAiPersonalizedAdvice("根据你的学习行为，建议制定一个系统的学习计划，合理分配各科复习时间。坚持每日学习，定期回顾总结。");

        if (buyCount > 10) profile.setSpendingLevel("高");
        else if (buyCount > 3) profile.setSpendingLevel("中等");
        else if (buyCount > 0) profile.setSpendingLevel("低");
        else profile.setSpendingLevel("未消费");

        profile.setMaterialTypePreferences(Arrays.asList("PDF文档", "视频课程"));

        return profile;
    }

    private Map<String, Integer> calculateActiveTimeDistribution(List<UserBehavior> behaviors) {
        Map<String, Integer> distribution = new HashMap<>();
        distribution.put("早晨(6-9点)", 0);
        distribution.put("上午(9-12点)", 0);
        distribution.put("下午(12-18点)", 0);
        distribution.put("晚上(18-24点)", 0);
        distribution.put("深夜(0-6点)", 0);

        for (UserBehavior b : behaviors) {
            if (b.getCreateTime() == null) continue;
            int hour = b.getCreateTime().getHour();
            if (hour >= 6 && hour < 9) distribution.merge("早晨(6-9点)", 1, Integer::sum);
            else if (hour >= 9 && hour < 12) distribution.merge("上午(9-12点)", 1, Integer::sum);
            else if (hour >= 12 && hour < 18) distribution.merge("下午(12-18点)", 1, Integer::sum);
            else if (hour >= 18 && hour < 24) distribution.merge("晚上(18-24点)", 1, Integer::sum);
            else distribution.merge("深夜(0-6点)", 1, Integer::sum);
        }

        return distribution;
    }

    private String buildBehaviorData(List<UserBehavior> behaviors) {
        StringBuilder sb = new StringBuilder();
        sb.append("用户行为数据（最近").append(behaviors.size()).append("条）：\n");

        Set<Long> targetIds = behaviors.stream()
                .map(UserBehavior::getTargetId)
                .collect(Collectors.toSet());

        Map<Long, Material> materialMap = new HashMap<>();
        if (!targetIds.isEmpty()) {
            LambdaQueryWrapper<Material> mq = new LambdaQueryWrapper<>();
            mq.in(Material::getId, targetIds);
            List<Material> materials = materialMapper.selectList(mq);
            materialMap = materials.stream().collect(Collectors.toMap(Material::getId, m -> m));
        }

        Map<String, Integer> behaviorTypeCount = new HashMap<>();
        behaviorTypeCount.put("浏览", 0);
        behaviorTypeCount.put("收藏", 0);
        behaviorTypeCount.put("购买", 0);
        behaviorTypeCount.put("搜索", 0);

        for (UserBehavior b : behaviors) {
            String typeStr;
            switch (b.getBehaviorType()) {
                case 1: typeStr = "浏览"; break;
                case 2: typeStr = "收藏"; break;
                case 3: typeStr = "购买"; break;
                case 4: typeStr = "搜索"; break;
                default: typeStr = "其他";
            }
            behaviorTypeCount.merge(typeStr, 1, Integer::sum);

            Material m = materialMap.get(b.getTargetId());
            if (m != null) {
                sb.append("- ").append(typeStr).append(" ").append(m.getName())
                        .append(" (分类:").append(m.getCategory()).append(")\n");
            }
        }

        sb.append("\n行为统计：").append(behaviorTypeCount).append("\n");
        return sb.toString();
    }

    private String callAiForStudyPlan(String targetSchool, String targetMajor, Integer examYear, Integer dailyHours) {
        try {
            String prompt = String.format("目标院校：%s\n目标专业：%s\n考研年份：%d\n每日学习时长：%d小时",
                    targetSchool != null ? targetSchool : "待定",
                    targetMajor != null ? targetMajor : "待定",
                    examYear != null ? examYear : 2026,
                    dailyHours != null ? dailyHours : 8);

            List<Map<String, String>> messages = new ArrayList<>();
            messages.add(Map.of("role", "system", "content", STUDY_PLAN_PROMPT));
            messages.add(Map.of("role", "user", "content", prompt));

            Map<String, Object> requestBody = new HashMap<>();
            requestBody.put("model", model);
            requestBody.put("messages", messages);
            requestBody.put("max_tokens", 2000);
            requestBody.put("temperature", 0.7);
            requestBody.put("response_format", Map.of("type", "json_object"));

            HttpResponse response = HttpRequest.post(baseUrl + "/chat/completions")
                    .header("Content-Type", "application/json")
                    .header("Authorization", "Bearer " + apiKey)
                    .body(JSONUtil.toJsonStr(requestBody))
                    .timeout(15000)
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

    private String callAiForProfile(String behaviorData) {
        try {
            List<Map<String, String>> messages = new ArrayList<>();
            messages.add(Map.of("role", "system", "content", PROFILE_PROMPT));
            messages.add(Map.of("role", "user", "content", behaviorData));

            Map<String, Object> requestBody = new HashMap<>();
            requestBody.put("model", model);
            requestBody.put("messages", messages);
            requestBody.put("max_tokens", 1500);
            requestBody.put("temperature", 0.5);
            requestBody.put("response_format", Map.of("type", "json_object"));

            HttpResponse response = HttpRequest.post(baseUrl + "/chat/completions")
                    .header("Content-Type", "application/json")
                    .header("Authorization", "Bearer " + apiKey)
                    .body(JSONUtil.toJsonStr(requestBody))
                    .timeout(15000)
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
