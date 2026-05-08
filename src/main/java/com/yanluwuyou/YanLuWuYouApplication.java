package com.yanluwuyou;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;

/**
 * 研路无忧系统启动类
 * Spring Boot应用程序入口点
 * 基于 Spring Boot + MyBatis-Plus + Vue 的考研资料分享平台
 */
@SpringBootApplication
@MapperScan("com.yanluwuyou.mapper")
public class YanLuWuYouApplication {

    /**
     * 应用程序主方法
     * 启动Spring Boot嵌入式Tomcat服务器并初始化Spring容器
     * 
     * @param args 命令行参数（可配置端口、profile等）
     */
    public static void main(String[] args) {
        SpringApplication.run(YanLuWuYouApplication.class, args);
    }

}
