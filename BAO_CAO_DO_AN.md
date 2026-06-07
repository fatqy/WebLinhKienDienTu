# KỊCH BẢN THUYẾT TRÌNH BÁO CÁO ĐỒ ÁN MÔN HỌC
**HỆ THỐNG THƯƠNG MẠI ĐIỆN TỬ LINH KIỆN MÁY TÍNH**

*Tài liệu này dùng để thuyết trình và trả lời vấn đáp của Giáo viên. Bạn có thể copy toàn bộ nội dung này dán vào Microsoft Word để in ra cầm tay.*

---

## A. PHẦN CƠ BẢN (7 ĐIỂM)

### 1. Phân hệ dành cho Khách hàng (User)

**1.1. Đăng ký / Đăng nhập / Quên mật khẩu**
- **Thuyết trình:** Hệ thống cung cấp luồng xác thực an toàn, mã hóa mật khẩu người dùng và hỗ trợ tính năng cấp lại mật khẩu thông qua token.
- **Code nằm ở đâu:** 
  - **Controller:** `src/main/java/.../controller/AuthController.java` (Chứa các hàm `@GetMapping("/login")`, `@PostMapping("/register")`).
  - **Service:** `UserService.java` (Xử lý mã hóa mật khẩu BCrypt, tạo Token quên mật khẩu).
  - **Giao diện:** `login.html`, `register.html`, `forgot-password.html`, `reset-password.html`.

**1.2. Trang chủ**
- **Thuyết trình:** Trang chủ được thiết kế để gây ấn tượng ngay lập tức, lấy dữ liệu động từ Database để hiển thị Sản phẩm mới nhất, Sản phẩm đang Sale.
- **Code nằm ở đâu:**
  - **Controller:** `ProductController.java` -> Hàm `getIndexPage()` gọi repository để lấy Top 8 sản phẩm mới (`findTop8ByOrderByIdDesc()`).
  - **Giao diện:** `index.html`.

**1.3. Tìm kiếm & Lọc (Filter)**
- **Thuyết trình:** Hệ thống có bộ lọc theo tên, danh mục, hãng sản xuất và một thanh lọc giá linh hoạt chặn số âm.
- **Code nằm ở đâu:**
  - **Controller:** `ProductController.java` -> Hàm `getCollectionPage()`.
  - **Repository:** `ProductRepository.java` -> Hàm `@Query` JPQL `findByFilters()` để thực thi truy vấn tìm kiếm ở tầng CSDL.
  - **Giao diện:** `linh-kien-may-tinh.html` (Thanh lọc giá và Danh mục bên tay trái).

**1.4. Chi tiết sản phẩm**
- **Thuyết trình:** Hiển thị toàn bộ dữ liệu của một mặt hàng cụ thể, bao gồm ảnh, giá cũ/mới, số lượng tồn kho và phần bình luận đánh giá.
- **Code nằm ở đâu:**
  - **Controller:** `ProductController.java` -> Hàm `getProductDetail()` ánh xạ tới URL `/product/{id}`.
  - **Giao diện:** `product-detail.html`.

**1.5. Giỏ hàng**
- **Thuyết trình:** Giỏ hàng cho phép khách hàng tăng/giảm số lượng, nếu giảm về 0 sẽ tự động xóa khỏi giỏ. Có Giỏ hàng ẩn (Mini Cart) trên Header.
- **Code nằm ở đâu:**
  - **Controller & Service:** `CartController.java` và `CartService.java` (Xử lý `updateQuantity`, `removeFromCart`).
  - **Giao diện:** `cart.html` và đoạn Script API trên `fragments.html` (Mini cart).

**1.6. Thanh toán**
- **Thuyết trình:** Cho phép khách nhập thông tin giao nhận, điền mã giảm giá, và lựa chọn giữa thanh toán COD hoặc quét mã QR Chuyển khoản ví điện tử.
- **Code nằm ở đâu:**
  - **Controller:** `CheckoutController.java`.
  - **Service:** `OrderService.java` -> Hàm `placeOrder()` (Xử lý lưu hóa đơn).
  - **Giao diện:** `checkout.html`.

**1.7. Lịch sử đơn hàng**
- **Thuyết trình:** Khách hàng có thể theo dõi tiến trình đơn hàng của mình (Chờ duyệt -> Đang giao -> Đã giao -> Hủy).
- **Code nằm ở đâu:**
  - **Controller:** `OrderController.java` -> Hàm `getOrderHistory()`.
  - **Giao diện:** `order-history.html`.

---

### 2. Phân hệ dành cho Quản trị viên (Admin)

**2.1. Bảng điều khiển (Dashboard)**
- **Thuyết trình:** Hiển thị thống kê tổng quan doanh thu, số lượng đơn hàng, số lượng khách hàng bằng các chỉ số trực quan.
- **Code nằm ở đâu:** `AdminController.java` (Thống kê Count bằng Repository) và `admin/dashboard.html`.

**2.2. Quản lý Danh mục (Category)**
- **Thuyết trình:** Thêm/Sửa/Xóa các danh mục lớn của hệ thống.
- **Code nằm ở đâu:** `AdminCategoryController.java` và `admin/categories.html`.

**2.3. Quản lý Sản phẩm (Product & Inventory)**
- **Thuyết trình:** Thêm mới sản phẩm, quản lý số lượng tồn kho, giá gốc, giá khuyến mãi. Điểm nổi bật là việc xử lý Upload file ảnh.
- **Code nằm ở đâu:** `AdminProductController.java` (có hàm upload file multipart) và `admin/products.html`.

**2.4. Quản lý Đơn hàng (Orders)**
- **Thuyết trình:** Nơi Admin phê duyệt đơn hàng, thay đổi trạng thái (Ví dụ từ Pending sang Shipping).
- **Code nằm ở đâu:** `AdminOrderController.java` (hàm updateOrderStatus) và `admin/orders.html`.

**2.5. Quản lý Người dùng (Users)**
- **Thuyết trình:** Admin xem danh sách khách hàng, có thể khóa tài khoản nếu phát hiện gian lận.
- **Code nằm ở đâu:** `AdminUserController.java` (hàm toggleUserStatus) và `admin/users.html`.

**2.6. Quản lý Đánh giá (Reviews)**
- **Thuyết trình:** Quản lý bình luận, kiểm duyệt hiện/ẩn bình luận spam trên trang sản phẩm.
- **Code nằm ở đâu:** `AdminReviewController.java` và `admin/reviews.html`.

---

## B. PHẦN NÂNG CAO (3 ĐIỂM)

### 1. Yêu cầu chi tiết về Nghiệp vụ (Backend Logic)

**1.1. Quản lý Kho (Inventory Management)**
- **Trừ số lượng khi mua:** Nằm ở `OrderService.java`, bên trong hàm `placeOrder()`. Khi lưu đơn hàng thành công, hệ thống sẽ gọi `product.setStock(product.getStock() - item.getQuantity())`.
- **Thông báo sắp hết hàng:** Nằm ở `AdminProductController.java`. Nếu `stock < 5` (hoặc ngưỡng thấp), hệ thống có thể hiển thị cảnh báo đỏ trên trang danh sách sản phẩm admin.
- **Ngăn thêm quá số lượng tồn:** Nằm ở `CartService.java`, hàm `addToCart()` và `updateQuantity()`. Có dòng code kiểm tra `if (item.getProduct().getStock() < quantity) throw Exception`.

**1.2. Hệ thống Khuyến mãi (Coupons)**
- **Logic:** Mã giảm giá lưu trong DB với Discount Type (`FIXED` hoặc `PERCENTAGE`) và `MinOrderValue`.
- **Code:** Khi nhập mã ở `checkout.html`, nó gọi qua API `CheckoutController.java` (`/api/coupon/validate`). Khi submit đơn hàng, `OrderService.java` chịu trách nhiệm kiểm tra lần cuối điều kiện giá tối thiểu và đảm bảo mỗi user chỉ dùng mã 1 lần.

**1.3. Trạng thái Đơn hàng Logic**
- **Quy định luồng:** Sử dụng hằng số String (`PENDING`, `SHIPPING`, `DELIVERED`, `CANCELLED`).
- **Khách hàng tự hủy đơn:** Nằm ở `OrderController.java`, hàm `cancelOrder()`. Hệ thống kiểm tra chặt chẽ `if ("PENDING".equals(order.getStatus()))` mới cho phép hủy.

### 2. Yêu cầu về UX/UI (Trải nghiệm người dùng)

- **Lọc kết hợp (Multi-filtering) & Sắp xếp:** Tích hợp trong Form của `linh-kien-may-tinh.html` và lấy `@RequestParam` trong `ProductController.java`. Form tự động submit bảo toàn nhiều bộ lọc.
- **Xem nhanh (Quick View):** Khi bấm con mắt, sử dụng Javascript `fetch()` gọi API backend `/api/product/{id}` trả về thông tin rồi mở Modal đè lên màn hình hiện tại thay vì nhảy trang. Code Modal nằm trong `fragments.html`.
- **Lưu phiên (Remember Me):** Spring Security tự động cấu hình tính năng này trong `SecurityConfig.java` với `.rememberMe()`.
- **Phản hồi Toast/Alert:** Hệ thống bắt `FlashAttributes` và `RuntimeException` từ Controller, truyền ra màn hình giao diện để báo lỗi.

### 3. Yêu cầu về Bảo mật & Hệ thống

- **Phân quyền (Authorization):** Sử dụng `SecurityConfig.java`. Cấu hình `.requestMatchers("/admin/**").hasRole("ADMIN")`. Khách thường gõ link Admin sẽ bị Spring chặn và ném về `/login` hoặc `403.html`.
- **Validation (Kiểm tra đầu vào):**
  - Frontend: Sử dụng thuộc tính `required`, JS chặn nhập số âm trên trang Admin và Form Giá.
  - Backend: Sử dụng Logic kiểm tra trong các Controller (Ví dụ: `minPrice` được kẹp cứng để không bao giờ rớt xuống dưới 0 dù bị sửa URL).
- **Lưu trữ hình ảnh:** Ảnh upload từ Admin KHÔNG lưu dạng Blob vào DB. Thay vào đó, hàm MultipartFile chuyển file thẳng vào lưu vật lý ở thư mục tĩnh `uploads/` trên ổ cứng server, và Database chỉ lưu dưới dạng Text (String) là `/uploads/ten-hinh-anh.jpg`.
