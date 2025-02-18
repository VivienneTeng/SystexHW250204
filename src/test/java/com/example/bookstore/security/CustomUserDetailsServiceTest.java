package com.example.bookstore.security;

import com.example.bookstore.dao.UserRepository;
import com.example.bookstore.entity.Role;
import com.example.bookstore.entity.User;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.util.Optional;
import java.util.Set;

@ExtendWith(MockitoExtension.class) // 讓 Mockito 整合 JUnit 5
public class CustomUserDetailsServiceTest {

    @Mock
    private UserRepository userRepository; // 模擬 UserRepository，避免實際查詢 DB

    @InjectMocks
    private CustomUserDetailsService customUserDetailsService; // 測試目標

    private User testUser;

    @BeforeEach
    void setUp() {
        // 初始化一個測試用的 User 物件
        testUser = new User();
        testUser.setUsername("testuser");
        testUser.setPassword("password123");
        testUser.setRoles(Set.of(new Role("ADMIN"))); // 假設這個使用者是 ADMIN
    }

    @Test
    void loadUserByUsername_ShouldReturnUserDetails_WhenUserExists() {
        // 模擬 UserRepository 的行為：當查詢 "testuser" 時，回傳 testUser
        when(userRepository.findByUsername("testuser")).thenReturn(Optional.of(testUser));

        // 測試 `loadUserByUsername` 方法
        UserDetails userDetails = customUserDetailsService.loadUserByUsername("testuser");

        // 驗證返回的 UserDetails 是否正確
        assertNotNull(userDetails);
        assertEquals("testuser", userDetails.getUsername());
        assertEquals("password123", userDetails.getPassword());
        assertTrue(userDetails.getAuthorities().stream().anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN"))); // 檢查是否有 ROLE_ADMIN 權限
    }

    @Test
    void loadUserByUsername_ShouldThrowException_WhenUserNotFound() {
        // 模擬當查詢 "unknown" 時，找不到使用者
        when(userRepository.findByUsername("unknown")).thenReturn(Optional.empty());

        // 測試找不到使用者時，應拋出 UsernameNotFoundException
        assertThrows(UsernameNotFoundException.class, () -> {
            customUserDetailsService.loadUserByUsername("unknown");
        });
    }

    @Test
    void loadUserByUsername_ShouldAssignDefaultRole_WhenUserHasNoRoles() {
        testUser.setRoles(null); // 清除角色，模擬使用者沒有角色
        when(userRepository.findByUsername("testuser")).thenReturn(Optional.of(testUser));

        UserDetails userDetails = customUserDetailsService.loadUserByUsername("testuser");

        // 應該預設賦予 ROLE_EMPLOYEE
        assertTrue(userDetails.getAuthorities().stream().anyMatch(a -> a.getAuthority().equals("ROLE_EMPLOYEE")));
    }
}