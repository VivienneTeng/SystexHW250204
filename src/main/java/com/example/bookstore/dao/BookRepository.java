package com.example.bookstore.dao;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;       //用來操作資料庫
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;                   //Spring註解(@Repository)，代表這個類別是 DAO 層

import com.example.bookstore.entity.Book;                                   //引入 Book 實體類別
import java.util.List;


//BookRepository 介面 繼承 JpaRepository<Book, Long>，具備基本的 CRUD 操作
@Repository
public interface BookRepository extends JpaRepository<Book, Long> {
    // 查詢特定作者的書籍
    List<Book> findByAuthorId(Long athorId);

    // 查詢特定分類的書籍
    List<Book> findByCategoryId(Long categoryId);

    // 查詢價格範圍內的書籍
    List<Book> findBySalePriceBetween(Integer minPrice, Integer maxPrice);

    // 查詢庫存數量少於指定值的書籍
    List<Book> findByStockQuantityLessThan(Integer stockLimit);

    // 使用 @Query 來寫 JPQL：註解自訂 SQL 查詢指定 ISBN 的書籍
    @Query("SELECT b FROM Book b WHERE b.isbn = :isbn")
    Book findByIsbn(@Param("isbn") String isbn);

    // 依分類查詢，並支援分頁與排序
    Page<Book> findByCategoryIdPaged(Long categoryId, Pageable pageable);
}

