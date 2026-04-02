# Sử dụng base image là PHP 8.2 có tích hợp sẵn web server Apache
FROM php:8.2-apache

# Thư mục chứa mã nguồn mặc định bảo vệ bởi Apache
WORKDIR /var/www/html/

# Copy mã nguồn hiện tại vào thư mục phục vụ của Apache. 
# (Trường hợp Mount thì bước này làm nền, mã host sẽ ghi đè vào bước runtime)
COPY src/ .

# Expose cổng số 80 của Apache
EXPOSE 80
