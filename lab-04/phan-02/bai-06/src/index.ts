import express from 'express';

const app = express();
const port = 3000;

app.get('/', (req, res) => {
  res.send('Hello from Multi-stage Docker build with TypeScript!');
});

app.listen(port, () => {
  console.log(`App listening at http://0.0.0.0:${port}`);
});
