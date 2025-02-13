package com.example.bookstore.controller;

import com.example.bookstore.dto.UserDto;
import com.example.bookstore.service.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.List;
import java.util.Optional;
import java.util.Set;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(MockitoExtension.class)
class UserControllerTest {

    private MockMvc mockMvc;

    @Mock
    private UserService userService;

    @InjectMocks
    private UserController userController;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(userController).build();
    }

    @Test
    void getAllUsers_ShouldReturnUsers() throws Exception {
        UserDto user = new UserDto();
        user.setUsername("testuser");
        user.setName("Test User");
        user.setPhone("123456789");
        user.setEmail("test@example.com");
        user.setRoles(Set.of("EMPLOYEE"));

        when(userService.getAllUsers()).thenReturn(List.of(user));

        mockMvc.perform(get("/api/users"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].username").value("testuser"));
    }

    @Test
    void getUserById_ShouldReturnUser_WhenUserExists() throws Exception {
        UserDto user = new UserDto();
        user.setUsername("testuser");
        user.setName("Test User");
        user.setPhone("123456789");
        user.setEmail("test@example.com");
        user.setRoles(Set.of("EMPLOYEE"));

        when(userService.getUserById(anyLong())).thenReturn(Optional.of(user));

        mockMvc.perform(get("/api/users/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.username").value("testuser"));
    }

    @Test
    void updateUser_ShouldUpdateAndReturnUser() throws Exception {
        UserDto user = new UserDto();
        user.setUsername("updateduser");
        user.setName("Updated User");
        user.setPhone("987654321");
        user.setEmail("updated@example.com");
        user.setRoles(Set.of("ADMIN"));

        when(userService.updateUser(anyLong(), any(UserDto.class))).thenReturn(user);

        mockMvc.perform(put("/api/users/1/manage")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"username\":\"updateduser\", \"name\":\"Updated User\", \"phone\":\"987654321\", \"email\":\"updated@example.com\", \"roles\":[\"ADMIN\"]}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.username").value("updateduser"));
    }

    @Test
    void deleteUser_ShouldReturnSuccessMessage() throws Exception {
        Mockito.doNothing().when(userService).deleteUser(anyLong());

        mockMvc.perform(delete("/api/users/1/manage"))
                .andExpect(status().isOk())
                .andExpect(content().string("User deleted successfully"));
    }

    @Test
    void updateUserRole_ShouldReturnSuccessMessage() throws Exception {
        Mockito.doNothing().when(userService).assignRoleToUser(anyLong(), any());

        mockMvc.perform(put("/api/users/1/role?role=ADMIN"))
                .andExpect(status().isOk())
                .andExpect(content().string("User role updated successfully"));
    }
}
