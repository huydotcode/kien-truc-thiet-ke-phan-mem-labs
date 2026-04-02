<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <title>PHP in Docker</title>
</head>
<body>
    <h1>Trang web PHP phục vụ bởi Apache</h1>
    <p>
        <?php 
            echo "Hello từ PHP phiên bản " . phpversion(); 
        ?>
    </p>
    <p>Nếu bạn thấy trang này thông qua Volume Mount, quá trình thành công!</p>
</body>
</html>
