import React from 'react';
import ReactDOM from 'react-dom/client';

const root = ReactDOM.createRoot(document.getElementById('root'));
root.render(
  <React.StrictMode>
    <div style={{ textAlign: 'center', marginTop: '50px', fontFamily: 'Arial' }}>
      <h1>Hello from React!</h1>
      <p>Ứng dụng này đã được build và serve bởi <b>Nginx</b> trong Docker.</p>
    </div>
  </React.StrictMode>
);
