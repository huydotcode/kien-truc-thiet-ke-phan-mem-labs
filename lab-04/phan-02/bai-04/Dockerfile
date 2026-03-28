# Sử dụng nginx:latest làm base image
FROM nginx:latest

# Copy file index.html từ máy host vào thư mục chứa static files mặc định của web server Nginx
COPY index.html /usr/share/nginx/html/

# Mở cổng 80 (cổng mặc định của web server HTTP Nginx)
EXPOSE 80

# Image của nginx đã cấu hình sẵn lệnh khởi chạy Nginx ngầm, nên không cần CMD
