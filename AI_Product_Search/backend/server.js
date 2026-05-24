require('dotenv').config();
const express = require('express');
const cors = require('cors');
const helmet = require('helmet');
const morgan = require('morgan');
const connectDB = require('./src/config/db');
const { initMeiliSearch } = require('./src/config/meili');

// Khởi tạo app
const app = express();

// Middleware
app.use(express.json());
app.use(cors());
app.use(helmet());
app.use(morgan('dev'));

// Kết nối Database & Search Engine
connectDB();

// Route cơ bản kiểm tra health
app.get('/api/health', async (req, res) => {
  res.json({ status: 'OK', message: 'AI Product Search Engine API is running' });
});

const PORT = process.env.PORT || 5000;

app.listen(PORT, async () => {
  console.log(`Server running on port ${PORT}`);
  // Khởi tạo settings cho Meilisearch khi server start
  await initMeiliSearch();
});