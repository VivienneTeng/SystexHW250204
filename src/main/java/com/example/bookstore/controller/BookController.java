package com.example.bookstore.controller;

import com.example.bookstore.dto.BookDto;
import com.example.bookstore.dto.UserDto;
import com.example.bookstore.entity.Book;
import com.example.bookstore.entity.User;
import com.example.bookstore.exception.ResourceNotFoundException;
import com.example.bookstore.service.BookService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/books")
public class BookController {

    @Autowired
    private BookService bookService;

    // 取得所有書籍
    @GetMapping
    public List<BookDto> getAllBooks() {
        return bookService.getAllBooks();
    }

    // 取得特定書籍（透過 ID）
    @GetMapping("/{id}")
    public ResponseEntity<BookDto> getBookById(@PathVariable Long id) {
        Optional<BookDto> book = bookService.getBookById(id);
        return book.map(ResponseEntity::ok)         //如果書籍存在，則回傳 ResponseEntity.ok(book)
                .orElseThrow(() -> new ResourceNotFoundException("Book not found with id: " + id));
    }

    // 新增書籍
    @PostMapping
    @PreAuthorize("hasRole('BOOK_MANAGER')")
    public ResponseEntity<Book> createBook(@RequestBody BookDto bookDto) {
        Book createdBook = bookService.createBook(bookDto);
        return ResponseEntity.status(HttpStatus.CREATED).body(createdBook);
        // HttpStatus.CREATED (201) 表示 成功建立
    }

    // 更新書籍
    @PutMapping("/{id}")
    @PreAuthorize("hasRole('BOOK_MANAGER')")
    public ResponseEntity<BookDto> updateBook(@PathVariable Long id, @RequestBody BookDto bookDto) {
        return ResponseEntity.ok(bookService.updateBook(id, bookDto));
    }

    // 刪除書籍
    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('BOOK_MANAGER')")
    public ResponseEntity<Void> deleteBook(@PathVariable Long id) {
        bookService.deleteBook(id);
        return ResponseEntity.noContent().build();
        //回傳 204 No Content（表示成功，但沒有回應內容）
    }
}
