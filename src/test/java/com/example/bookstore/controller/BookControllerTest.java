package com.example.bookstore.controller;

import com.example.bookstore.dto.BookDto;
import com.example.bookstore.entity.Book;
import com.example.bookstore.service.BookService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(SpringExtension.class)
@SpringBootTest
public class BookControllerTest {

    private MockMvc mockMvc;

    @Mock
    private BookService bookService;

    @InjectMocks
    private BookController bookController;

    @BeforeEach
    public void setup() {
        MockitoAnnotations.openMocks(this);
        mockMvc = MockMvcBuilders.standaloneSetup(bookController).build();
    }

    @Test
    public void testGetAllBooks() throws Exception {
        BookDto book1 = new BookDto();
        book1.setTitle("Book One");
        book1.setIsbn("1234567890123");
        book1.setOriginalPrice(500);
        book1.setSalePrice(450);
        book1.setStockQuantity(10);
        book1.setPublishedDate(LocalDate.of(2020, 1, 1));

        BookDto book2 = new BookDto();
        book2.setTitle("Book Two");
        book2.setIsbn("9876543210987");
        book2.setOriginalPrice(600);
        book2.setSalePrice(550);
        book2.setStockQuantity(5);
        book2.setPublishedDate(LocalDate.of(2021, 5, 10));

        List<BookDto> books = Arrays.asList(book1, book2);
        when(bookService.getAllBooks()).thenReturn(books);

        mockMvc.perform(get("/api/books"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(2))
                .andExpect(jsonPath("$[0].title").value("Book One"))
                .andExpect(jsonPath("$[1].title").value("Book Two"));
    }

    @Test
    public void testGetBookById() throws Exception {
        BookDto book = new BookDto();
        book.setTitle("Book One");
        book.setIsbn("1234567890123");
        book.setOriginalPrice(500);
        book.setSalePrice(450);
        book.setStockQuantity(10);
        book.setPublishedDate(LocalDate.of(2020, 1, 1));

        when(bookService.getBookById(anyLong())).thenReturn(Optional.of(book));

        mockMvc.perform(get("/api/books/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.title").value("Book One"))
                .andExpect(jsonPath("$.isbn").value("1234567890123"));
    }

    @Test
    public void testCreateBook() throws Exception {
        BookDto bookDto = new BookDto();
        bookDto.setTitle("New Book");
        bookDto.setIsbn("1234567890123");
        bookDto.setOriginalPrice(700);
        bookDto.setSalePrice(650);
        bookDto.setStockQuantity(20);
        bookDto.setPublishedDate(LocalDate.of(2022, 8, 15));

        Book createdBook = new Book();
        createdBook.setBookId(1L);
        createdBook.setTitle(bookDto.getTitle());
        createdBook.setIsbn(bookDto.getIsbn());
        createdBook.setOriginalPrice(bookDto.getOriginalPrice());
        createdBook.setSalePrice(bookDto.getSalePrice());
        createdBook.setStockQuantity(bookDto.getStockQuantity());
        createdBook.setPublishedDate(bookDto.getPublishedDate());

        when(bookService.createBook(any(BookDto.class))).thenReturn(createdBook);

        mockMvc.perform(post("/api/books/manage")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"title\":\"New Book\", \"isbn\":\"1234567890123\", \"originalPrice\":700, \"salePrice\":650, \"stockQuantity\":20, \"publishedDate\":\"2022-08-15\"}"))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.title").value("New Book"))
                .andExpect(jsonPath("$.isbn").value("1234567890123"));
    }

    @Test
    public void testUpdateBook() throws Exception {
        BookDto bookDto = new BookDto();
        bookDto.setTitle("Updated Book");
        bookDto.setIsbn("1234567890123");
        bookDto.setOriginalPrice(750);
        bookDto.setSalePrice(700);
        bookDto.setStockQuantity(15);
        bookDto.setPublishedDate(LocalDate.of(2023, 1, 1));

        when(bookService.updateBook(anyLong(), any(BookDto.class))).thenReturn(bookDto);

        mockMvc.perform(put("/api/books/1/manage")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"title\":\"Updated Book\", \"isbn\":\"1234567890123\", \"originalPrice\":750, \"salePrice\":700, \"stockQuantity\":15, \"publishedDate\":\"2023-01-01\"}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.title").value("Updated Book"))
                .andExpect(jsonPath("$.isbn").value("1234567890123"));
    }

    @Test
    public void testDeleteBook() throws Exception {
        doNothing().when(bookService).deleteBook(anyLong());

        mockMvc.perform(delete("/api/books/1/manage"))
                .andExpect(status().isNoContent());
    }
}
