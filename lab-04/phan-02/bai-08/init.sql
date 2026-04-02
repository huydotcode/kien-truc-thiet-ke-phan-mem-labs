-- Tạo database mới (Tùy chọn, vì có thể tạo bằng POSTGRES_DB env var)
CREATE DATABASE my_custom_db;

-- Chuyển sang context của database mới để thiết lập bảng
\c my_custom_db;

-- Tạo bảng users
CREATE TABLE IF NOT EXISTS users (
    id SERIAL PRIMARY KEY,
    username VARCHAR(50) NOT NULL,
    email VARCHAR(100) UNIQUE NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- Thêm một số dữ liệu mẫu
INSERT INTO users (username, email) VALUES 
('admin', 'admin@example.com'),
('testuser', 'test@example.com');
