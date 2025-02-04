DROP DATABASE IF EXISTS bookstore;

CREATE DATABASE bookstore;
USE bookstore;

-- 刪除舊的表（如果已經存在）
DROP TABLE IF EXISTS Books;
DROP TABLE IF EXISTS Authors;
DROP TABLE IF EXISTS Categories;

-- 創建 Authors（作者表）
CREATE TABLE Authors (
    AuthorID INT AUTO_INCREMENT PRIMARY KEY,  -- 作者唯一識別碼
    Name VARCHAR(255) NOT NULL,                -- 作者姓名
    Biography TEXT,                            -- 作者簡介
    BirthDate DATE,                            -- 出生日期
    Nationality VARCHAR(100),                  -- 國籍
    CreatedAt TIMESTAMP DEFAULT CURRENT_TIMESTAMP, -- 創建時間
    UpdatedAt TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP -- 更新時間
);

-- 插入 "佚名" 作者，確保刪除作者時能指向它
INSERT INTO Authors (AuthorID, Name, Biography)
VALUES (1, '佚名', '作者不詳或已刪除');

-- 創建 Categories（書籍分類表）
CREATE TABLE Categories (
    CategoryID INT AUTO_INCREMENT PRIMARY KEY,  -- 類別唯一識別碼
    CategoryName VARCHAR(100) UNIQUE NOT NULL,  -- 類別名稱（唯一）
    Description TEXT,                           -- 類別描述
    CreatedAt TIMESTAMP DEFAULT CURRENT_TIMESTAMP, -- 創建時間
    UpdatedAt TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP -- 更新時間
);

-- 創建 Books（書籍表）
CREATE TABLE Books (
    BookID INT AUTO_INCREMENT PRIMARY KEY,  -- 書籍唯一識別碼
    Title VARCHAR(255) NOT NULL,             -- 書名
    
    AuthorID INT NOT NULL DEFAULT 1,        -- 預設為 "佚名"（AuthorID = 1）
    CategoryID INT,                          -- 類別 ID（可以為 NULL）
    
    ISBN VARCHAR(13) UNIQUE NOT NULL,        -- 書籍 ISBN 編號
    OriginalPrice INT NOT NULL,    -- **定價**
    SalePrice INT NOT NULL,        -- **售價**
    PublishedDate DATE,                       -- 出版日期
    StockQuantity INT NOT NULL,               -- 庫存數量
    Describe TEXT NULL             -- 簡介

    CreatedAt TIMESTAMP DEFAULT CURRENT_TIMESTAMP,  -- 創建時間
    UpdatedAt TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP, -- 更新時間

    FOREIGN KEY (AuthorID) REFERENCES Authors(AuthorID) ON DELETE SET DEFAULT,  -- 刪除作者時變 "佚名"
    FOREIGN KEY (CategoryID) REFERENCES Categories(CategoryID) ON DELETE SET NULL -- 刪除分類時設為 NULL
);

-- 測試數據：插入一些作者、類別和書籍
INSERT INTO Authors (Name, Biography, BirthDate, Nationality) 
VALUES 
    ('J.K. Rowling', 'British author, best known for Harry Potter.', '1965-07-31', 'British'),
    ('George Orwell', 'English novelist and critic, famous for 1984.', '1903-06-25', 'British');

INSERT INTO Categories (CategoryName, Description) 
VALUES 
    ('Fiction', 'Fictional books including novels and stories.'),
    ('Science Fiction', 'Books based on speculative scientific concepts.');

INSERT INTO Books (Title, AuthorID, CategoryID, ISBN, OriginalPrice, SalePrice, PublishedDate, StockQuantity, Describe) 
VALUES 
    ('Harry Potter and the Sorcerer''s Stone', 2, 1, '9780439708180', 494, 360, '1997-06-26', 100, 'A young wizard discovers his magical heritage and embarks on an epic journey at Hogwarts.'),
    ('1984', 3, 2, '9780451524935', 490, 387, '1949-06-08', 50, 'A dystopian novel depicting a totalitarian regime that controls every aspect of life.');
