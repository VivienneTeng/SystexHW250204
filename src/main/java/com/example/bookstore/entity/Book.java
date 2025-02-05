package com.example.bookstore.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Getter
@Setter
@Entity
@Table(name = "books")
public class Book {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "BookID")  
    private Long bookId;

    @Column(name = "Title", nullable = false, length = 255)  
    private String title;

    @Column(name = "AuthorID", nullable = false)  
    private Long authorId;

    @Column(name = "CategoryId", nullable = true)  
    private Long categoryId;

    @Column(name = "ISBN", nullable = false, length = 13, unique = true)  
    private String isbn;

    @Column(name = "OriginalPrice", nullable = false) 
    private Integer originalPrice;

    @Column(name = "SalePrice", nullable = true) 
    private Integer salePrice; 

    @Column(name = "PublishedDate", nullable = true)  
    private LocalDate publishedDate;

    @Column(name = "StockQuantity", nullable = false) 
    private Integer stockQuantity;

    @Column(name = "Description", columnDefinition = "TEXT", nullable = true) 
    private String description;
    
    @Column(name = "CreatedAt", nullable = false, updatable = false) 
    private LocalDateTime createdAt = LocalDateTime.now();

    @Column(name = "UpdatedAt", nullable = false)  
    private LocalDateTime updatedAt = LocalDateTime.now();
}
