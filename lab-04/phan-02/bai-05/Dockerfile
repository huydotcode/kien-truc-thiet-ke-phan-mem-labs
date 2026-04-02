# Sử dụng image golang siêu nhẹ làm base image
FROM golang:1.20-alpine

# Cài đặt thư mục làm việc trong container
WORKDIR /app

# Copy file go.mod (nếu có thêm thư viện thì copy cả go.sum)
COPY go.mod ./

# Copy mã nguồn Go vào container
COPY *.go ./

# Build ứng dụng Go thành một file thực thi mang tên 'main'
RUN go build -o main .

# Thông báo container sẽ dùng cổng 8080
EXPOSE 8080

# Chạy file thực thi 'main'
CMD ["./main"]
