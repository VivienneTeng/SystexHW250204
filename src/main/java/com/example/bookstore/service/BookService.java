package com.example.bookstore.service;

import com.example.bookstore.dto.BookDto;
import com.example.bookstore.entity.Book;
import com.example.bookstore.exception.ResourceNotFoundException;

import java.util.List;
import java.util.Optional;

public interface BookService {
    Book createBook(BookDto bookDto);
    List<BookDto> getAllBooks();
    Optional<BookDto> getBookById(Long id) throws ResourceNotFoundException;
    BookDto updateBook(Long id, BookDto bookDto) throws ResourceNotFoundException;
    void deleteBook(Long id) throws ResourceNotFoundException;
}
