# Sử dụng python:3.9-slim làm base image vì nó nhẹ
FROM python:3.9-slim

# Thiết lập thư mục làm việc trong container
WORKDIR /app

# Đặt biến môi trường APP_ENV với giá trị 'development'
ENV APP_ENV=development

# Copy mã nguồn vào container
COPY app.py .

# Chạy mã nguồn Python khi container khởi động
CMD ["python", "app.py"]
