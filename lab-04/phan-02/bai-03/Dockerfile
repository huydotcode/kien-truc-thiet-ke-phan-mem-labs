FROM node:20-alpine

# Thiết lập thư mục làm việc trong container
WORKDIR /app

# Copy file package.json và package-lock.json để tận dụng Docker cache
COPY package*.json ./

# Cài đặt các thư viện
RUN npm install

# Copy toàn bộ mã nguồn còn lại vào container
COPY . .

# Build ứng dụng React (sẽ tạo ra thư mục 'dist')
RUN npm run build

# Cài đặt một HTTP server đơn giản để phục vụ file tĩnh
RUN npm install -g serve

# Mở cổng 3000 để truy cập ứng dụng
EXPOSE 3000

# Chạy web server cho thư mục 'dist' đã được build
CMD ["serve", "-s", "dist", "-l", "3000"]
