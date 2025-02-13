package com.example.bookstore.controller;

import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.web.bind.annotation.*;

import com.example.bookstore.entity.User;
import com.example.bookstore.security.JwtUtil;
import com.example.bookstore.service.UserService;
import com.example.bookstore.dto.*;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.authentication.AuthenticationManager;
import java.util.List;
import java.util.Map;


@RestController
@RequestMapping("/api/auth")
public class AuthContorller {

    @Autowired
    private UserService userService;

    @Autowired
    private JwtUtil jwtUtil;

    @Autowired
    private AuthenticationManager authenticationManager;
    
    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody LoginRequest loginRequest) {
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        loginRequest.getUsername(),
                        loginRequest.getPassword()
                )
        );

        // 確保 `authentication` 被使用，設置到 SecurityContext
        SecurityContextHolder.getContext().setAuthentication(authentication);

        // 使用 `authentication` 來獲取 username
        String username = authentication.getName();
        
        // 從 Authentication 取得使用者的角色
        UserDetails userDetails = (UserDetails) authentication.getPrincipal();
        List<String> roles = userDetails.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .collect(Collectors.toList());

        // 使用 username 產生 JWT Token
        String token = jwtUtil.generateToken(username, roles);

        return ResponseEntity.ok(new JwtResponse(token));
    }

    // 註冊新員工
    @PostMapping("/register")
    public ResponseEntity<User> register(@RequestBody UserDto userDto, @RequestParam String password) {
        User newUser = userService.registerUser(userDto, password);
        return ResponseEntity.ok(newUser);
    }

    @PostMapping("/logout")
    public ResponseEntity<String> logout(@RequestHeader("Authorization") String token) {
        SecurityContextHolder.clearContext(); // 清除當前使用者的安全上下文
        return ResponseEntity.ok("Logged out successfully");
    }

    @PostMapping("/forgot-password")
    public ResponseEntity<Map<String, String>> forgotPassword(@RequestBody ResetPasswordRequest request) {
        Map<String, String> response = userService.sendResetPasswordEmail(request.getEmail());
        return ResponseEntity.ok(response);
    }

    @PostMapping("/reset-password")
    public ResponseEntity<String> resetPassword(@RequestParam String token, @RequestParam String newPassword) {
        userService.resetPassword(token, newPassword);
        return ResponseEntity.ok("Password reset successful");
    }

}
