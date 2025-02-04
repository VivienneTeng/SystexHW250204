package com.bookstore.service;

import com.bookstore.dto.BookDto;
import com.bookstore.entity.Book;
import java.util.List;
import java.util.Optional;

public interface BookService {
    Book createBook(BookDto bookDto);
    List<Book> getAllBooks();
    Optional<Book> getBookById(Long id);
    Book updateBook(Long id, BookDto bookDto);
    void deleteBook(Long id);
}
