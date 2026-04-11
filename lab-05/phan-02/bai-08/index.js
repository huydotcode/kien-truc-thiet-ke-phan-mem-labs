const express = require('express');
const mysql = require('mysql2');
const app = express();
const port = 3000;

// Tạo kết nối (sử dụng thông tin từ env)
const connection = mysql.createPool({
  host: process.env.DB_HOST || 'db',
  user: process.env.DB_USER || 'user',
  password: process.env.DB_PASSWORD || 'password',
  database: process.env.DB_NAME || 'mydb',
  waitForConnections: true,
  connectionLimit: 10,
  queueLimit: 0
});

app.get('/', (req, res) => {
  connection.query('SELECT 1 + 1 AS solution', (err, results) => {
    if (err) {
      return res.status(500).send('Kết nối Database thất bại: ' + err.message);
    }
    res.send(`<h1>Kết nối thành công!</h1><p>MySQL trả về: ${results[0].solution}</p>`);
  });
});

app.listen(port, () => {
  console.log(`Server Node.js đang chạy tại http://localhost:${port}`);
});
