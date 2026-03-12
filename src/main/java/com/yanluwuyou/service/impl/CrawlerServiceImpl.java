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
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;
import java.time.LocalDateTime;

@Service
public class CrawlerServiceImpl implements CrawlerService {

    @Autowired
    private GuideService guideService;

    @Override
    // Removed @Transactional to allow partial success and avoid long transactions
    public int crawlGuides(String url, String category) {
        int count = 0;
        try {
            System.out.println("Starting crawl for url: " + url);
            // 设置User-Agent模拟浏览器
            Document doc = Jsoup.connect(url)
                    .userAgent("Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/91.0.4472.124 Safari/537.36")
                    .header("Accept", "text/html,application/xhtml+xml,application/xml;q=0.9,image/webp,*/*;q=0.8")
                    .header("Accept-Language", "zh-CN,zh;q=0.9,en;q=0.8")
                    .timeout(10000)
                    .get();

            // 针对中国考研网(chinakaoyan.com)的通用列表适配
            // 尝试多种常见的列表选择器
            Elements links = doc.select(".z29 ul li.z30 a, .list-item a, .article-list a, .info-list a, .list a, .news_list a, .uc_ulbox li a, .ket_lists li a");
            
            // 如果是chinakaoyan的具体结构
            if (links.isEmpty()) {
                 links = doc.select(".list li a, .z29 a, .uc_ulbox a");
            }
            
            System.out.println("Found " + links.size() + " links.");
            if (links.isEmpty()) {
                System.out.println("WARNING: No links found. Dumping document body preview:");
                System.out.println(StrUtil.sub(doc.body().html(), 0, 500));
            }

            for (Element link : links) {
                String href = link.attr("abs:href");
                String title = link.text();

                // 简单的过滤
                if (StrUtil.isBlank(title) || title.length() < 5) continue;
                if (count >= 10) break; // 限制每次爬取数量，避免超时

                try {
                    // 爬取详情
                    Document detailDoc = Jsoup.connect(href)
                            .userAgent("Mozilla/5.0")
                            .timeout(10000)
                            .get();

                    // 尝试获取内容
                    Element contentElem = detailDoc.selectFirst(".arcont, .article-content, .content, #content, .news_content");
                    String content = contentElem != null ? contentElem.html() : "内容获取失败，请访问原链接查看：" + href;
                    
                    // 获取完整标题
                    Element titleElem = detailDoc.selectFirst("h1, .artitle, .title");
                    String fullTitle = titleElem != null ? titleElem.text() : title;

                    // 提取院校
                    String institution = extractUniversity(fullTitle);
                    
                    Guide guide = new Guide();
                    guide.setTitle(fullTitle);
                    guide.setContent(content);
                    guide.setCategory(category);
                    guide.setInstitution(institution);
                    guide.setMajor("不限"); // 默认为不限，后续可尝试提取
                    guide.setCreateTime(LocalDateTime.now());
                    guide.setStatus(1); // 默认发布
                    guide.setViewCount(0);
                    
                    guideService.save(guide);
                    count++;
                    System.out.println("Saved guide successfully: " + fullTitle);
                    
                    // 礼貌性延时
                    Thread.sleep(500);
                } catch (Exception e) {
                    // 忽略单个失败
                    System.err.println("Failed to crawl detail: " + href + ", error: " + e.getMessage());
                    e.printStackTrace();
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
            return -1;
        }
        return count;
    }

    private String extractUniversity(String title) {
        // 简单提取逻辑：匹配"大学"或"学院"
        if (StrUtil.isBlank(title)) return "未知院校";
        
        // 常见格式：XX大学202x年...
        int idx = title.indexOf("大学");
        if (idx > -1) {
            // 向前找，通常院校名在开头
            // 这里简单截取
            return title.substring(0, idx + 2);
        }
        
        idx = title.indexOf("学院");
        if (idx > -1) {
             return title.substring(0, idx + 2);
        }
        
        return "未知院校";
    }
}
