# Sử dụng image Redis mới nhất
FROM redis:latest

# Thiết lập thư mục chứa config
WORKDIR /usr/local/etc/redis/

# Copy file cấu hình tùy chỉnh vào container
COPY redis.conf /usr/local/etc/redis/redis.conf

# Mở cổng mặc định của redis
EXPOSE 6379

# Chạy redis server ngầm với file cấu hình vừa copy
CMD [ "redis-server", "/usr/local/etc/redis/redis.conf" ]
