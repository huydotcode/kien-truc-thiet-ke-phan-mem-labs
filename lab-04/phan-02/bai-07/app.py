import os

# Đọc biến môi trường APP_ENV, nếu không tồn tại sẽ trả về giá trị mặc định thứ 2
app_env = os.environ.get('APP_ENV', 'Chưa được thiết lập')

print("====================================")
print(f"Giá trị của biến môi trường APP_ENV là: {app_env}")
print("====================================")
