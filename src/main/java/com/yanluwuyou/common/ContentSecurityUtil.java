package com.yanluwuyou.common;

import cn.hutool.core.util.StrUtil;
import org.springframework.stereotype.Component;

import java.util.*;
import java.util.regex.Pattern;

/**
 * 内容安全工具类
 * 提供敏感词检测、HTML内容清理、XSS防护等功能
 * 用于过滤用户输入中的敏感内容和恶意脚本
 */
@Component
public class ContentSecurityUtil {

    /**
     * 敏感词库
     * 包含枪支、毒品、赌博、作弊等违法违规词汇
     */
    private static final Set<String> SENSITIVE_WORDS = new HashSet<>(Arrays.asList(
            "枪支", "弹药", "毒品", "赌博", "卖淫", "反动", "分裂", "恐怖",
            "作弊", "泄题", "助考", "代考", "槍支", "嗎啡", "海洛因",
            "彩票", "赌球", "假证", "文凭", "枪版", "破解", "木马",
            "病毒", "钓鱼", "诈骗", "洗钱", "贿赂", "贪污", "走私"
    ));

    /**
     * 匹配<script>标签的正则表达式
     */
    private static final Pattern SCRIPT_PATTERN = Pattern.compile(
            "<script[^>]*?>[\\s\\S]*?<\\/script>", Pattern.CASE_INSENSITIVE);
    
    /**
     * 匹配HTML标签的正则表达式
     */
    private static final Pattern HTML_TAG_PATTERN = Pattern.compile(
            "<[^>]+>", Pattern.CASE_INSENSITIVE);
    
    /**
     * 匹配javascript:协议的正则表达式
     */
    private static final Pattern JAVASCRIPT_PATTERN = Pattern.compile(
            "javascript:", Pattern.CASE_INSENSITIVE);
    
    /**
     * 匹配onerror事件处理器的正则表达式
     */
    private static final Pattern ONERROR_PATTERN = Pattern.compile(
            "onerror\\s*=", Pattern.CASE_INSENSITIVE);
    
    /**
     * 匹配onclick事件处理器的正则表达式
     */
    private static final Pattern ONCLICK_PATTERN = Pattern.compile(
            "onclick\\s*=", Pattern.CASE_INSENSITIVE);
    
    /**
     * 匹配onload事件处理器的正则表达式
     */
    private static final Pattern ONLOAD_PATTERN = Pattern.compile(
            "onload\\s*=", Pattern.CASE_INSENSITIVE);

    /**
     * 安全检查结果
     * 包含检查是否通过、清理后的内容和检测到的敏感词列表
     */
    public static class SecurityResult {
        private boolean passed;
        private String sanitizedContent;
        private List<String> detectedWords;

        public SecurityResult(boolean passed, String sanitizedContent, List<String> detectedWords) {
            this.passed = passed;
            this.sanitizedContent = sanitizedContent;
            this.detectedWords = detectedWords;
        }

        public boolean isPassed() { return passed; }
        public String getSanitizedContent() { return sanitizedContent; }
        public List<String> getDetectedWords() { return detectedWords; }
    }

    /**
     * 检查内容安全性
     * 检测敏感词并清理HTML内容
     * 
     * @param content 待检查的内容
     * @return 安全检查结果
     */
    public SecurityResult checkContent(String content) {
        if (StrUtil.isBlank(content)) {
            return new SecurityResult(true, content, new ArrayList<>());
        }

        List<String> detectedWords = new ArrayList<>();
        String lowerContent = content.toLowerCase();

        for (String word : SENSITIVE_WORDS) {
            if (lowerContent.contains(word.toLowerCase())) {
                detectedWords.add(word);
            }
        }

        String sanitized = sanitizeHtml(content);

        return new SecurityResult(detectedWords.isEmpty(), sanitized, detectedWords);
    }

    /**
     * 清理HTML内容中的危险标签和脚本
     * 移除<script>、javascript:、事件处理器等潜在恶意内容
     * 
     * @param content 原始HTML内容
     * @return 清理后的安全内容
     */
    public String sanitizeHtml(String content) {
        if (StrUtil.isBlank(content)) {
            return content;
        }

        String sanitized = content;

        sanitized = SCRIPT_PATTERN.matcher(sanitized).replaceAll("");
        sanitized = JAVASCRIPT_PATTERN.matcher(sanitized).replaceAll("");
        sanitized = ONERROR_PATTERN.matcher(sanitized).replaceAll("");
        sanitized = ONCLICK_PATTERN.matcher(sanitized).replaceAll("");
        sanitized = ONLOAD_PATTERN.matcher(sanitized).replaceAll("");
        sanitized = HTML_TAG_PATTERN.matcher(sanitized).replaceAll("");

        return sanitized;
    }

    /**
     * 将HTML特殊字符转义为HTML实体
     * 防止XSS攻击
     * 
     * @param content 原始内容
     * @return 转义后的内容
     */
    public String escapeHtml(String content) {
        if (StrUtil.isBlank(content)) {
            return content;
        }

        return content.replace("&", "&amp;")
                     .replace("<", "&lt;")
                     .replace(">", "&gt;")
                     .replace("\"", "&quot;")
                     .replace("'", "&#x27;")
                     .replace("/", "&#x2F;");
    }

    /**
     * 检查内容是否包含敏感词
     * 
     * @param content 待检查的内容
     * @return true-包含敏感词, false-不包含
     */
    public boolean containsSensitiveWords(String content) {
        if (StrUtil.isBlank(content)) {
            return false;
        }

        String lowerContent = content.toLowerCase();
        for (String word : SENSITIVE_WORDS) {
            if (lowerContent.contains(word.toLowerCase())) {
                return true;
            }
        }
        return false;
    }

    /**
     * 将内容中的敏感词替换为掩码字符
     * 
     * @param content 原始内容
     * @param maskChar 掩码字符
     * @return 替换敏感词后的内容
     */
    public String maskSensitiveWords(String content, char maskChar) {
        if (StrUtil.isBlank(content)) {
            return content;
        }

        String result = content;
        for (String word : SENSITIVE_WORDS) {
            if (result.toLowerCase().contains(word.toLowerCase())) {
                String mask = String.valueOf(maskChar).repeat(word.length());
                result = result.replaceAll("(?i)" + Pattern.quote(word), mask);
            }
        }
        return result;
    }
}
