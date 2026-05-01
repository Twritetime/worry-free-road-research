package com.yanluwuyou.service.impl;

import com.yanluwuyou.service.CrawlerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@Service
public class CrawlerSchedulerService {

    @Autowired
    private CrawlerService crawlerService;

    @Value("${crawler.schedule.enabled:true}")
    private boolean scheduleEnabled;

    @Value("${crawler.schedule.zhaoshengjianzhang.cron:0 0 8 * * ?}")
    private String zhaoshengjianzhangCron;

    @Value("${crawler.schedule.zhaoshengjianzhang.url:https://yz.chsi.com.cn/kyzx/}")
    private String zhaoshengjianzhangUrl;

    @Value("${crawler.schedule.zhuanyemulu.cron:0 0 9 * * ?}")
    private String zhuanyemuluCron;

    @Value("${crawler.schedule.zhuanyemulu.url:https://yz.chsi.com.cn/zsml/}")
    private String zhuanyemuluUrl;

    @Value("${crawler.schedule.kaoshidagang.cron:0 0 10 * * ?}")
    private String kaoshidagangCron;

    @Value("${crawler.schedule.kaoshidagang.url:https://yz.chsi.com.cn/kyzx/}")
    private String kaoshidagangUrl;

    @Value("${crawler.schedule.fushixize.cron:0 0 11 * * ?}")
    private String fushixizeCron;

    @Value("${crawler.schedule.fushixize.url:https://yz.chsi.com.cn/kyzx/}")
    private String fushixizeUrl;

    @Scheduled(cron = "${crawler.schedule.zhaoshengjianzhang.cron:0 0 8 * * ?}")
    public void crawlZhaoshengjianzhang() {
        if (!scheduleEnabled) {
            return;
        }
        executeCrawl("zhaoshengjianzhang", zhaoshengjianzhangUrl);
    }

    @Scheduled(cron = "${crawler.schedule.zhuanyemulu.cron:0 0 9 * * ?}")
    public void crawlZhuanyemulu() {
        if (!scheduleEnabled) {
            return;
        }
        executeCrawl("zhuanyemulu", zhuanyemuluUrl);
    }

    @Scheduled(cron = "${crawler.schedule.kaoshidagang.cron:0 0 10 * * ?}")
    public void crawlKaoshidagang() {
        if (!scheduleEnabled) {
            return;
        }
        executeCrawl("kaoshidagang", kaoshidagangUrl);
    }

    @Scheduled(cron = "${crawler.schedule.fushixize.cron:0 0 11 * * ?}")
    public void crawlFushixize() {
        if (!scheduleEnabled) {
            return;
        }
        executeCrawl("fushixize", fushixizeUrl);
    }

    private void executeCrawl(String category, String url) {
        try {
            String time = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
            System.out.println("========================================");
            System.out.println("[定时任务] 开始执行 - 分类: " + category);
            System.out.println("[定时任务] 执行时间: " + time);
            System.out.println("[定时任务] 目标URL: " + url);
            System.out.println("========================================");

            int count = crawlerService.crawlGuides(url, category);

            String endTime = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
            System.out.println("========================================");
            System.out.println("[定时任务] 执行完成 - 分类: " + category);
            System.out.println("[定时任务] 完成时间: " + endTime);
            System.out.println("[定时任务] 爬取数量: " + count);
            System.out.println("========================================");
        } catch (Exception e) {
            System.err.println("[定时任务] 执行失败 - 分类: " + category + ", 错误: " + e.getMessage());
            e.printStackTrace();
        }
    }
}
