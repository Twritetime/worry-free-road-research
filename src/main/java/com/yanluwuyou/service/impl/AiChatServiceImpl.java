package com.yanluwuyou.service.impl;

import cn.hutool.core.util.StrUtil;
import cn.hutool.http.HttpRequest;
import cn.hutool.http.HttpResponse;
import cn.hutool.json.JSONArray;
import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.yanluwuyou.entity.ChatMessage;
import com.yanluwuyou.entity.Material;
import com.yanluwuyou.mapper.ChatMessageMapper;
import com.yanluwuyou.mapper.MaterialMapper;
import com.yanluwuyou.service.AiChatService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

@Service
public class AiChatServiceImpl implements AiChatService {

    @Autowired
    private ChatMessageMapper chatMessageMapper;

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

    @Value("${ai.deepseek.max-tokens:1000}")
    private int maxTokens;

    @Value("${ai.deepseek.temperature:0.7}")
    private double temperature;

    private static final String SYSTEM_PROMPT = "你是【研路无忧】平台的AI智能助手，专门帮助考研学生解答考研相关问题。\n\n" +
            "【核心规则 - 必须遵守】\n" +
            "1. 你只负责回答考研政策、备考方法、院校选择、复习规划等问题。\n" +
            "2. 你不需要推荐平台资料，因为平台资料推荐会由系统单独处理。\n" +
            "3. 如果用户询问资料相关问题，你只需要简要说明，不要列出具体资料名称和价格。\n\n" +
            "【绝对禁止】\n" +
            "1. 严禁编造平台的客服联系方式、邮箱、电话、公众号、QQ群等信息！\n" +
            "2. 严禁编造平台没有的功能、服务、活动等信息！\n" +
            "3. 严禁编造资料的特点、内容描述、适用人群等！\n" +
            "4. 如果用户询问客服/联系方式/投诉/退款等，统一回复：\"请联系平台管理员或前往个人中心-帮助与反馈提交问题。\"\n" +
            "5. 不要编造任何你不知道的平台具体信息！\n\n" +
            "【重要】\n" +
            "回答要简洁，只回答用户的问题本身，不要主动推荐资料。";

    @Override
    public ChatMessage sendMessage(Long userId, String content) {
        String sessionId = getOrCreateSessionId(userId);

        ChatMessage userMessage = new ChatMessage();
        userMessage.setUserId(userId);
        userMessage.setSessionId(sessionId);
        userMessage.setRole(ChatMessage.ROLE_USER);
        userMessage.setContent(content);
        userMessage.setCreateTime(LocalDateTime.now());
        chatMessageMapper.insert(userMessage);

        String aiResponse = getAiResponse(userId, sessionId, content);

        ChatMessage assistantMessage = new ChatMessage();
        assistantMessage.setUserId(userId);
        assistantMessage.setSessionId(sessionId);
        assistantMessage.setRole(ChatMessage.ROLE_ASSISTANT);
        assistantMessage.setContent(aiResponse);
        assistantMessage.setCreateTime(LocalDateTime.now());
        chatMessageMapper.insert(assistantMessage);

        return assistantMessage;
    }

    @Override
    public List<ChatMessage> getHistory(Long userId, String sessionId, int limit) {
        if (StrUtil.isBlank(sessionId)) {
            LambdaQueryWrapper<ChatMessage> latestWrapper = new LambdaQueryWrapper<>();
            latestWrapper.eq(ChatMessage::getUserId, userId)
                    .orderByDesc(ChatMessage::getCreateTime)
                    .last("LIMIT 1");
            List<ChatMessage> latestMessages = chatMessageMapper.selectList(latestWrapper);
            if (latestMessages.isEmpty()) {
                return new ArrayList<>();
            }
            sessionId = latestMessages.get(0).getSessionId();
        }
        LambdaQueryWrapper<ChatMessage> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(ChatMessage::getUserId, userId)
                .eq(ChatMessage::getSessionId, sessionId)
                .orderByAsc(ChatMessage::getCreateTime)
                .last("LIMIT " + limit);
        return chatMessageMapper.selectList(queryWrapper);
    }

    @Override
    public void clearHistory(Long userId, String sessionId) {
        LambdaQueryWrapper<ChatMessage> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(ChatMessage::getUserId, userId)
                .eq(ChatMessage::getSessionId, sessionId);
        chatMessageMapper.delete(queryWrapper);
    }

    private String getOrCreateSessionId(Long userId) {
        LambdaQueryWrapper<ChatMessage> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(ChatMessage::getUserId, userId)
                .orderByDesc(ChatMessage::getCreateTime)
                .last("LIMIT 1");
        List<ChatMessage> messages = chatMessageMapper.selectList(queryWrapper);
        if (messages.isEmpty()) {
            return UUID.randomUUID().toString();
        }
        return messages.get(0).getSessionId();
    }

    private String getAiResponse(Long userId, String sessionId, String userMessage) {
        if (!aiEnabled || StrUtil.isBlank(apiKey)) {
            List<Material> materials = searchRelatedMaterials(userMessage);
            return getFallbackResponseWithMaterials(userMessage, materials);
        }

        try {
            List<ChatMessage> history = getHistoryForApi(userId, sessionId, 10);
            List<Material> relatedMaterials = searchRelatedMaterials(userMessage);

            String enhancedPrompt = SYSTEM_PROMPT;

            List<Map<String, String>> messages = new ArrayList<>();
            messages.add(Map.of("role", "system", "content", enhancedPrompt));

            for (ChatMessage msg : history) {
                String role = msg.getRole() == ChatMessage.ROLE_USER ? "user" : "assistant";
                messages.add(Map.of("role", role, "content", msg.getContent()));
            }
            messages.add(Map.of("role", "user", "content", userMessage));

            Map<String, Object> requestBody = new HashMap<>();
            requestBody.put("model", model);
            requestBody.put("messages", messages);
            requestBody.put("max_tokens", maxTokens);
            requestBody.put("temperature", 0.3);

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
                        String aiContent = message.getStr("content");
                        if (relatedMaterials != null && !relatedMaterials.isEmpty()) {
                            aiContent = aiContent + "\n\n" + buildMaterialLinks(relatedMaterials);
                        }
                        return aiContent;
                    }
                }
            }

            return getFallbackResponseWithMaterials(userMessage, relatedMaterials);

        } catch (Exception e) {
            List<Material> relatedMaterials = searchRelatedMaterials(userMessage);
            return getFallbackResponseWithMaterials(userMessage, relatedMaterials);
        }
    }

    private List<Material> searchRelatedMaterials(String userMessage) {
        String msg = userMessage.toLowerCase();
        LambdaQueryWrapper<Material> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Material::getStatus, 1)
                .gt(Material::getStock, 0);

        boolean hasSpecificKeyword = true;

        if (msg.contains("数学") || msg.contains("高数") || msg.contains("微积分") || msg.contains("数一") || msg.contains("数二") || msg.contains("数三")) {
            wrapper.and(w -> w.like(Material::getCategory, "数学")
                    .or().like(Material::getName, "数学")
                    .or().like(Material::getName, "高数")
                    .or().like(Material::getName, "微积分")
                    .or().like(Material::getTags, "数学"));
        } else if (msg.contains("英语") || msg.contains("单词") || msg.contains("词汇") || msg.contains("阅读") || msg.contains("作文") || msg.contains("翻译") || msg.contains("完形") || msg.contains("英一") || msg.contains("英二")) {
            wrapper.and(w -> w.like(Material::getCategory, "英语")
                    .or().like(Material::getName, "英语")
                    .or().like(Material::getName, "单词")
                    .or().like(Material::getName, "词汇")
                    .or().like(Material::getName, "阅读")
                    .or().like(Material::getName, "作文")
                    .or().like(Material::getTags, "英语"));
        } else if (msg.contains("政治") || msg.contains("马原") || msg.contains("毛概") || msg.contains("思修") || msg.contains("史纲") || msg.contains("时政") || msg.contains("肖")) {
            wrapper.and(w -> w.like(Material::getCategory, "政治")
                    .or().like(Material::getName, "政治")
                    .or().like(Material::getName, "肖")
                    .or().like(Material::getName, "马原")
                    .or().like(Material::getTags, "政治"));
        } else if (msg.contains("线代") || msg.contains("线性代数")) {
            wrapper.and(w -> w.like(Material::getName, "线代")
                    .or().like(Material::getName, "线性代数")
                    .or().like(Material::getTags, "线代"));
        } else if (msg.contains("概率") || msg.contains("概率论")) {
            wrapper.and(w -> w.like(Material::getName, "概率")
                    .or().like(Material::getTags, "概率"));
        } else if (msg.contains("专业课") || msg.contains("专业")) {
            wrapper.like(Material::getCategory, "专业课");
        } else if (msg.contains("计算机") || msg.contains("408") || msg.contains("数据结构")) {
            wrapper.and(w -> w.like(Material::getCategory, "计算机")
                    .or().like(Material::getName, "计算机")
                    .or().like(Material::getName, "408")
                    .or().like(Material::getName, "数据结构")
                    .or().like(Material::getTags, "计算机"));
        } else if (msg.contains("真题") || msg.contains("历年") || msg.contains("试卷")) {
            wrapper.and(w -> w.like(Material::getName, "真题")
                    .or().like(Material::getName, "试卷")
                    .or().like(Material::getTags, "真题"));
        } else if (msg.contains("模拟") || msg.contains("押题") || msg.contains("预测")) {
            wrapper.and(w -> w.like(Material::getName, "模拟")
                    .or().like(Material::getName, "押题")
                    .or().like(Material::getName, "预测")
                    .or().like(Material::getTags, "模拟"));
        } else if (msg.contains("基础") || msg.contains("入门") || msg.contains("教材")) {
            wrapper.and(w -> w.like(Material::getName, "基础")
                    .or().like(Material::getName, "入门")
                    .or().like(Material::getName, "教材")
                    .or().like(Material::getTags, "基础"));
        } else if (msg.contains("冲刺") || msg.contains("密卷")) {
            wrapper.and(w -> w.like(Material::getName, "冲刺")
                    .or().like(Material::getName, "密卷")
                    .or().like(Material::getTags, "冲刺"));
        } else if (msg.contains("资料") || msg.contains("购买") || msg.contains("推荐") || msg.contains("书") || msg.contains("课程")) {
            return getAllMaterials();
        } else {
            hasSpecificKeyword = false;
        }

        List<Material> result = materialMapper.selectList(wrapper.last("LIMIT 15"));

        if (result.isEmpty() && !hasSpecificKeyword) {
            wrapper = new LambdaQueryWrapper<>();
            wrapper.eq(Material::getStatus, 1).gt(Material::getStock, 0);
            wrapper.and(w -> w.like(Material::getName, userMessage)
                    .or().like(Material::getCategory, userMessage)
                    .or().like(Material::getTags, userMessage)
                    .or().like(Material::getDescription, userMessage));
            result = materialMapper.selectList(wrapper.last("LIMIT 15"));
        }

        if (result.isEmpty()) {
            result = getAllMaterials();
        }

        return result;
    }

    private List<Material> getAllMaterials() {
        LambdaQueryWrapper<Material> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Material::getStatus, 1)
                .gt(Material::getStock, 0)
                .orderByDesc(Material::getSales)
                .last("LIMIT 30");
        return materialMapper.selectList(wrapper);
    }

    private String getFallbackResponseWithMaterials(String userMessage, List<Material> materials) {
        String msg = userMessage.toLowerCase();

        if (msg.contains("报名") || msg.contains("时间") || msg.contains("什么时候")) {
            return "📅 **考研报名时间**\n\n" +
                    "• 预报名：通常在**9月下旬**（应届本科毕业生）\n" +
                    "• 正式报名：通常在**10月**（所有考生）\n" +
                    "• 报名入口：中国研究生招生信息网（研招网）\n\n" +
                    "💡 建议提前准备好报名所需的证件和信息哦！";
        }

        if (msg.contains("院校") || msg.contains("学校") || msg.contains("择校")) {
            return "🏫 **择校建议**\n\n" +
                    "选择院校时可以考虑以下因素：\n" +
                    "1. **专业排名**：选择目标专业排名靠前的院校\n" +
                    "2. **地理位置**：一线城市机会多，但竞争也大\n" +
                    "3. **报录比**：了解往年录取比例\n" +
                    "4. **自身基础**：量力而行，避免好高骛远\n\n" +
                    "你可以告诉我你的专业方向、本科背景，我帮你推荐合适的院校！";
        }

        if (msg.contains("复习") || msg.contains("备考") || msg.contains("计划")) {
            StringBuilder response = new StringBuilder();
            response.append("📚 **备考规划建议**\n\n");
            response.append("**基础阶段（现在-6月）**\n");
            response.append("• 英语：背单词，做真题阅读\n");
            response.append("• 数学：过一遍教材，做课后题\n");
            response.append("• 政治：先不着急\n\n");
            response.append("**强化阶段（7-9月）**\n");
            response.append("• 开始政治复习\n");
            response.append("• 数学大量做题\n");
            response.append("• 专业课深入学习\n\n");
            response.append("**冲刺阶段（10-12月）**\n");
            response.append("• 模拟题、真题反复做\n");
            response.append("• 背诵政治大题\n");
            response.append("• 调整心态\n\n");

            if (!materials.isEmpty()) {
                response.append("📖 **相关复习资料**：\n");
                for (Material m : materials) {
                    String price = m.getPrice() != null ? m.getPrice().toString() : "未知";
                    response.append(String.format("[%s](path=/materials/%d) - %s元\n",
                            m.getName(), m.getId(), price));
                }
            }

            return response.toString();
        }

        if (msg.contains("数学")) {
            StringBuilder response = new StringBuilder();
            response.append("📐 **考研数学复习建议**\n\n");
            response.append("**老师推荐**：\n");
            response.append("• 高数：汤家凤、张宇、武忠祥\n");
            response.append("• 线代：李永乐（线代王）\n");
            response.append("• 概率：王式安\n\n");

            if (!materials.isEmpty()) {
                response.append("📖 **平台数学资料**：\n");
                for (Material m : materials) {
                    String price = m.getPrice() != null ? m.getPrice().toString() : "未知";
                    response.append(String.format("[%s](path=/materials/%d) - %s元\n",
                            m.getName(), m.getId(), price));
                }
            } else {
                response.append("**资料推荐**：\n");
                response.append("• 《复习全书》（李永乐）\n");
                response.append("• 《张宇18讲》\n");
                response.append("• 《660题》《330题》\n");
            }

            response.append("\n数学最重要的是**多做题、多总结**！");
            return response.toString();
        }

        if (msg.contains("英语")) {
            StringBuilder response = new StringBuilder();
            response.append("📝 **考研英语复习建议**\n\n");
            response.append("**单词**：\n");
            response.append("• 《恋练有词》或墨墨背单词APP\n");
            response.append("• 每天坚持背，重复是关键\n\n");
            response.append("**阅读**（占比最大）：\n");
            response.append("• 精读真题阅读文章\n");
            response.append("• 推荐《黄皮书》或《考研真相》\n\n");
            response.append("**作文**：\n");
            response.append("• 背模板不如背功能句\n");
            response.append("• 考前多写多练\n\n");

            if (!materials.isEmpty()) {
                response.append("📖 **平台英语资料**：\n");
                for (Material m : materials) {
                    String price = m.getPrice() != null ? m.getPrice().toString() : "未知";
                    response.append(String.format("[%s](path=/materials/%d) - %s元\n",
                            m.getName(), m.getId(), price));
                }
            }

            return response.toString();
        }

        if (msg.contains("政治")) {
            StringBuilder response = new StringBuilder();
            response.append("📖 **考研政治复习建议**\n\n");
            response.append("**复习时间**：\n");
            response.append("• 不用太早开始，7-8月开始足够\n\n");
            response.append("**老师推荐**：\n");
            response.append("• 徐涛（强化班）- 生动有趣\n");
            response.append("• 肖秀荣（资料权威）\n");
            response.append("• 腿姐（技巧班很强）\n\n");

            if (!materials.isEmpty()) {
                response.append("📖 **平台政治资料**：\n");
                for (Material m : materials) {
                    String price = m.getPrice() != null ? m.getPrice().toString() : "未知";
                    response.append(String.format("[%s](path=/materials/%d) - %s元\n",
                            m.getName(), m.getId(), price));
                }
            } else {
                response.append("**必备资料**：\n");
                response.append("• 《肖秀荣1000题》\n");
                response.append("• 《肖八》《肖四》必做！\n");
            }

            response.append("\n政治得高分的关键是**选择题**，大题背肖四就够了！");
            return response.toString();
        }

        if (msg.contains("专业课")) {
            StringBuilder response = new StringBuilder();
            response.append("📕 **专业课复习建议**\n\n");
            response.append("**获取资料渠道**：\n");
            response.append("1. 目标院校研究生院官网（招生简章、大纲）\n");
            response.append("2. 考研论坛、QQ群（学长学姐）\n");
            response.append("3. 校内打印店\n\n");
            response.append("**复习方法**：\n");
            response.append("• 找到历年真题，了解出题风格\n");
            response.append("• 整理重点笔记\n");
            response.append("• 反复背诵\n\n");

            if (!materials.isEmpty()) {
                response.append("📖 **平台专业课资料**：\n");
                for (Material m : materials) {
                    String price = m.getPrice() != null ? m.getPrice().toString() : "未知";
                    response.append(String.format("[%s](path=/materials/%d) - %s元\n",
                            m.getName(), m.getId(), price));
                }
            }

            return response.toString();
        }

        if (msg.contains("客服") || msg.contains("联系") || msg.contains("投诉") || msg.contains("退款") || msg.contains("售后")) {
            return "📞 **联系平台**\n\n" +
                    "如需帮助，请通过以下方式联系：\n" +
                    "• 前往【个人中心】→【帮助与反馈】提交问题\n" +
                    "• 或联系平台管理员\n\n" +
                    "💡 提交问题时请描述清楚你的需求，我们会尽快处理。";
        }

        if (msg.contains("调剂") || msg.contains("复试")) {
            return "🔄 **复试与调剂建议**\n\n" +
                    "**复试准备**：\n" +
                    "• 专业课笔试：复习初试内容\n" +
                    "• 英语口语：准备自我介绍和常见问答\n" +
                    "• 综合面试：了解专业前沿动态\n\n" +
                    "**调剂流程**：\n" +
                    "• 研招网调剂系统开放后填报\n" +
                    "• 提前联系目标院校\n" +
                    "• 注意时间节点！\n\n" +
                    "初试成绩出来后尽早准备调剂，不要等到最后！";
        }

        if (msg.contains("资料") || msg.contains("购买") || msg.contains("推荐")) {
            if (!materials.isEmpty()) {
                StringBuilder response = new StringBuilder();
                response.append("🎯 **为你推荐以下资料**：\n\n");
                for (Material m : materials) {
                    String price = m.getPrice() != null ? m.getPrice().toString() : "未知";
                    String desc = m.getDescription() != null ? m.getDescription().substring(0, Math.min(30, m.getDescription().length())) : "";
                    response.append(String.format("📚 [%s](path=/materials/%d)\n   价格：%s元 | 分类：%s\n   %s...\n\n",
                            m.getName(), m.getId(), price,
                            m.getCategory() != null ? m.getCategory() : "未分类",
                            desc));
                }
                response.append("[查看全部资料](path=/materials)");
                return response.toString();
            } else {
                List<Material> allMaterials = getAllMaterials();
                if (!allMaterials.isEmpty()) {
                    StringBuilder response = new StringBuilder();
                    response.append("📦 **平台现有资料**（共").append(allMaterials.size()).append("件）：\n\n");
                    for (Material m : allMaterials) {
                        String price = m.getPrice() != null ? m.getPrice().toString() : "未知";
                        response.append(String.format("📚 [%s](path=/materials/%d) - %s元 [%s]\n",
                                m.getName(), m.getId(), price,
                                m.getCategory() != null ? m.getCategory() : "未分类"));
                    }
                    response.append("\n[查看全部资料](path=/materials)");
                    return response.toString();
                }
                return "抱歉，平台目前暂无资料。敬请期待！";
            }
        }

        if (!materials.isEmpty()) {
            StringBuilder response = new StringBuilder();
            response.append("📚 **相关资料**：\n\n");
            for (Material m : materials) {
                String price = m.getPrice() != null ? m.getPrice().toString() : "未知";
                response.append(String.format("[%s](path=/materials/%d) - %s元 [%s]\n",
                        m.getName(), m.getId(), price,
                        m.getCategory() != null ? m.getCategory() : "未分类"));
            }
            response.append("\n[查看全部资料](path=/materials)");
            return response.toString();
        }

        return "👋 你好！我是【研路无忧】的考研AI助手。\n\n" +
                "我能帮你：\n" +
                "• 解答考研政策、院校选择问题\n" +
                "• 提供备考规划、各科复习方法\n" +
                "• 推荐平台资料、解答复试调剂问题\n\n" +
                "❗ 请注意：我不会提供平台的客服联系方式，如有售后问题请前往个人中心-帮助与反馈。\n\n" +
                "请直接说你想问的问题，我会直接回答！";
    }

    private List<ChatMessage> getHistoryForApi(Long userId, String sessionId, int limit) {
        LambdaQueryWrapper<ChatMessage> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(ChatMessage::getUserId, userId)
                .eq(ChatMessage::getSessionId, sessionId)
                .orderByAsc(ChatMessage::getCreateTime)
                .last("LIMIT " + limit);
        return chatMessageMapper.selectList(queryWrapper);
    }

    /**
     * 验证并修复AI回答中的链接
     * 1. 提取AI回答中提到的资料名称
     * 2. 在真实资料列表中查找匹配项
     * 3. 将AI生成的链接替换为正确的链接（使用真实ID）
     * 4. 删除AI编造的不存在资料的链接
     */
    private String validateAndFixLinks(String aiContent, List<Material> validMaterials) {
        if (StrUtil.isBlank(aiContent) || validMaterials == null || validMaterials.isEmpty()) {
            return aiContent;
        }

        Map<String, Material> nameToMaterial = new HashMap<>();
        Map<Long, Material> idToMaterial = new HashMap<>();
        for (Material m : validMaterials) {
            if (m.getName() != null) {
                nameToMaterial.put(m.getName().trim(), m);
                nameToMaterial.put(m.getName().trim().toLowerCase(), m);
            }
            if (m.getId() != null) {
                idToMaterial.put(m.getId(), m);
            }
        }

        String fixed = aiContent;

        Pattern linkPattern1 = Pattern.compile("\\[([^\\]]+)\\]\\(path=/materials&id=(\\d+)\\)");
        Matcher matcher1 = linkPattern1.matcher(fixed);
        StringBuffer result1 = new StringBuffer();
        while (matcher1.find()) {
            String linkText = matcher1.group(1).trim();
            String idStr = matcher1.group(2);
            long linkId;
            try {
                linkId = Long.parseLong(idStr);
            } catch (NumberFormatException e) {
                matcher1.appendReplacement(result1, Matcher.quoteReplacement(linkText));
                continue;
            }
            Material matchedById = idToMaterial.get(linkId);
            Material matchedByName = nameToMaterial.get(linkText);
            if (matchedByName == null) {
                matchedByName = nameToMaterial.get(linkText.toLowerCase());
            }
            if (matchedById != null && matchedById.getName().equals(linkText)) {
                String replacement = String.format("[%s](path=/materials/%d)",
                        matchedById.getName(), matchedById.getId());
                matcher1.appendReplacement(result1, Matcher.quoteReplacement(replacement));
            } else if (matchedByName != null) {
                String replacement = String.format("[%s](path=/materials/%d)",
                        matchedByName.getName(), matchedByName.getId());
                matcher1.appendReplacement(result1, Matcher.quoteReplacement(replacement));
            } else {
                matcher1.appendReplacement(result1, Matcher.quoteReplacement(linkText));
            }
        }
        matcher1.appendTail(result1);
        fixed = result1.toString();

        Pattern linkPattern2 = Pattern.compile("\\[([^\\]]+)\\]\\(path=/materials/detail/(\\d+)\\)");
        Matcher matcher2 = linkPattern2.matcher(fixed);
        StringBuffer result2 = new StringBuffer();
        while (matcher2.find()) {
            String linkText = matcher2.group(1).trim();
            String idStr = matcher2.group(2);
            long linkId;
            try {
                linkId = Long.parseLong(idStr);
            } catch (NumberFormatException e) {
                matcher2.appendReplacement(result2, Matcher.quoteReplacement(linkText));
                continue;
            }
            Material matchedById = idToMaterial.get(linkId);
            Material matchedByName = nameToMaterial.get(linkText);
            if (matchedByName == null) {
                matchedByName = nameToMaterial.get(linkText.toLowerCase());
            }
            if (matchedById != null && matchedById.getName().equals(linkText)) {
                String replacement = String.format("[%s](path=/materials/%d)",
                        matchedById.getName(), matchedById.getId());
                matcher2.appendReplacement(result2, Matcher.quoteReplacement(replacement));
            } else if (matchedByName != null) {
                String replacement = String.format("[%s](path=/materials/%d)",
                        matchedByName.getName(), matchedByName.getId());
                matcher2.appendReplacement(result2, Matcher.quoteReplacement(replacement));
            } else {
                matcher2.appendReplacement(result2, Matcher.quoteReplacement(linkText));
            }
        }
        matcher2.appendTail(result2);
        fixed = result2.toString();

        Pattern plainNamePattern = Pattern.compile("(?m)^[•\\-\\*]\\s*(.+?)(?:\\s+-\\s+\\d+\\.\\d+元?)?$");
        Matcher plainMatcher = plainNamePattern.matcher(fixed);
        StringBuffer result3 = new StringBuffer();
        while (plainMatcher.find()) {
            String lineText = plainMatcher.group(1).trim();
            String matchedName = null;
            for (String name : nameToMaterial.keySet()) {
                if (lineText.contains(name) || name.contains(lineText)) {
                    matchedName = name;
                    break;
                }
            }
            if (matchedName != null) {
                Material m = nameToMaterial.get(matchedName);
                String price = m.getPrice() != null ? m.getPrice().toString() : "未知";
                String replacement = String.format("• [%s](path=/materials/%d) - %s元 [%s]",
                        m.getName(), m.getId(), price,
                        m.getCategory() != null ? m.getCategory() : "未分类");
                plainMatcher.appendReplacement(result3, Matcher.quoteReplacement(replacement));
            } else {
                plainMatcher.appendReplacement(result3, Matcher.quoteReplacement(plainMatcher.group(0)));
            }
        }
        plainMatcher.appendTail(result3);
        fixed = result3.toString();

        Pattern pathPattern = Pattern.compile("\\[([^\\]]+)\\]\\(path=/materials/\\d+\\)");
        Matcher pathMatcher = pathPattern.matcher(fixed);
        boolean hasValidLinks = pathMatcher.find();

        if (!hasValidLinks && !validMaterials.isEmpty()) {
            fixed = fixed + "\n\n" + buildMaterialLinks(validMaterials);
        }

        return fixed;
    }

    private boolean hasValidMaterialLinks(String content) {
        if (StrUtil.isBlank(content)) {
            return false;
        }
        Pattern pattern = Pattern.compile("\\[([^\\]]+)\\]\\(path=/materials/\\d+\\)");
        Matcher matcher = pattern.matcher(content);
        return matcher.find();
    }

    private String buildMaterialLinks(List<Material> materials) {
        if (materials == null || materials.isEmpty()) {
            return "";
        }
        StringBuilder sb = new StringBuilder("📚 **平台相关资料推荐**：\n\n");
        int count = 0;
        for (Material m : materials) {
            if (count >= 5) break;
            String price = m.getPrice() != null ? m.getPrice().toString() : "未知";
            sb.append(String.format("• [%s](path=/materials/%d) - %s元 [%s]\n",
                    m.getName(), m.getId(), price,
                    m.getCategory() != null ? m.getCategory() : "未分类"));
            count++;
        }
        return sb.toString();
    }
}
