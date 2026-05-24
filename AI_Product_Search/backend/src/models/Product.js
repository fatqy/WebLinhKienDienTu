const mongoose = require('mongoose');

const productSchema = new mongoose.Schema({
  name: { type: String, required: true, trim: true },
  slug: { type: String, lowercase: true, unique: true },
  description: { type: String },
  brand: { type: String, index: true },
  category: { type: String, index: true },
  
  // Giá cả
  originalPrice: { type: Number, required: true },
  salePrice: { type: Number },
  discountPercentage: { type: Number },
  
  // Hình ảnh
  mainImage: { type: String, required: true },
  images: [String],
  
  // Thông tin sàn & Affiliate
  source: { type: String, enum: ['shopee', 'lazada', 'amazon', 'gearvn'], required: true },
  originalLink: { type: String, required: true },
  affiliateLink: { type: String },
  shopName: { type: String },
  shopLocation: { type: String },
  
  // AI & Ranking Metrics
  rating: { type: Number, default: 0 },
  reviewCount: { type: Number, default: 0 },
  salesCount: { type: Number, default: 0 },
  stock: { type: Number, default: 0 },
  
  // AI Vector Embedding (Dùng cho Semantic Search sau này)
  vectorEmbedding: [Number],
  
  // Ranking Score (Hệ thống AI tự tính điểm)
  rankingScore: { type: Number, default: 0 },
  
  tags: [String],
  isActive: { type: Boolean, default: true },
  freshness: { type: Date, default: Date.now }
}, {
  timestamps: true
});

// Middleware tự động tính % giảm giá trước khi lưu
productSchema.pre('save', function(next) {
  if (this.salePrice && this.originalPrice) {
    this.discountPercentage = Math.round(((this.originalPrice - this.salePrice) / this.originalPrice) * 100);
  }
  next();
});

module.exports = mongoose.model('Product', productSchema);