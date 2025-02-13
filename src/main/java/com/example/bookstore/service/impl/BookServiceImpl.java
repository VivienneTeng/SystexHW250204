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
import java.util.stream.Collectors;

//未來需要使用 Collectors.toList() 來處理 List<Book> 的轉換
//import java.util.stream.Collectors;

@Service
public class BookServiceImpl implements BookService {

    @Autowired
    private BookRepository bookRepository;
    
    @Override
    public Book createBook(BookDto bookDto) {
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

        return bookRepository.save(book);
    }

    
    @Override
    public BookDto updateBook(Long id, BookDto bookDto) throws ResourceNotFoundException {
        
        Book existingBook = bookRepository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("Book not found with id: " + id));

        existingBook.setTitle(bookDto.getTitle());
        existingBook.setIsbn(bookDto.getIsbn());
        existingBook.setAuthorId(bookDto.getAuthorId());
        existingBook.setCategoryId(bookDto.getCategoryId());
        existingBook.setPublishedDate(bookDto.getPublishedDate());
        existingBook.setOriginalPrice(bookDto.getOriginalPrice());
        existingBook.setSalePrice(bookDto.getSalePrice());
        existingBook.setStockQuantity(bookDto.getStockQuantity());
        existingBook.setDescription(bookDto.getDescription());
        existingBook.setUpdatedAt(LocalDateTime.now()); // 更新最後修改時間

        bookRepository.save(existingBook);

        return convertToDto(existingBook);
    }

    @Override
    public List<BookDto> getAllBooks() {
        return bookRepository.findAll().stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }
    @Override
    public Optional<BookDto> getBookById(Long id) throws ResourceNotFoundException {
        return bookRepository.findById(id).map(this::convertToDto);
    }

    @Override
    public void deleteBook(Long id) throws ResourceNotFoundException {
        Book book = bookRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Book not found with id: " + id));
        bookRepository.delete(book);
    }

    private BookDto convertToDto(Book book) {
        BookDto dto = new BookDto();
        dto.setTitle(book.getTitle());
        dto.setIsbn(book.getIsbn());
        dto.setAuthorId(book.getAuthorId());
        dto.setCategoryId(book.getCategoryId());
        dto.setPublishedDate(book.getPublishedDate());
        dto.setOriginalPrice(book.getOriginalPrice());
        dto.setSalePrice(book.getSalePrice());
        dto.setStockQuantity(book.getStockQuantity());
        dto.setDescription(book.getDescription());
        dto.setCreatedAt(book.getCreatedAt());
        dto.setUpdatedAt(book.getUpdatedAt());
        //dto.setUpdatedAt(LocalDateTime.now());
        return dto;
    }
}
