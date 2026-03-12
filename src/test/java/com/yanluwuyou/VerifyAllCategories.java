package com.yanluwuyou;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class VerifyAllCategories {

    @Test
    public void testAllCategories() {
        Map<String, String> urls = new HashMap<>();
        urls.put("zhaoshengjianzhang", "http://www.chinakaoyan.com/zhaosheng/");
        urls.put("zhuanyemulu", "https://www.chinakaoyan.com/info/list/ClassID/52.shtml");
        urls.put("kaoshidagang", "https://www.chinakaoyan.com/info/list/ClassID/97.shtml");
        urls.put("fushixize", "https://www.chinakaoyan.com/info/list/ClassID/23.shtml");

        for (Map.Entry<String, String> entry : urls.entrySet()) {
            String category = entry.getKey();
            String url = entry.getValue();
            System.out.println("\nTesting Category: " + category + " URL: " + url);
            
            try {
                Document doc = Jsoup.connect(url)
                        .userAgent("Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/91.0.4472.124 Safari/537.36")
                        .timeout(10000)
                        .get();

                // Selectors from CrawlerServiceImpl
                Elements links = doc.select(".z29 ul li.z30 a, .list-item a, .article-list a, .info-list a, .list a, .news_list a, .uc_ulbox li a, .ket_lists li a");
                if (links.isEmpty()) {
                     links = doc.select(".list li a, .z29 a, .uc_ulbox a");
                }
                
                System.out.println("Found " + links.size() + " links.");
                
                if (links.isEmpty()) {
                    System.err.println("FAILED to find links for " + category);
                    // Print body structure for debugging
                    System.out.println("Body classes: " + doc.body().className());
                    System.out.println("First div class: " + (doc.selectFirst("div") != null ? doc.selectFirst("div").className() : "null"));
                } else {
                    // Check first link
                    String href = links.get(0).attr("abs:href");
                    System.out.println("First link: " + links.get(0).text() + " -> " + href);
                    
                    // Try to crawl detail
                    try {
                        Document detailDoc = Jsoup.connect(href)
                            .userAgent("Mozilla/5.0")
                            .timeout(10000)
                            .get();
                            
                        // Selectors from CrawlerServiceImpl
                        // .arcont (common in chinakaoyan), .article-content, ...
                        if (detailDoc.selectFirst(".arcont, .article-content, .content, #content, .news_content") != null) {
                            System.out.println("  Content found.");
                        } else {
                            System.err.println("  FAILED to find content in detail page.");
                        }
                    } catch (Exception e) {
                        System.err.println("  Failed to connect to detail page: " + e.getMessage());
                    }
                }

            } catch (IOException e) {
                System.err.println("Failed to connect to " + url + ": " + e.getMessage());
            }
        }
    }
}
