package com.example.bookstore.security;

import com.example.bookstore.dao.UserRepository;
import com.example.bookstore.entity.Role;
import com.example.bookstore.entity.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;


@DataJpaTest // 讓 Spring Boot 只啟動 JPA 測試環境
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE) // 可切換到真實 DB 或 Testcontainers
public class CustomUserDetailsServiceIT {

    @Autowired
    private UserRepository userRepository;

    private CustomUserDetailsService customUserDetailsService;

    @BeforeEach
    void setUp() {
        // 建立測試用使用者
        User testUser = new User();
        testUser.setUsername("admin");
        testUser.setPassword("hashedPassword");
        testUser.setRoles(Set.of(new Role("ADMIN")));
        userRepository.save(testUser);
    }

    @Test
    void loadUserByUsername_ShouldReturnUserDetails_WhenUserExists() {
        UserDetails userDetails = customUserDetailsService.loadUserByUsername("admin");

        assertNotNull(userDetails);
        assertEquals("admin", userDetails.getUsername());
        assertTrue(userDetails.getAuthorities().stream().anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN")));
    }

    @Test
    void loadUserByUsername_ShouldThrowException_WhenUserNotFound() {
        assertThrows(UsernameNotFoundException.class, () -> {
            customUserDetailsService.loadUserByUsername("nonexistent");
        });
    }
}