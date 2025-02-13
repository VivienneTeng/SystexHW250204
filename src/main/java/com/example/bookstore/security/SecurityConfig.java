package com.example.bookstore.security;

import com.example.bookstore.security.JwtAuthenticationFilter;
import com.example.bookstore.security.JwtUtil;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import jakarta.servlet.http.HttpServletResponse;


@Configuration          // 告訴 Spring 這是一個 組態類別（Configuration Class）
@EnableWebSecurity      // 啟用 Spring Security，讓專案的 API 受到保護
public class SecurityConfig {

    // 加載使用者資料的介面
    private final CustomUserDetailsService userDetailsService;
    private final JwtUtil jwtUtil;
    
    public SecurityConfig(CustomUserDetailsService userDetailsService, JwtUtil jwtUtil) {
        this.userDetailsService = userDetailsService;
        this.jwtUtil = jwtUtil;
    }

    // 密碼加密
    @Bean   // Spring 自動管理這個物件
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();     //BCrypt 雜湊加密
    }

    // 身份驗證管理器
    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration authConfig) throws Exception {
        return authConfig.getAuthenticationManager();       // 取得 Spring 預設的身份驗證機制
    }

    // 身份驗證提供者
    @Bean
    public AuthenticationProvider authenticationProvider() {
        //DaoAuthenticationProvider 是 Spring 提供的 使用資料庫驗證帳號密碼的方式
        DaoAuthenticationProvider authProvider = new DaoAuthenticationProvider();
        authProvider.setUserDetailsService(userDetailsService);
        authProvider.setPasswordEncoder(passwordEncoder());
        return authProvider;
    }

    @Bean
    public JwtAuthenticationFilter jwtAuthenticationFilter() {
        return new JwtAuthenticationFilter(jwtUtil, userDetailsService);
    }


    // 安全性設定
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
            .csrf(csrf -> csrf.disable()) 
            .authorizeHttpRequests(auth -> auth

                .requestMatchers("/swagger-ui/**", "/swagger-ui.html", "/v3/api-docs/**").permitAll()
                .requestMatchers("/api/auth/**").permitAll() // 允許未登入訪問api，所有人皆可註冊、登入、登出

                //.requestMatchers("/api/books/**").hasRole("BOOK_MANAGER") 
                .requestMatchers("POST", "/api/books/manage").hasRole("BOOK_MANAGER")
                .requestMatchers("PUT", "/api/books/{id}/manage").hasRole("BOOK_MANAGER")
                .requestMatchers("DELETE", "/api/books/{id}/mange").hasRole("BOOK_MANAGER")
                // 只有 BOOK_MANAGER 可新增、更新、刪除書籍
                
                .requestMatchers("PUT", "/api/users/{id}/manage").hasRole("ADMIN")
                .requestMatchers("DELETE", "/api/users/{id}/manage").hasRole("ADMIN")
                .requestMatchers("PUT", "/api/users/{id}/role").hasRole("ADMIN")
                // 只有 ADMIN 可變更員工角色、更新員工資訊、刪除員工

                .requestMatchers("/api/books", "/api/books/{id}").permitAll() // 所有人可查詢書籍或特定書籍
                .requestMatchers("/api/users", "/api/users/{id}").hasAnyAuthority("ROLE_EMPLOYEE", "ROLE_BOOK_MANAGER", "ROLE_ADMIN")
                // EMPLOYEE、BOOK_MANAGER、ADMIN可查詢所有員工、特定員工
                
                .anyRequest().authenticated()
            )
            .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))       // 讓 API 無狀態
            .formLogin(login -> login.disable())    // 禁用 Spring Security 的預設登入表單
            .logout(logout -> logout
                .logoutUrl("/api/auth/logout") // 指定登出端點
                .logoutSuccessHandler((request, response, authentication) -> {
                    response.setStatus(HttpServletResponse.SC_OK);
                    response.getWriter().write("{\"message\": \"Logged out successfully\"}");
                })
                .permitAll()
            )
            .httpBasic(basic -> basic.disable());

        // 加入 JWT 過濾器
        http.addFilterBefore(jwtAuthenticationFilter(), UsernamePasswordAuthenticationFilter.class);


        return http.build();
    }
}
