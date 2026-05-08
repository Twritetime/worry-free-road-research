package com.yanluwuyou;

import com.yanluwuyou.service.UserService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static org.junit.jupiter.api.Assertions.assertNotNull;

/**
 * 应用启动测试类
 * 验证Spring Boot上下文加载是否正常
 */
@SpringBootTest
class YanLuWuYouApplicationTests {

    @Autowired
    private UserService userService;

    @Test
    void contextLoads() {
        assertNotNull(userService);
        long count = userService.count();
        System.out.println("Current user count: " + count);
    }

}
