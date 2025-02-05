package com.example.bookstore.service.impl;

import com.example.bookstore.dao.BookRepository;
import com.example.bookstore.dto.BookDto;
import com.example.bookstore.entity.Book;
import com.example.bookstore.exception.ResourceNotFoundException;
import com.example.bookstore.service.BookService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.Collections;

//未來需要使用 Collectors.toList() 來處理 List<Book> 的轉換
//import java.util.stream.Collectors;

@Service
public class BookServiceImpl implements BookService {

    @Autowired
    private BookRepository bookRepository;

    @Override
    public Book createBook(BookDto bookDto) {
        Book book = convertToEntity(bookDto);
        book.setCreatedAt(LocalDateTime.now());
        book.setUpdatedAt(LocalDateTime.now());
        return bookRepository.save(book);
    }

    @Override
    public BookDto updateBook(Long id, BookDto bookDto) throws ResourceNotFoundException {
        Book existingBook = bookRepository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("Book not found with id: " + id));

        // 使用 convertToEntity(bookDto) 轉換，但保留 bookId 和 createdAt
        Book updatedBook = convertToEntity(bookDto);
        updatedBook.setBookId(existingBook.getBookId()); // 保留 ID
        updatedBook.setCreatedAt(existingBook.getCreatedAt()); // 保留原始創建時間
        updatedBook.setUpdatedAt(LocalDateTime.now()); // 更新最後修改時間

        bookRepository.save(updatedBook);

        return bookDto; 
    }

    @Override
    public List<Book> getAllBooks() {
        List<Book> books = bookRepository.findAll();
        return books.isEmpty() ? Collections.emptyList() : books;
    }


    @Override
    public Optional<Book> getBookById(Long id) throws ResourceNotFoundException {
        return Optional.ofNullable(bookRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Book not found with id: " + id)));
    }

    @Override
    public void deleteBook(Long id) throws ResourceNotFoundException {
        Book book = bookRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Book not found with id: " + id));
        bookRepository.delete(book);
    }

    private Book convertToEntity(BookDto bookDto) {
        Book book = new Book();
        book.setTitle(bookDto.getTitle());
        book.setIsbn(bookDto.getIsbn());
        book.setAuthorId(bookDto.getAuthorId());
        book.setCategoryId(bookDto.getCategoryId());
        book.setPublishedDate(bookDto.getPublishedDate());
        book.setOriginalPrice(bookDto.getOriginalPrice());
        book.setSalePrice(bookDto.getSalePrice());
        book.setStockQuantity(bookDto.getStockQuantity());
        book.setDescription(bookDto.getDescription());
        book.setUpdatedAt(LocalDateTime.now());
        return book;
    }
}
