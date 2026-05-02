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
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

@Service
public class CrawlerServiceImpl implements CrawlerService {

    private static final int MAX_CRAWL_COUNT = 30;
    private static final int CURRENT_YEAR = LocalDate.now().getYear();
    private static final Set<String> OFFICIAL_HOST_SUFFIXES = Set.of(
            "chsi.com.cn",
            "chsi.cn",
            "edu.cn",
            "gov.cn"
    );
    private static final List<String> PAID_KEYWORDS = List.of(
            "全程班", "规划营", "课程", "辅导", "资料", "上岸", "广告", "咨询", "培训", "报名", "收费"
    );
    private static final List<String> INVALID_TOKENS = List.of(
            "{{", "}}", "${", "item.", "<%=", "%>"
    );
    private static final Pattern YEAR_PATTERN = Pattern.compile("(20\\d{2})");

    @Autowired
    private GuideService guideService;

    @Override
    public int crawlGuides(String url, String category) {
        if (!isOfficialUrl(url)) {
            throw new IllegalArgumentException("仅支持研招网及高校官网链接（chsi.cn/chsi.com.cn/edu.cn/gov.cn）");
        }

        int totalCount = 0;
        Set<String> crawledUrls = new HashSet<>();

        try {
            System.out.println("[爬虫启动] 开始爬取: " + url + ", 分类: " + category);

            int count = crawlListPage(url, category, crawledUrls);
            totalCount += count;
            System.out.println("[爬虫总结] 总共爬取: " + totalCount + " 条数据");
        } catch (Exception e) {
            System.err.println("[爬虫错误] 爬取失败: " + e.getMessage());
            e.printStackTrace();
            return -1;
        }

        return totalCount;
    }

    private int crawlListPage(String listUrl, String category, Set<String> crawledUrls) throws IOException {
        int count = 0;
        Document doc = createConnection(listUrl).get();

        System.out.println("[页面解析] URL: " + listUrl + ", 标题: " + doc.title());

        List<String> articleLinks = extractArticleLinks(doc, listUrl);
        System.out.println("[链接提取] 提取到 " + articleLinks.size() + " 个候选链接");

        for (String articleUrl : articleLinks) {
            if (count >= MAX_CRAWL_COUNT) {
                break;
            }

            if (crawledUrls.contains(articleUrl)) {
                continue;
            }

            try {
                boolean success = crawlArticle(articleUrl, category);
                if (success) {
                    crawledUrls.add(articleUrl);
                    count++;
                }

                Thread.sleep(500);
            } catch (Exception e) {
                System.err.println("[文章爬取失败] " + articleUrl + ", 错误: " + e.getMessage());
            }
        }

        return count;
    }

    private List<String> extractArticleLinks(Document doc, String baseUrl) {
        List<String> links = new ArrayList<>();

        // 研招网特定选择器
        String[] selectors = {
            ".news-list li a",
            ".article-list li a",
            ".list-item a",
            "ul.list li a",
            ".content-list a",
            ".main-content a[href]",
            "article a[href]",
            "a[href*='zxzc']",
            "a[href*='zcfg']",
            "a[href*='kyzx']",
            "a[href*='detail']",
            "a[href*='article']",
            "a[href*='view']",
            "a[href*='info']",
            "a[href*='content']",
            "a[href*='show']",
            "a[href*='html']"
        };

        for (String selector : selectors) {
            Elements elements = doc.select(selector);
            for (Element element : elements) {
                String href = element.attr("abs:href");
                String text = element.text().trim();

                if (isValidArticleLink(href, text, baseUrl)) {
                    links.add(href);
                }
            }
        }

        // 如果没找到，尝试所有链接
        if (links.isEmpty()) {
            Elements allLinks = doc.select("a[href]");
            for (Element element : allLinks) {
                String href = element.attr("abs:href");
                String text = element.text().trim();

                if (isValidArticleLink(href, text, baseUrl)) {
                    links.add(href);
                }
            }
        }

        return links.stream()
                .distinct()
                .limit(MAX_CRAWL_COUNT * 2)
                .collect(Collectors.toList());
    }

    private boolean isValidArticleLink(String href, String text, String baseUrl) {
        if (StrUtil.isBlank(href) || StrUtil.isBlank(text)) {
            return false;
        }

        if (!isOfficialUrl(href)) {
            return false;
        }

        // 过滤导航链接
        if (text.length() < 3 || text.length() > 80) {
            return false;
        }

        // 过滤付费关键词
        if (containsPaidKeyword(text)) {
            return false;
        }

        // 过滤常见导航文字
        String lowerText = text.toLowerCase();
        List<String> navWords = List.of("首页", "上一页", "下一页", "末页", "返回", "更多", ">>", "<<", "登录", "注册", "搜索");
        for (String nav : navWords) {
            if (lowerText.contains(nav)) {
                return false;
            }
        }

        // 必须是详情页链接
        return isLikelyDetailUrl(href);
    }

    private boolean crawlArticle(String articleUrl, String category) {
        try {
            Document doc = createConnection(articleUrl).get();

            String title = extractTitle(doc);
            if (StrUtil.isBlank(title) || title.length() < 5) {
                System.out.println("[跳过] 标题太短或为空: " + articleUrl);
                return false;
            }

            if (!isValidTitle(title)) {
                System.out.println("[跳过] 标题无效: " + title);
                return false;
            }

            // 检查标题是否已存在
            if (existsByTitle(title)) {
                System.out.println("[跳过] 标题已存在: " + title);
                return false;
            }

            String content = extractContent(doc, articleUrl);
            if (!isValidContent(content)) {
                System.out.println("[跳过] 内容无效: " + title);
                return false;
            }

            String institution = extractInstitution(title, doc.text());

            Guide guide = new Guide();
            guide.setTitle(title);
            guide.setContent(content);
            guide.setCategory(category);
            guide.setInstitution(institution);
            guide.setMajor("不限专业");
            guide.setCreateTime(LocalDateTime.now());
            guide.setStatus(1);
            guide.setViewCount(0);
            guide.setSortOrder(0);

            guideService.save(guide);
            System.out.println("[保存成功] " + title);

            return true;
        } catch (Exception e) {
            System.err.println("[文章解析失败] " + articleUrl + ", 错误: " + e.getMessage());
            return false;
        }
    }

    private String extractTitle(Document doc) {
        // 优先使用 h1 标签
        Element h1 = doc.selectFirst("h1");
        if (h1 != null) {
            String title = h1.text().trim();
            if (StrUtil.isNotBlank(title) && title.length() >= 5) {
                return cleanTitle(title);
            }
        }

        // 尝试其他标题选择器
        String[] titleSelectors = {
            ".article-title",
            ".news-title",
            ".detail-title",
            ".title",
            "title"
        };

        for (String selector : titleSelectors) {
            Element element = doc.selectFirst(selector);
            if (element != null) {
                String title = element.text().trim();
                if (StrUtil.isNotBlank(title) && title.length() >= 5) {
                    return cleanTitle(title);
                }
            }
        }

        return "";
    }

    private String cleanTitle(String title) {
        // 移除常见的网站后缀
        return title.replaceAll("_研招网$", "")
                .replaceAll("_中国研究生招生信息网$", "")
                .replaceAll("-研招网$", "")
                .replaceAll("-中国研究生招生信息网$", "")
                .trim();
    }

    private String extractContent(Document doc, String pageUrl) {
        Element contentElement = null;

        // 内容选择器，按优先级排序
        String[] contentSelectors = {
            ".content-detail",
            ".detail-content",
            ".article-content",
            "article .content",
            "#content",
            ".news_content",
            ".news-detail",
            ".article-body",
            "article",
            ".main-content",
            ".cont",
            ".text"
        };

        for (String selector : contentSelectors) {
            Element element = doc.selectFirst(selector);
            if (element != null) {
                String text = element.text();
                if (text.length() >= 30 && !containsInvalidToken(text)) {
                    contentElement = element;
                    break;
                }
            }
        }

        // 如果没找到，尝试从 body 中提取主要内容
        if (contentElement == null) {
            contentElement = doc.selectFirst("body");
        }

        if (contentElement == null) {
            return "";
        }

        return sanitizeContent(contentElement, pageUrl);
    }

    private String sanitizeContent(Element contentElem, String pageUrl) {
        Element root = contentElem.clone();

        // 移除不需要的元素
        root.select("script, iframe, style, form, noscript, template, .ad, .advertisement, .share, .social-share, .copyright, nav, header, footer").remove();

        // 处理链接
        root.select("a").forEach(element -> {
            String href = element.attr("abs:href");
            String text = element.text();

            if (!isOfficialUrl(href) || containsPaidKeyword(text)) {
                element.remove();
                return;
            }

            element.attr("href", href);
            element.attr("target", "_blank");
            element.attr("rel", "noopener noreferrer");
        });

        // 处理图片
        root.select("img").forEach(element -> {
            String src = element.attr("abs:src");
            if (StrUtil.isBlank(src) || !isOfficialUrl(src)) {
                element.remove();
                return;
            }
            element.attr("src", src);
            element.attr("loading", "lazy");
        });

        // 移除包含无效token的元素
        root.select("*").forEach(element -> {
            if (containsInvalidToken(element.text())) {
                element.remove();
            }
        });

        // 清理空白段落
        root.select("p, div").forEach(element -> {
            if (element.text().trim().isEmpty()) {
                element.remove();
            }
        });

        String html = root.html();

        // 清理空标签和多余换行
        html = html.replaceAll("<p>\\s*</p>", "");
        html = html.replaceAll("<div>\\s*</div>", "");
        html = html.replaceAll("<br\\s*/?>\\s*<br\\s*/?>", "<br/><br/>");
        html = html.replaceAll("(\\s*<br\\s*/?>\\s*){3,}", "<br/><br/>");

        // 如果内容太少，返回原文链接
        String plainText = html.replaceAll("<[^>]+>", "").trim();
        if (plainText.length() < 20) {
            return "<div class=\"original-link\">" +
                    "<p>请访问研招网或高校研究生院官网查看原文：</p>" +
                    "<p><a href=\"" + pageUrl + "\" target=\"_blank\" rel=\"noopener noreferrer\">" + pageUrl + "</a></p>" +
                    "</div>";
        }

        return "<div class=\"article-content\">" + html + "</div>";
    }

    private boolean isValidTitle(String title) {
        if (StrUtil.isBlank(title) || title.length() < 5) {
            return false;
        }

        if (containsInvalidToken(title)) {
            return false;
        }

        if (containsPaidKeyword(title)) {
            return false;
        }

        return true;
    }

    private boolean isValidContent(String content) {
        if (StrUtil.isBlank(content)) {
            return false;
        }

        String text = Jsoup.parse(content).text();
        if (text.length() < 20) {
            System.out.println("[过滤] 内容太短: " + text.length() + " 字符");
            return false;
        }

        return !containsInvalidToken(text);
    }

    private String extractInstitution(String title, String fallbackText) {
        String source = StrUtil.blankToDefault(title, fallbackText);
        if (StrUtil.isBlank(source)) {
            return "多校适用";
        }

        String normalized = source.replace("（", "(").replace("）", ")");
        String[] suffixes = {"大学研究生院", "研究生院", "大学", "学院"};

        for (String suffix : suffixes) {
            int idx = normalized.indexOf(suffix);
            if (idx > 0) {
                int start = Math.max(idx - 14, 0);
                String candidate = normalized.substring(start, idx + suffix.length()).trim();

                int cut = Math.max(candidate.lastIndexOf(" "), candidate.lastIndexOf("，"));
                if (cut >= 0 && cut + 1 < candidate.length()) {
                    candidate = candidate.substring(cut + 1);
                }

                if (candidate.length() >= 4 && candidate.length() <= 30) {
                    return candidate;
                }
            }
        }

        return "多校适用";
    }

    private org.jsoup.Connection createConnection(String url) {
        return Jsoup.connect(url)
                .userAgent("Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/122.0 Safari/537.36")
                .header("Accept", "text/html,application/xhtml+xml,application/xml;q=0.9,image/webp,*/*;q=0.8")
                .header("Accept-Language", "zh-CN,zh;q=0.9,en;q=0.8")
                .header("Connection", "keep-alive")
                .timeout(30000)
                .followRedirects(true)
                .ignoreHttpErrors(true)
                .ignoreContentType(true);
    }

    private boolean isLikelyDetailUrl(String url) {
        String normalized = url.toLowerCase();
        return normalized.contains("html")
                || normalized.contains("detail")
                || normalized.contains("article")
                || normalized.contains("view")
                || normalized.contains("info")
                || normalized.contains("content")
                || normalized.contains("show")
                || normalized.contains("zxzc")
                || normalized.contains("zcfg")
                || normalized.contains("kyzx");
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

    private boolean containsInvalidToken(String value) {
        return StrUtil.containsAny(value, INVALID_TOKENS.toArray(new String[0]));
    }

    private boolean containsPaidKeyword(String text) {
        if (StrUtil.isBlank(text)) {
            return false;
        }
        String lowerText = text.toLowerCase();
        return PAID_KEYWORDS.stream().anyMatch(lowerText::contains);
    }
}
