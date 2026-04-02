# Sử dụng bản PostgreSQL theo yêu cầu
FROM postgres:15

# Docker image của Postgres mặc định sẽ tự động chạy tất cả các script
# (có đuôi .sql, .sql.gz, hoặc .sh) được tìm thấy trong thư mục /docker-entrypoint-initdb.d/
# khi container được tạo ra LẦN ĐẦU TIÊN (database trống).
COPY init.sql /docker-entrypoint-initdb.d/

# Cổng mặc định của PostgreSQL
EXPOSE 5432
