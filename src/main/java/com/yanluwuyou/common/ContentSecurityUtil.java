package com.yanluwuyou.common;

import cn.hutool.core.util.StrUtil;
import org.springframework.stereotype.Component;

import java.util.*;
import java.util.regex.Pattern;

@Component
public class ContentSecurityUtil {

    private static final Set<String> SENSITIVE_WORDS = new HashSet<>(Arrays.asList(
            "枪支", "弹药", "毒品", "赌博", "卖淫", "反动", "分裂", "恐怖",
            "作弊", "泄题", "助考", "代考", "槍支", "嗎啡", "海洛因",
            "彩票", "赌球", "假证", "文凭", "枪版", "破解", "木马",
            "病毒", "钓鱼", "诈骗", "洗钱", "贿赂", "贪污", "走私"
    ));

    private static final Pattern SCRIPT_PATTERN = Pattern.compile(
            "<script[^>]*?>[\\s\\S]*?<\\/script>", Pattern.CASE_INSENSITIVE);
    private static final Pattern HTML_TAG_PATTERN = Pattern.compile(
            "<[^>]+>", Pattern.CASE_INSENSITIVE);
    private static final Pattern JAVASCRIPT_PATTERN = Pattern.compile(
            "javascript:", Pattern.CASE_INSENSITIVE);
    private static final Pattern ONERROR_PATTERN = Pattern.compile(
            "onerror\\s*=", Pattern.CASE_INSENSITIVE);
    private static final Pattern ONCLICK_PATTERN = Pattern.compile(
            "onclick\\s*=", Pattern.CASE_INSENSITIVE);
    private static final Pattern ONLOAD_PATTERN = Pattern.compile(
            "onload\\s*=", Pattern.CASE_INSENSITIVE);

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
