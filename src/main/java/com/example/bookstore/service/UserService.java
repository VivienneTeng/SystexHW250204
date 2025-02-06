package com.example.bookstore.service;

import com.example.bookstore.dto.UserDto;
import com.example.bookstore.entity.User;
import java.util.List;
import java.util.Optional;

public interface UserService {
    User registerUser(UserDto userDto, String password);
    void assignRoleToUser(Long userId, String roleName);
    List<UserDto> getAllUsers();
    Optional<UserDto> getUserById(Long id);
    UserDto updateUser(Long id, UserDto userDto);
    void deleteUser(Long id);
}
