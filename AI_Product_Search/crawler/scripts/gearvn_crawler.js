const puppeteer = require('puppeteer');
const axios = require('axios');
require('dotenv').config();

/**
 * Script mẫu cào dữ liệu từ GearVN (Ví dụ)
 * Bạn có thể thay đổi link để cào các sàn khác.
 */
async function crawlGearVN() {
    console.log('--- ĐANG BẮT ĐẦU CÀO DỮ LIỆU GEARVN ---');
    const browser = await puppeteer.launch({ headless: "new" });
    const page = await browser.newPage();
    
    // Giả lập User Agent để tránh bị chặn
    await page.setUserAgent('Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/119.0.0.0 Safari/537.36');

    try {
        // Link danh mục sản phẩm (ví dụ: Laptop)
        const url = 'https://gearvn.com/collections/laptop-gaming-pho-thong';
        await page.goto(url, { waitUntil: 'networkidle2', timeout: 60000 });

        console.log('Đang phân tích trang: ' + url);

        // Chờ các card sản phẩm load xong
        await page.waitForSelector('.product-row');

        // Lấy danh sách sản phẩm
        const products = await page.evaluate(() => {
            const items = [];
            const cards = document.querySelectorAll('.product-row');
            
            cards.forEach(card => {
                const name = card.querySelector('.product-row-name')?.innerText.trim();
                const currentPriceStr = card.querySelector('.product-row-sale')?.innerText.replace(/[^0-9]/g, '');
                const oldPriceStr = card.querySelector('.product-row-old-price')?.innerText.replace(/[^0-9]/g, '');
                const image = card.querySelector('.product-row-img img')?.src;
                const link = card.querySelector('a')?.href;

                if (name && currentPriceStr) {
                    items.push({
                        name,
                        salePrice: parseInt(currentPriceStr),
                        originalPrice: oldPriceStr ? parseInt(oldPriceStr) : parseInt(currentPriceStr),
                        mainImage: image,
                        originalLink: link,
                        source: 'gearvn',
                        brand: 'ASUS', // Giả định brand mẫu
                        category: 'Laptop'
                    });
                }
            });
            return items;
        });

        console.log(`Tìm thấy ${products.length} sản phẩm. Đang lưu vào Database...`);

        // Gửi dữ liệu về Backend API (Giả định Backend có endpoint nhận dữ liệu)
        for (const product of products) {
            try {
                // Chúng ta sẽ trực tiếp lưu vào DB hoặc gọi API
                // Ở đây tôi in ra log để bạn xem trước
                console.log(`> Đã cào: ${product.name} | Giá: ${product.salePrice}đ`);
            } catch (err) {
                console.error('Lỗi khi gửi sản phẩm:', err.message);
            }
        }

    } catch (error) {
        console.error('CRAWL ERROR:', error);
    } finally {
        await browser.close();
        console.log('--- HOÀN TẤT CÀO DỮ LIỆU ---');
    }
}

crawlGearVN();