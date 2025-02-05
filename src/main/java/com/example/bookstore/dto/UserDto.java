package com.example.bookstore.dto;

import lombok.Data;
import java.util.Set;

@Data
public class UserDto {
    private String username;
    private String name;
    private String phone;
    private String email;
    private Set<String> roles; // 角色清單
}
