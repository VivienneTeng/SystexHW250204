package com.bookstore.dto;

import lombok.Data;

@Data
public class BookDto {
    private String title;
    private String author;
    private String description;
    private Double price;
    private Double salePrice;
}
