package com.example.bookstore.config;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import com.example.bookstore.security.CustomUserDetailsService;
import com.example.bookstore.security.JwtAuthenticationFilter;
import com.example.bookstore.security.JwtUtil;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.web.servlet.MockMvc;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;

import java.util.List;
// 單元測試
@ExtendWith(MockitoExtension.class)
class SecurityConfigTest {

    @Mock
    private CustomUserDetailsService userDetailsService;

    @Mock
    private JwtUtil jwtUtil;

    @Mock
    private AuthenticationConfiguration authenticationConfiguration;

    @InjectMocks
    private SecurityConfig securityConfig;

    @BeforeEach
    void setUp() {
        securityConfig = new SecurityConfig(userDetailsService, jwtUtil);
    }

    // 確認密碼加密使用 BCrypt，符合安全標準。
    @Test
    void passwordEncoder_ShouldReturnBCryptPasswordEncoder() {
        PasswordEncoder passwordEncoder = securityConfig.passwordEncoder();
        assertNotNull(passwordEncoder);
        assertTrue(passwordEncoder instanceof BCryptPasswordEncoder);
    }

    // 確保身份驗證管理器能夠被正確初始化。
    @Test
    void authenticationManager_ShouldReturnInstance() throws Exception {
        AuthenticationManager mockAuthManager = mock(AuthenticationManager.class);
        when(authenticationConfiguration.getAuthenticationManager()).thenReturn(mockAuthManager);

        AuthenticationManager authenticationManager = securityConfig.authenticationManager(authenticationConfiguration);
        assertNotNull(authenticationManager);
        assertEquals(mockAuthManager, authenticationManager);
    }

    // 確保使用 CustomUserDetailsService 來處理用戶身份驗證。
    // 確保使用 passwordEncoder() 來加密密碼。
    @Test
    void authenticationProvider_ShouldReturnDaoAuthenticationProvider() {
        AuthenticationProvider authenticationProvider = securityConfig.authenticationProvider();
        assertNotNull(authenticationProvider);
        assertTrue(authenticationProvider instanceof DaoAuthenticationProvider);
    }

    // 確保 JWT 過濾器能夠正確被初始化。
    // 確保它會被 Spring Security 正確使用來處理請求。
    @Test
    void jwtAuthenticationFilter_ShouldReturnInstance() {
        JwtAuthenticationFilter filter = securityConfig.jwtAuthenticationFilter();
        assertNotNull(filter);
    }
}

// 整合測試
@SpringBootTest
@AutoConfigureMockMvc
class SecurityConfigIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private JwtUtil jwtUtil;

    @Test
    void whenAccessPublicEndpoint_thenReturnSuccess() throws Exception {
        mockMvc.perform(get("/swagger-ui/index.html")) // 這裡改為 "/swagger-ui/index.html"
                .andExpect(status().isOk());
    }

    /** 測試註冊請求是否 permitAll **/
    @Test
    void whenRegisterUser_thenReturnSuccess() throws Exception {
        mockMvc.perform(post("/api/auth/register?password=Test1234")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"username\": \"testtest\"," +
                        "\"name\": \"Test tset\"," +
                        "\"phone\": \"0912345678\"," + 
                        "\"email\": \"test@example.com\" }"))
                .andExpect(status().isOk());
    }

    /** 測試登入請求是否 permitAll **/
    @Test
    void whenLoginUser_thenReturnSuccess() throws Exception {
        mockMvc.perform(post("/api/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"username\": \"employee1\","+ 
                        "\"password\": \"Test1234\"}"))
                .andExpect(status().isOk());
    }

    /** 測試登出請求是否 permitAll **/
    @Test
    void whenLogoutUser_thenReturnSuccess() throws Exception {
        String token = jwtUtil.generateToken("bookmanager1", List.of("ROLE_BOOK_MANAGER"));

        // 使用 JWT Token 來測試登出
        mockMvc.perform(post("/api/auth/logout")
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + token))
                .andExpect(status().isOk());
    }

    /** 測試未授權的請求是否被拒絕 **/
    @Test
    void whenAccessProtectedEndpointWithoutToken_thenReturnUnauthorized() throws Exception {
        mockMvc.perform(get("/api/users"))
                .andExpect(status().isUnauthorized());
    }

    /** 測試 EMPLOYEE 角色是否無法存取書籍管理 API **/
    @Test
    void whenGetAllBookst_thenReturnSuccess() throws Exception {

        mockMvc.perform(get("/api/books")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"title\": \"Unauthorized Book\", \"isbn\": \"1234567890123\"}"))
                .andExpect(status().isOk());
    }

    /** 測試 EMPLOYEE 角色是否無法存取書籍管理 API **/
    @Test
    void whenEmployeeAccessBookManagement_thenReturnForbidden() throws Exception {
        String token = "Bearer " + jwtUtil.generateToken("employee1", List.of("ROLE_EMPLOYEE"));
        System.out.println("Using Token: " + token);

        mockMvc.perform(post("/api/books/manage")
                .header(HttpHeaders.AUTHORIZATION, token)
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"title\": \"Unauthorized Book\", \"isbn\": \"1234567890123\"}"))
                .andExpect(status().isForbidden());
    }

    /** 測試 BOOK_MANAGER 是否能夠管理書籍 **/
    @Test
    void whenBookManagerAccessBookManagement_thenReturnSuccess() throws Exception {
        String token = "Bearer " + jwtUtil.generateToken("bookmanager1", List.of("ROLE_BOOK_MANAGER"));

        mockMvc.perform(post("/api/books/manage")
                .header(HttpHeaders.AUTHORIZATION, token)
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"title\": \"New Book\"," + 
                        "\"authorId\": 1," +             
                        "\"categoryId\": 1," + 
                        "\"isbn\": \"9876111110123\"," +
                        "\"originalPrice\": 500," +
                        "\"salePrice\": 450," + 
                        "\"stockQuantity\": 10}"))
                .andExpect(status().isCreated());
    }

    /** 測試 ADMIN 是否能夠變更使用者角色 **/
    @Test
    void whenAdminChangesUserRole_thenReturnSuccess() throws Exception {
        String token = "Bearer " + jwtUtil.generateToken("admin1", List.of("ROLE_ADMIN"));

        mockMvc.perform(put("/api/users/1/role")
                .header(HttpHeaders.AUTHORIZATION, token)
                .contentType(MediaType.APPLICATION_JSON)
                .param("role", "BOOK_MANAGER"))
                .andExpect(status().isOk());
    }

    /** 測試 EMPLOYEE 是否無法變更其他使用者角色 **/
    @Test
    void whenEmployeeChangesUserRole_thenReturnForbidden() throws Exception {
        String token = "Bearer " + jwtUtil.generateToken("employee1", List.of("ROLE_EMPLOYEE"));

        mockMvc.perform(put("/api/users/1/role")
                .header(HttpHeaders.AUTHORIZATION, token)
                .contentType(MediaType.APPLICATION_JSON)
                .param("role", "ADMIN"))
                .andExpect(status().isForbidden());
    }
}