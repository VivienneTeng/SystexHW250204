package com.example.bookstore.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Getter
@Setter
@Entity
@Table(name = "Books")
public class Book {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")  
    private Long bookId;

    @Column(nullable = false, length = 255)  
    private String title;

    @Column(nullable = false)  
    private Long authorId;

    @Column(nullable = true)  
    private Long categoryId;

    @Column(nullable = false, length = 13, unique = true)  
    private String isbn;

    @Column(nullable = false) 
    private Integer originalPrice;

    @Column(nullable = false) 
    private Integer salePrice; 

    @Column(nullable = true)  
    private LocalDate publishedDate;

    @Column(nullable = false) 
    private Integer stockQuantity;

    @Column(name = "Description", columnDefinition = "TEXT", nullable = true) 
    private String description;
    
    @Column(nullable = false, updatable = false) 
    private LocalDateTime createdAt = LocalDateTime.now();

    @Column(nullable = false)  
    private LocalDateTime updatedAt = LocalDateTime.now();
}
