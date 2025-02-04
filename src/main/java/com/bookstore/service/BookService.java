package com.bookstore.service;

import com.bookstore.dto.BookDto;
import com.bookstore.entity.Book;
import com.bookstore.exception.ResourceNotFoundException;

import java.util.List;
import java.util.Optional;

public interface BookService {
    Book createBook(BookDto bookDto);
    List<Book> getAllBooks();
    Optional<Book> getBookById(Long id) throws ResourceNotFoundException;
    BookDto updateBook(Long id, BookDto bookDto) throws ResourceNotFoundException;
    void deleteBook(Long id) throws ResourceNotFoundException;
}
