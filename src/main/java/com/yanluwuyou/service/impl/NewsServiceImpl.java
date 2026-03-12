package com.yanluwuyou.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.yanluwuyou.entity.News;
import com.yanluwuyou.mapper.NewsMapper;
import com.yanluwuyou.service.NewsService;
import org.springframework.stereotype.Service;

/**
 * 新闻资讯服务实现类
 */
@Service
public class NewsServiceImpl extends ServiceImpl<NewsMapper, News> implements NewsService {
}
