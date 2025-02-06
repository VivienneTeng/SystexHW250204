package com.example.bookstore.entity;

import jakarta.persistence.*;       //JPA (Java Persistence API) 的標準，用於對應資料庫的表格
import lombok.Getter;
import lombok.Setter;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

@Getter
@Setter
@Entity
@Table(name = "Users")
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "UserId") 
    private Long userId;

    @Column(name = "Username", unique = true, nullable = false)
    private String username;

    @Column(name = "Name", nullable = false)
    private String name;

    @Column(name = "Password", nullable = false)
    private String password;

    @Column(name = "Phone", unique = true, nullable = false)
    private String phone;

    @Column(name = "Email", unique = true, nullable = false)
    private String email;

    @Column(name = "CreatedAt", nullable = false, updatable = false)
    private LocalDateTime createdAt = LocalDateTime.now();

    @Column(name = "UpdatedAt", nullable = false)
    private LocalDateTime updatedAt = LocalDateTime.now();

    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(
        name = "UserRoles",
        joinColumns = @JoinColumn(name = "UserId"),
        inverseJoinColumns = @JoinColumn(name = "RoleId")
    )
    private Set<Role> roles = new HashSet<>();

    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }
}

