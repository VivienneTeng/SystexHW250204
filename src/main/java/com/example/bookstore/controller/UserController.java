package com.example.bookstore.controller;

import com.example.bookstore.dto.UserDto;
import com.example.bookstore.dto.LoginRequest;
import com.example.bookstore.dto.JwtResponse;
import com.example.bookstore.entity.User;
import com.example.bookstore.security.JwtUtil;
import com.example.bookstore.service.UserService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.web.bind.annotation.*;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.authentication.AuthenticationManager;


import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/users")
public class UserController {

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
        
        // 使用 username 產生 JWT Token
        String token = jwtUtil.generateToken(username);

        return ResponseEntity.ok(new JwtResponse(token));
    }

    // 註冊新員工
    @PostMapping("/register")
    public ResponseEntity<User> register(@RequestBody UserDto userDto, @RequestParam String password) {
        User newUser = userService.registerUser(userDto, password);
        return ResponseEntity.ok(newUser);
    }

    // 讓 ADMIN 變更員工角色
    @PutMapping("/{id}/role")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<String> updateUserRole(@PathVariable Long id, @RequestParam String role) {
        userService.assignRoleToUser(id, role);
        return ResponseEntity.ok("User role updated successfully");
    }

    // 查詢所有員工
    @GetMapping
    public List<UserDto> getAllUsers() {
        return userService.getAllUsers();
    }

    // 查詢特定員工
    @GetMapping("/{id}")
    public ResponseEntity<UserDto> getUserById(@PathVariable Long id) {
        Optional<UserDto> user = userService.getUserById(id);
        return user.map(ResponseEntity::ok).orElse(ResponseEntity.notFound().build());
    }

    // 更新員工資訊
    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<UserDto> updateUser(@PathVariable Long id, @RequestBody UserDto userDto) {
        return ResponseEntity.ok(userService.updateUser(id, userDto));
    }

    // 刪除員工
    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<String> deleteUser(@PathVariable Long id) {
        userService.deleteUser(id);
        return ResponseEntity.ok("User deleted successfully");
    }
}
