# Sử dụng python:3.9 làm base image theo yêu cầu
FROM python:3.9

# Thiết lập thư mục làm việc trong container
WORKDIR /app

# Copy file requirements.txt vào container trước để tận dụng Docker cache
COPY requirements.txt .

# Cài đặt các thư viện cần thiết
RUN pip install --no-cache-dir -r requirements.txt

# Copy toàn bộ mã nguồn vào thư mục làm việc trong container
COPY . .

# Thông báo container sẽ lắng nghe trên cổng 5000
EXPOSE 5000

# Lệnh để khởi chạy ứng dụng Flask
CMD ["python", "app.py"]
