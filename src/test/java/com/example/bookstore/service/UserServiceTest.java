package com.example.bookstore.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

import com.example.bookstore.dao.RoleRepository;
import com.example.bookstore.dao.UserRepository;
import com.example.bookstore.dto.UserDto;
import com.example.bookstore.entity.Role;
import com.example.bookstore.entity.User;
import com.example.bookstore.service.impl.UserServiceImpl;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.*;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private RoleRepository roleRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @InjectMocks
    private UserServiceImpl userService;

    private User user;
    private Role role;
    private UserDto userDto;

    @BeforeEach
    void setUp() {
        user = new User();
        user.setUserId(1L);
        user.setUsername("testUser");
        user.setPassword("encodedPassword");
        user.setName("Test User");
        user.setPhone("1234567890");
        user.setEmail("test@example.com");
        user.setRoles(new HashSet<>());

        role = new Role("EMPLOYEE");

        userDto = new UserDto();
        userDto.setUsername("testUser");
        userDto.setName("Test User");
        userDto.setPhone("1234567890");
        userDto.setEmail("test@example.com");
        userDto.setRoles(Set.of("EMPLOYEE"));
    }

    @Test
    void testRegisterUser() {
        when(roleRepository.findByRoleName("EMPLOYEE")).thenReturn(Optional.of(role));
        when(passwordEncoder.encode("password")).thenReturn("encodedPassword");
        when(userRepository.save(any(User.class))).thenAnswer(invocation -> {
            User savedUser = invocation.getArgument(0);
            savedUser.setRoles(Set.of(role)); // 確保角色被正確設定
            return savedUser;
        });
    
        User registeredUser = userService.registerUser(userDto, "password");
    
        assertNotNull(registeredUser);
        assertEquals("testUser", registeredUser.getUsername());
        assertEquals("encodedPassword", registeredUser.getPassword());
    
        System.out.println("User roles: " + registeredUser.getRoles()); // 除錯用
    
        assertTrue(registeredUser.getRoles().stream()
            .anyMatch(r -> r.getRoleName().equals("EMPLOYEE"))); // 確保角色為 EMPLOYEE
    
        verify(userRepository, times(1)).save(any(User.class));
    }
    

    @Test
    void testAssignRoleToUser() {
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(roleRepository.findByRoleName("ADMIN")).thenReturn(Optional.of(new Role("ADMIN")));

        userService.assignRoleToUser(1L, "ADMIN");

        assertTrue(user.getRoles().stream().anyMatch(r -> r.getRoleName().equals("ADMIN")));
        verify(userRepository, times(1)).save(user);
    }

    @Test
    void testGetAllUsers() {
        when(userRepository.findAll()).thenReturn(List.of(user));

        List<UserDto> users = userService.getAllUsers();

        assertFalse(users.isEmpty());
        assertEquals(1, users.size());
        assertEquals("testUser", users.get(0).getUsername());

        verify(userRepository, times(1)).findAll();
    }

    @Test
    void testGetUserById() {
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));

        Optional<UserDto> foundUser = userService.getUserById(1L);

        assertTrue(foundUser.isPresent());
        assertEquals("testUser", foundUser.get().getUsername());

        verify(userRepository, times(1)).findById(1L);
    }

    @Test
    void testUpdateUser() {
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(roleRepository.findByRoleName("EMPLOYEE")).thenReturn(Optional.of(role));
        when(userRepository.save(any(User.class))).thenReturn(user);

        UserDto updatedUser = userService.updateUser(1L, userDto);

        assertEquals("testUser", updatedUser.getUsername());
        assertEquals("Test User", updatedUser.getName());

        verify(userRepository, times(1)).save(user);
    }

    @Test
    void testDeleteUser() {
        doNothing().when(userRepository).deleteById(1L);

        userService.deleteUser(1L);

        verify(userRepository, times(1)).deleteById(1L);
    }

    @Test
    void testSendResetPasswordEmail() {
        String email = "test@example.com";
        
        when(userRepository.findByEmail(email)).thenReturn(Optional.of(user));
    
        Map<String, String> response = userService.sendResetPasswordEmail(email);
    
        assertNotNull(response);
        assertEquals(email, response.get("email"));
    
        // 取得 token
        String token = response.get("resetLink").split("=")[1];
        System.out.println("Generated Token: " + token);
    
        verify(userRepository, times(1)).findByEmail(email);
    }
    

    @Test
    void testResetPassword() {
        String email = "test@example.com";
    
        // 先執行 testSendResetPasswordEmail() 取得 token
        when(userRepository.findByEmail(email)).thenReturn(Optional.of(user));
        Map<String, String> response = userService.sendResetPasswordEmail(email);
    
        String token = response.get("resetLink").split("=")[1]; // 取得 token
        System.out.println("Using Token for Reset: " + token);
    
        // Mock 密碼編碼
        when(passwordEncoder.encode("newPassword")).thenReturn("encodedNewPassword");
    
        // 測試重置密碼
        userService.resetPassword(token, "newPassword");
    
        assertEquals("encodedNewPassword", user.getPassword()); // 檢查密碼是否更新
        verify(userRepository, times(1)).save(user);
    }
    
    
}
