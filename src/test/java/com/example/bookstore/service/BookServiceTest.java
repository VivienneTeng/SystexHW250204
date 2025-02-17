package com.example.bookstore.service;

import com.example.bookstore.dao.BookRepository;
import com.example.bookstore.dto.BookDto;
import com.example.bookstore.entity.Book;
import com.example.bookstore.exception.ResourceNotFoundException;
import com.example.bookstore.service.impl.BookServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.Arrays;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class BookServiceTest {

    @Mock
    private BookRepository bookRepository;

    @InjectMocks
    private BookServiceImpl bookService;

    private Book book;
    private BookDto bookDto;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        book = new Book();
        book.setBookId(1L);
        book.setTitle("Test Book");
        book.setIsbn("1234567890123");
        book.setAuthorId(1L);
        book.setCategoryId(1L);
        book.setOriginalPrice(500);
        book.setSalePrice(450);
        book.setPublishedDate(LocalDate.now());
        book.setStockQuantity(10);
        book.setDescription("This is a test book");

        bookDto = new BookDto();
        bookDto.setTitle("Test Book");
        bookDto.setIsbn("1234567890123");
        bookDto.setAuthorId(1L);
        bookDto.setCategoryId(1L);
        bookDto.setOriginalPrice(500);
        bookDto.setSalePrice(450);
        bookDto.setPublishedDate(LocalDate.now());
        bookDto.setStockQuantity(10);
        bookDto.setDescription("This is a test book");
    }

    @Test
    void testCreateBook() {
        when(bookRepository.save(any(Book.class))).thenReturn(book);
        Book createdBook = bookService.createBook(bookDto);
        assertNotNull(createdBook);
        assertEquals("Test Book", createdBook.getTitle());
        verify(bookRepository, times(1)).save(any(Book.class));
    }

    @Test
    void testUpdateBook_Success() {
        when(bookRepository.findById(1L)).thenReturn(Optional.of(book));
        when(bookRepository.save(any(Book.class))).thenReturn(book);

        BookDto updatedBookDto = new BookDto();
        updatedBookDto.setTitle("Updated Title");
        updatedBookDto.setIsbn("1234567890123");
        updatedBookDto.setAuthorId(2L);
        updatedBookDto.setCategoryId(2L);
        updatedBookDto.setOriginalPrice(600);
        updatedBookDto.setSalePrice(550);
        updatedBookDto.setPublishedDate(LocalDate.now());
        updatedBookDto.setStockQuantity(5);
        updatedBookDto.setDescription("Updated Description");

        BookDto result = bookService.updateBook(1L, updatedBookDto);
        assertEquals("Updated Title", result.getTitle());
        verify(bookRepository, times(1)).save(any(Book.class));
    }

    @Test
    void testUpdateBook_NotFound() {
        when(bookRepository.findById(1L)).thenReturn(Optional.empty());
        assertThrows(ResourceNotFoundException.class, () -> bookService.updateBook(1L, bookDto));
        verify(bookRepository, never()).save(any(Book.class));
    }

    @Test
    void testGetBookById_Found() {
        when(bookRepository.findById(1L)).thenReturn(Optional.of(book));
        Optional<BookDto> result = bookService.getBookById(1L);
        assertTrue(result.isPresent());
        assertEquals("Test Book", result.get().getTitle());
        verify(bookRepository, times(1)).findById(1L);
    }

    @Test
    void testGetBookById_NotFound() {
        when(bookRepository.findById(1L)).thenReturn(Optional.empty());
        Optional<BookDto> result = bookService.getBookById(1L);
        assertFalse(result.isPresent());
    }

    @Test
    void testGetAllBooks() {
        when(bookRepository.findAll()).thenReturn(Arrays.asList(book));
        List<BookDto> books = bookService.getAllBooks();
        assertEquals(1, books.size());
        assertEquals("Test Book", books.get(0).getTitle());
        verify(bookRepository, times(1)).findAll();
    }

    @Test
    void testDeleteBook_Success() {
        when(bookRepository.findById(1L)).thenReturn(Optional.of(book));
        doNothing().when(bookRepository).delete(book);
        bookService.deleteBook(1L);
        verify(bookRepository, times(1)).delete(book);
    }

    @Test
    void testDeleteBook_NotFound() {
        when(bookRepository.findById(1L)).thenReturn(Optional.empty());
        assertThrows(ResourceNotFoundException.class, () -> bookService.deleteBook(1L));
        verify(bookRepository, never()).delete(any(Book.class));
    }
}
