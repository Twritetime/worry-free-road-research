package com.yanluwuyou.service;

public interface CrawlerService {
    /**
     * 爬取指南数据
     * @param url 目标URL
     * @param category 分类
     * @return 爬取数量
     */
    int crawlGuides(String url, String category);
}
