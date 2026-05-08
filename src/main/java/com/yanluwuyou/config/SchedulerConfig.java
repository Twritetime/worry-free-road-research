package com.yanluwuyou.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableScheduling;

/**
 * 定时任务配置类
 * 启用Spring的定时任务功能
 * 支持使用@Scheduled注解定义定时任务
 */
@Configuration
@EnableScheduling
public class SchedulerConfig {
}
