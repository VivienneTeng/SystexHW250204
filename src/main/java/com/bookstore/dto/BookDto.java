package com.bookstore.dto;

import java.time.LocalDate;
import java.time.LocalDateTime;

import lombok.Data;

@Data
public class BookDto {
    private String title;
    private Long bookId;
    private String isbn;

    private Long authorId;              // 前端輸入時使用
    private String authorName;          // 前端顯示時使用

    private Long categoryId;            // 前端輸入時使用
    private String categoryName;        // 前端顯示時使用

    private LocalDate publishedDate;
    private Integer originalPrice;
    private Integer salePrice;
    private Integer stockQuantity;
    private String describe;
    
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
