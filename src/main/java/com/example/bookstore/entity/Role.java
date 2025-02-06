package com.example.bookstore.entity;

import jakarta.persistence.*;
import lombok.Getter;

@Getter
@Entity
@Table(name = "Roles")
public class Role {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long roleId;
    
    @Column(name = "RoleName", unique = true, nullable = false)
    private String roleName;

    public Role() {}

    public Role(String roleName) {
        this.roleName = roleName;
    }

    // Getters & Setters
}
