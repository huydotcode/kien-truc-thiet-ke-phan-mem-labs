# Sử dụng node:18 làm base image
FROM node:18

# Thiết lập thư mục làm việc trong container
WORKDIR /usr/src/app

# Copy các file package.json và package-lock.json (nếu có)
COPY package*.json ./

# Cài đặt dependencies (Trong ứng dụng này không dùng dependency ngoài nhưng đây là best practice)
RUN npm install

# Copy toàn bộ mã nguồn vào thư mục làm việc trong container
COPY . .

# Thông báo container sẽ lắng nghe trên cổng 3000
EXPOSE 3000

# Lệnh để khởi chạy ứng dụng
CMD ["npm", "start"]
