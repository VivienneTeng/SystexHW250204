package com.example.bookstore.service.impl;

import com.example.bookstore.dao.UserRepository;
import com.example.bookstore.dao.RoleRepository;
import com.example.bookstore.dto.UserDto;
import com.example.bookstore.entity.User;
import com.example.bookstore.entity.Role;
import com.example.bookstore.service.UserService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.Map;
import java.util.UUID;


@Service
public class UserServiceImpl implements UserService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private RoleRepository roleRepository;

    @Override
    public User registerUser(UserDto userDto, String password) {
        User user = new User();
        user.setUsername(userDto.getUsername());
        user.setPassword(passwordEncoder.encode(password));
        user.setName(userDto.getName());
        user.setPhone(userDto.getPhone());
        user.setEmail(userDto.getEmail());

        // 預設角色為 EMPLOYEE
        Role defaultRole = roleRepository.findByRoleName("EMPLOYEE")
                .orElseThrow(() -> new RuntimeException("Role not found"));
        user.setRoles(Set.of(defaultRole));

        return userRepository.save(user);
    }

    private Map<String, String> resetTokens = new HashMap<>();

    @Override
    public Map<String, String> sendResetPasswordEmail(String email) {
        User user = userRepository.findByEmail(email)
            .orElseThrow(() -> new RuntimeException("User not found"));

        String token = UUID.randomUUID().toString();
        resetTokens.put(token, user.getEmail());

        // 直接回傳 email 和重置連結
        Map<String, String> response = new HashMap<>();
        response.put("email", user.getEmail());
        response.put("resetLink", "http://localhost:8080/api/auth/reset-password?token=" + token);
        return response;
    }

    public void resetPassword(String token, String newPassword) {
        String email = resetTokens.get(token);
        if (email == null) {
            throw new RuntimeException("Invalid or expired token");
        }

        User user = userRepository.findByEmail(email)
            .orElseThrow(() -> new RuntimeException("User not found"));

        user.setPassword(passwordEncoder.encode(newPassword));
        userRepository.save(user);

        resetTokens.remove(token);
    }

    @Override
    public void assignRoleToUser(Long userId, String roleName) {
        User user = userRepository.findById(userId).orElseThrow(() -> new RuntimeException("User not found"));
        Role role = roleRepository.findByRoleName(roleName).orElseThrow(() -> new RuntimeException("Role not found"));

        if (!user.getRoles().contains(role)) {
            user.getRoles().add(role);
        }
        userRepository.save(user);
    }

    @Override
    public List<UserDto> getAllUsers() {
        return userRepository.findAll().stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }

    @Override
    public Optional<UserDto> getUserById(Long id) {
        return userRepository.findById(id).map(this::convertToDto);
    }

    @Override
    public UserDto updateUser(Long id, UserDto userDto) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("User not found"));
        user.setName(userDto.getName());
        user.setPhone(userDto.getPhone());
        user.setEmail(userDto.getEmail());

        // 轉換 Set<String> -> Set<Role>
        Set<Role> roleEntities = userDto.getRoles().stream()
                .map(roleName -> roleRepository.findByRoleName(roleName)
                        .orElseThrow(() -> new RuntimeException("Role not found: " + roleName)))
                .collect(Collectors.toSet());

        user.setRoles(roleEntities); // 設定轉換後的角色集合

        userRepository.save(user);
        return convertToDto(user);
    }


    @Override
    public void deleteUser(Long id) {
        userRepository.deleteById(id);
    }

    private UserDto convertToDto(User user) {
        UserDto dto = new UserDto();
        dto.setUsername(user.getUsername());
        dto.setName(user.getName());
        dto.setPhone(user.getPhone());
        dto.setEmail(user.getEmail());
    
        // 轉換 Set<Role> -> Set<String>
        Set<String> roleNames = user.getRoles().stream()
                .map(Role::getRoleName) // 取出 roleName
                .collect(Collectors.toSet());
    
        dto.setRoles(roleNames);
        return dto;
    }
    
}
