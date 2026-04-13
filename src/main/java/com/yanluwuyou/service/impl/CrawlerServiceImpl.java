package com.yanluwuyou.service.impl;

import cn.hutool.core.util.StrUtil;
import com.yanluwuyou.entity.Guide;
import com.yanluwuyou.service.CrawlerService;
import com.yanluwuyou.service.GuideService;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.net.URI;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class CrawlerServiceImpl implements CrawlerService {

    private static final int MAX_CRAWL_COUNT = 30;
    private static final Set<String> OFFICIAL_HOST_SUFFIXES = Set.of(
            "chsi.com.cn",
            "chsi.cn",
            "edu.cn",
            "gov.cn"
    );
    private static final List<String> PAID_KEYWORDS = List.of(
            "全程班", "规划营", "课程", "辅导", "资料", "上岸", "广告", "咨询"
    );
    private static final List<String> INVALID_TOKENS = List.of(
            "{{", "}}", "${", "item.", "<%=", "%>"
    );

    @Autowired
    private GuideService guideService;

    @Override
    public int crawlGuides(String url, String category) {
        if (!isOfficialUrl(url)) {
            throw new IllegalArgumentException("仅支持研招网及高校官网链接（chsi.cn/chsi.com.cn/edu.cn/gov.cn）");
        }
        int count = 0;
        try {
            System.out.println("Starting crawl for url: " + url);
            Document doc = createConnection(url).get();
            Elements links = selectListLinks(doc);
            List<String> candidates = links.stream()
                    .map(link -> link.attr("abs:href"))
                    .filter(StrUtil::isNotBlank)
                    .filter(this::isOfficialUrl)
                    .filter(this::isLikelyDetailUrl)
                    .distinct()
                    .collect(Collectors.toList());

            for (String href : candidates) {
                if (count >= MAX_CRAWL_COUNT) {
                    break;
                }
                String title = findLinkTitle(links, href);
                if (StrUtil.isBlank(title) || title.length() < 5) {
                    continue;
                }
                if (!isValidTitle(title)) {
                    continue;
                }

                try {
                    Document detailDoc = createConnection(href).get();
                    Element titleElem = detailDoc.selectFirst("h1, .artitle, .title, .article-title");
                    String fullTitle = titleElem != null ? titleElem.text() : title;
                    if (!isValidTitle(fullTitle) || existsByTitle(fullTitle)) {
                        continue;
                    }
                    Element contentElem = chooseContentElement(detailDoc);
                    String content = contentElem != null ? sanitizeContent(contentElem, href) : "";
                    if (!isValidContent(content)) {
                        continue;
                    }
                    String institution = extractUniversity(fullTitle, detailDoc.text());
                    Guide guide = new Guide();
                    guide.setTitle(fullTitle);
                    guide.setContent(content);
                    guide.setCategory(category);
                    guide.setInstitution(institution);
                    guide.setMajor("不限");
                    guide.setCreateTime(LocalDateTime.now());
                    guide.setStatus(1);
                    guide.setViewCount(0);

                    guideService.save(guide);
                    count++;
                    Thread.sleep(500);
                } catch (Exception e) {
                    System.err.println("Failed to crawl detail: " + href + ", error: " + e.getMessage());
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
            return -1;
        }
        return count;
    }

    private org.jsoup.Connection createConnection(String url) {
        return Jsoup.connect(url)
                .userAgent("Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/122.0 Safari/537.36")
                .header("Accept", "text/html,application/xhtml+xml,application/xml;q=0.9,image/webp,*/*;q=0.8")
                .header("Accept-Language", "zh-CN,zh;q=0.9,en;q=0.8")
                .timeout(15000);
    }

    private Elements selectListLinks(Document doc) {
        Elements links = doc.select(".article-list a, .info-list a, .list a, .news_list a, .list-item a, li a");
        if (links.isEmpty()) {
            links = doc.select("a[href]");
        }
        return links;
    }

    private String findLinkTitle(Elements links, String href) {
        for (Element link : links) {
            if (href.equals(link.attr("abs:href"))) {
                return link.text();
            }
        }
        return "";
    }

    private boolean isLikelyDetailUrl(String url) {
        String normalized = url.toLowerCase();
        return normalized.contains("html")
                || normalized.contains("detail")
                || normalized.contains("article")
                || normalized.contains("view")
                || normalized.contains("info");
    }

    private boolean isOfficialUrl(String url) {
        try {
            URI uri = URI.create(url);
            String host = uri.getHost();
            if (StrUtil.isBlank(host)) {
                return false;
            }
            String normalized = host.toLowerCase();
            for (String suffix : OFFICIAL_HOST_SUFFIXES) {
                if (normalized.equals(suffix) || normalized.endsWith("." + suffix)) {
                    return true;
                }
            }
            return false;
        } catch (Exception ignored) {
            return false;
        }
    }

    private boolean existsByTitle(String title) {
        return guideService.lambdaQuery().eq(Guide::getTitle, title).count() > 0;
    }

    private String sanitizeContent(Element contentElem, String pageUrl) {
        Element root = contentElem.clone();
        root.select("script, iframe, style, form").remove();
        root.select("noscript, template").remove();
        root.select("*").forEach(element -> {
            if ("a".equalsIgnoreCase(element.tagName())) {
                String href = element.attr("abs:href");
                String text = element.text();
                if (!isOfficialUrl(href) || containsPaidKeyword(text)) {
                    element.remove();
                    return;
                }
                element.attr("href", href);
            }
            if ("img".equalsIgnoreCase(element.tagName())) {
                String src = element.attr("abs:src");
                if (StrUtil.isBlank(src) || !isOfficialUrl(src)) {
                    element.remove();
                    return;
                }
                element.attr("src", src);
            }
        });
        root.select("*").forEach(element -> {
            if (containsInvalidToken(element.text())) {
                element.remove();
            }
        });
        String html = root.html();
        if (StrUtil.isBlank(html)) {
            return "<p>请访问研招网或高校研究生院官网查看原文：<a href=\"" + pageUrl + "\" target=\"_blank\">" + pageUrl + "</a></p>";
        }
        return html;
    }

    private Element chooseContentElement(Document detailDoc) {
        List<Element> candidates = new ArrayList<>();
        candidates.addAll(detailDoc.select("article"));
        candidates.addAll(detailDoc.select(".article-content"));
        candidates.addAll(detailDoc.select(".content"));
        candidates.addAll(detailDoc.select("#content"));
        candidates.addAll(detailDoc.select(".news_content"));
        candidates.addAll(detailDoc.select(".cont"));
        for (Element candidate : candidates) {
            if (candidate == null) {
                continue;
            }
            String text = StrUtil.trim(candidate.text());
            if (text.length() < 60) {
                continue;
            }
            if (containsInvalidToken(text)) {
                continue;
            }
            return candidate;
        }
        return detailDoc.selectFirst("body");
    }

    private boolean containsPaidKeyword(String text) {
        if (StrUtil.isBlank(text)) {
            return false;
        }
        for (String keyword : PAID_KEYWORDS) {
            if (text.contains(keyword)) {
                return true;
            }
        }
        return false;
    }

    private boolean containsInvalidToken(String text) {
        if (StrUtil.isBlank(text)) {
            return false;
        }
        for (String token : INVALID_TOKENS) {
            if (text.contains(token)) {
                return true;
            }
        }
        return false;
    }

    private boolean isValidTitle(String title) {
        if (StrUtil.isBlank(title)) {
            return false;
        }
        String normalized = StrUtil.trim(title);
        if (normalized.length() < 8 || normalized.length() > 120) {
            return false;
        }
        if (containsInvalidToken(normalized)) {
            return false;
        }
        return normalized.contains("招生")
                || normalized.contains("专业")
                || normalized.contains("复试")
                || normalized.contains("考试")
                || normalized.contains("硕士");
    }

    private boolean isValidContent(String content) {
        if (StrUtil.isBlank(content)) {
            return false;
        }
        String text = Jsoup.parse(content).text();
        if (text.length() < 80) {
            return false;
        }
        return !containsInvalidToken(text);
    }

    private String extractUniversity(String title, String fallbackText) {
        String source = StrUtil.blankToDefault(title, fallbackText);
        if (StrUtil.isBlank(source)) {
            return "未知院校";
        }
        String normalized = source.replace("（", "(").replace("）", ")");
        String[] suffixes = {"大学", "学院", "大学研究生院", "研究生院"};
        for (String suffix : suffixes) {
            int idx = normalized.indexOf(suffix);
            if (idx > 0) {
                int start = Math.max(idx - 14, 0);
                String candidate = normalized.substring(start, idx + suffix.length()).trim();
                int cut = Math.max(candidate.lastIndexOf(" "), candidate.lastIndexOf("，"));
                if (cut >= 0 && cut + 1 < candidate.length()) {
                    candidate = candidate.substring(cut + 1);
                }
                if (candidate.length() >= 4) {
                    return candidate;
                }
            }
        }
        return "未知院校";
    }
}
