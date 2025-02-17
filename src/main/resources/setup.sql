DROP DATABASE IF EXISTS bookstore;

CREATE DATABASE bookstore;
USE bookstore;

-- 刪除舊的表（如果已經存在）
DROP TABLE IF EXISTS Books;
DROP TABLE IF EXISTS Authors;
DROP TABLE IF EXISTS Categories;
DROP TABLE IF EXISTS UserRoles;
DROP TABLE IF EXISTS Roles;
DROP TABLE IF EXISTS Users;

-- 創建 Authors（作者表）
CREATE TABLE Authors (
    id INT AUTO_INCREMENT PRIMARY KEY,  -- 作者唯一識別碼
    Name VARCHAR(255) NOT NULL,                -- 作者姓名
    Biography TEXT,                            -- 作者簡介
    BirthDate DATE,                            -- 出生日期
    Nationality VARCHAR(100),                  -- 國籍
    CreatedAt TIMESTAMP DEFAULT CURRENT_TIMESTAMP, -- 創建時間
    UpdatedAt TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP -- 更新時間
);

-- 插入 "佚名" 作者，確保刪除作者時能指向它
INSERT INTO Authors (id, Name, Biography)
VALUES (1, '佚名', '作者不詳或已刪除');

-- 創建 Categories（書籍分類表）
CREATE TABLE Categories (
    id INT AUTO_INCREMENT PRIMARY KEY,  -- 類別唯一識別碼
    CategoryName VARCHAR(100) UNIQUE NOT NULL,  -- 類別名稱（唯一）
    Description TEXT,                           -- 類別描述
    CreatedAt TIMESTAMP DEFAULT CURRENT_TIMESTAMP, -- 創建時間
    UpdatedAt TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP -- 更新時間
);

-- 創建 Books（書籍表）
CREATE TABLE books (
    id INT AUTO_INCREMENT PRIMARY KEY,  -- 書籍唯一識別碼
    title VARCHAR(255) NOT NULL,             -- 書名
    
    author_id INT NOT NULL DEFAULT 1,        -- 預設為 "佚名"（author_id = 1）
    category_id INT,                          -- 類別 ID（可以為 NULL）
    
    isbn VARCHAR(13) UNIQUE NOT NULL,        -- 書籍 ISBN 編號
    original_price INT NOT NULL,    -- **定價**
    sale_price INT NOT NULL,        -- **售價**
    published_date DATE,                       -- 出版日期
    stock_quantity INT NOT NULL,               -- 庫存數量
    description TEXT NULL,            -- 簡介

    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,  -- 創建時間
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP, -- 更新時間

    FOREIGN KEY (author_id) REFERENCES authors(id) ON DELETE SET DEFAULT,  -- 刪除作者時變 "佚名"
    FOREIGN KEY (category_id) REFERENCES categories(id) ON DELETE SET NULL -- 刪除分類時設為 NULL
);


CREATE TABLE Roles (
    id INT AUTO_INCREMENT PRIMARY KEY,  -- 角色唯一識別碼
    role_name VARCHAR(50) NOT NULL UNIQUE   -- 角色名稱（EMPLOYEE, BOOK_MANAGER, ADMIN）
);

INSERT INTO Roles (role_name) 
VALUES
    ('EMPLOYEE'),
    ('BOOK_MANAGER'),
    ('ADMIN');

CREATE TABLE Users (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    Username VARCHAR(255) NOT NULL UNIQUE,
    Password VARCHAR(255) NOT NULL,
    Name VARCHAR(255) NOT NULL,
    Phone VARCHAR(20) NOT NULL UNIQUE,
    Email VARCHAR(255) NOT NULL UNIQUE,
    Created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    Updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
);

-- 管理使用者與角色的對應關係
CREATE TABLE user_roles   (
    user_id  BIGINT NOT NULL,
    role_id INT NOT NULL,
    PRIMARY KEY (user_id, role_id),
    FOREIGN KEY (user_id) REFERENCES Users(id) ON DELETE CASCADE,
    FOREIGN KEY (role_id) REFERENCES Roles(id) ON DELETE CASCADE
);


-- 測試數據：插入一些作者、類別和書籍
INSERT INTO Authors (Name, Biography, BirthDate, Nationality) 
VALUES 
    ('J.K. Rowling', 'British author, best known for Harry Potter.', '1965-07-31', 'British'),
    ('George Orwell', 'English novelist and critic, famous for 1984.', '1903-06-25', 'British'),
    ('Isaac Asimov', 'Science fiction writer, best known for the Foundation series.', '1920-01-02', 'American'),
    ('Arthur C. Clarke', 'British science fiction writer, known for 2001: A Space Odyssey.', '1917-12-16', 'British'),
    ('J.R.R. Tolkien', 'English writer and professor, best known for The Lord of the Rings.', '1892-01-03', 'British'),
    ('Agatha Christie', 'Famous detective novel writer, known for Hercule Poirot series.', '1890-09-15', 'British'),
    ('Stephen King', 'Master of horror and supernatural fiction.', '1947-09-21', 'American'),
    ( 'Walter Isaacson', 'American writer and journalist, known for biographies of historical figures.', '1952-05-20', 'American'),  -- AuthorID = 9
    ( 'Yuval Noah Harari', 'Israeli historian and professor, known for Sapiens.', '1976-02-24', 'Israeli'),  -- AuthorID = 10
    ( 'Robert C. Martin', 'Software engineer and author of Clean Code.', '1952-12-05', 'American'),  -- AuthorID = 11
    ( 'Andrew Hunt & David Thomas', 'Software developers, authors of The Pragmatic Programmer.', NULL, 'American');  -- AuthorID = 12


INSERT INTO Categories (CategoryName, Description) 
VALUES 
    ('Fiction', 'Fictional books including novels and stories.'),
    ('Science Fiction', 'Books based on speculative scientific concepts.'),
    ('Mystery', 'Books that involve solving crimes or uncovering secrets.'),
    ('Fantasy', 'Books with magical or supernatural elements.'),
    ('Horror', 'Books designed to scare and thrill readers.'),
    ('Non-Fiction', 'Books based on real events, biographies, and historical accounts.'),
    ('Technology', 'Books covering technological advancements and computer science.');

INSERT INTO Books (title, author_id, category_id, isbn, original_price, sale_price, published_date, stock_quantity, Description) 
VALUES 
    ('Harry Potter and the Sorcerer''s Stone', 2, 1, '9780439708180', 494, 360, '1997-06-26', 100, 'A young wizard discovers his magical heritage and embarks on an epic journey at Hogwarts.'),
    ('1984', 3, 2, '9780451524935', 490, 387, '1949-06-08', 50, 'A dystopian novel depicting a totalitarian regime that controls every aspect of life.'),
    ('Foundation', 4, 3, '9780553293357', 520, 450, '1951-06-01', 75, 'A classic sci-fi novel exploring the rise and fall of civilizations.'),
    ('2001: A Space Odyssey', 5, 3, '9780451457999', 499, 400, '1968-07-16', 60, 'A novel about human evolution, AI, and space exploration.'),
    ('The Hobbit', 6, 2, '9780345339683', 580, 480, '1937-09-21', 90, 'The adventure of Bilbo Baggins in the fantasy world of Middle-earth.'),
    ('Murder on the Orient Express', 7, 1, '9780062693662', 390, 350, '1934-01-01', 100, 'A famous detective novel featuring Hercule Poirot.'),
    ('It', 8, 3, '9781501142970', 699, 650, '1986-09-15', 55, 'A horror novel about a shape-shifting entity terrorizing children.'),
    ('The Shining', 8, 3, '9780385121675', 640, 580, '1977-01-28', 40, 'A psychological horror novel set in a haunted hotel.'),
    ('Steve Jobs', 9, 4, '9781451648539', 720, 650, '2011-10-24', 30, 'A biography of Apple co-founder Steve Jobs.'),
    ('Sapiens: A Brief History of Humankind', 10, 4, '9780062316097', 850, 780, '2011-06-04', 50, 'A historical analysis of the evolution of humans.'),
    ('Clean Code', 11, 5, '9780132350884', 820, 750, '2008-08-01', 45, 'A book about writing maintainable and efficient code.'),
    ('The Pragmatic Programmer', 12, 5, '9780201616224', 890, 830, '1999-10-20', 35, 'A guide to software development best practices.');

-- 插入測試 Users 資料
INSERT INTO Users (Username, Password, Name, Phone, Email)
VALUES 
    ('admin', '$2a$10$jPs7pVPOfDyOavI/Lfeuiu.zk38t2oXTYK8ObYtxRDi3N2.9INfe2', 'Admin User', '123456789', 'admin@example.com'),
    ('employee1', '$2a$10$hQTMVepx5rcZ8MXs1nMw5ur6TOP0m/0QkycDAxuGG.Y7xUBqonvPm', '員工A', '0911111111', 'employee1@example.com'),
    ('bookmanager1', '$2a$10$hQTMVepx5rcZ8MXs1nMw5ur6TOP0m/0QkycDAxuGG.Y7xUBqonvPm', '書籍管理員A', '0933333333', 'bookmanager1@example.com'),
    ('admin1', '$2a$10$hQTMVepx5rcZ8MXs1nMw5ur6TOP0m/0QkycDAxuGG.Y7xUBqonvPm', '主管A', '0955555555', 'admin1@example.com');

-- 指派角色到使用者（UserRoles）
INSERT INTO user_roles   (user_id, role_id) 
VALUES
    (1, 3),
    (2, 1), -- 員工1 角色: EMPLOYEE
    (3, 2), -- 書籍管理1 角色: BOOK_MANAGER
    (4, 3);