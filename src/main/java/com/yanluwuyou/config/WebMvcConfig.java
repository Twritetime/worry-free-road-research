package com.yanluwuyou.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.nio.file.Paths;

/**
 * Web MVC 配置
 */
@Configuration
public class WebMvcConfig implements WebMvcConfigurer {

    @Value("${files.upload.path}")
    private String fileUploadPath;

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        // 将 /files/** 映射到本地文件系统
        // 使用 Paths.get().toUri().toString() 确保路径格式正确 (兼容 Windows/Linux)
        String path = Paths.get(fileUploadPath).toAbsolutePath().toUri().toString();
        // 确保路径以 / 结尾，否则静态资源映射可能失效
        if (!path.endsWith("/")) {
            path += "/";
        }
        registry.addResourceHandler("/files/**")
                .addResourceLocations(path);
    }
}
