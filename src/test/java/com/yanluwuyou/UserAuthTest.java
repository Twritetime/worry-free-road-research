package com.yanluwuyou;

import com.yanluwuyou.dto.UserLoginDTO;
import com.yanluwuyou.dto.UserRegisterDTO;
import com.yanluwuyou.entity.User;
import com.yanluwuyou.service.UserService;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class UserAuthTest {

    @Autowired
    private UserService userService;

    private static final String TEST_USERNAME = "test_user_" + UUID.randomUUID().toString().substring(0, 8);
    private static final String TEST_PASSWORD = "password123";

    @Test
    @Order(1)
    void testRegister() {
        UserRegisterDTO registerDTO = new UserRegisterDTO();
        registerDTO.setUsername(TEST_USERNAME);
        registerDTO.setPassword(TEST_PASSWORD);
        registerDTO.setNickname("Test User");
        registerDTO.setEmail("test@example.com");
        registerDTO.setPhone("13800000000");

        assertDoesNotThrow(() -> userService.register(registerDTO));
    }

    @Test
    @Order(2)
    void testLogin() {
        UserLoginDTO loginDTO = new UserLoginDTO();
        loginDTO.setUsername(TEST_USERNAME);
        loginDTO.setPassword(TEST_PASSWORD);

        User user = userService.login(loginDTO);
        assertNotNull(user);
        assertEquals(TEST_USERNAME, user.getUsername());
    }

    @Test
    @Order(3)
    void testLoginFail() {
        UserLoginDTO loginDTO = new UserLoginDTO();
        loginDTO.setUsername(TEST_USERNAME);
        loginDTO.setPassword("wrongpassword");

        assertThrows(RuntimeException.class, () -> userService.login(loginDTO));
    }
}
