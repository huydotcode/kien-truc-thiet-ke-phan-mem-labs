# Stage 1: Build stage (Dùng node:18 để build code)
FROM node:18 AS builder

WORKDIR /app

# Copy các file config dependency
COPY package*.json ./

# Cài đặt tất cả dependencies (bao gồm devDependencies như Typescript)
RUN npm install

# Copy configuration và mã nguồn src/
COPY tsconfig.json ./
COPY src ./src

# Thực hiện quá trình "build" (compile code TS sang JS và lưu vào thư mục /app/dist)
RUN npm run build

# ========================================== #

# Stage 2: Production stage (Dùng node:18-alpine để chạy ứng dụng đã build)
FROM node:18-alpine

WORKDIR /app

# Copy file dependency lại một lần nữa
COPY package*.json ./

# Chỉ cài đặt production dependencies (Bỏ qua typescript và @types để tiết kiệm dung lượng)
RUN npm install --omit=dev

# Lấy thư mục 'dist' (chứa code đã compile) từ Stage 1 mang sang Stage 2
COPY --from=builder /app/dist ./dist

EXPOSE 3000

# Chạy project
CMD ["npm", "start"]
