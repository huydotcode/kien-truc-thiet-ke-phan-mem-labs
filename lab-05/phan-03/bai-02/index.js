const express = require('express');
const mongoose = require('mongoose');
const app = express();
const port = 3000;

const mongoUri = process.env.MONGO_URI || 'mongodb://mongodb:27017/mydb';

mongoose.connect(mongoUri)
  .then(() => console.log('Successfully connected to MongoDB!'))
  .catch(err => console.error('MongoDB connection error:', err));

app.get('/', (req, res) => {
  res.json({
    message: 'Hello from Node.js with MongoDB!',
    dbStatus: mongoose.connection.readyState === 1 ? 'Connected' : 'Disconnected'
  });
});

app.listen(port, () => {
  console.log(`Server is running on port ${port}`);
});
