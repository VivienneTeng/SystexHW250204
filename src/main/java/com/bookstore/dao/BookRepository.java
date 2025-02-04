package com.bookstore.dao;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import com.bookstore.entity.Book;

@Repository
public interface BookRepository extends JpaRepository<Book, Long> {
}
