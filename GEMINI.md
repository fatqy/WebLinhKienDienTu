# GEMINI.md - Hướng dẫn ngữ cảnh cho Dự án "DoAn"

## Tổng quan dự án
**DoAn** là một ứng dụng web Spring Boot hiện đại được thiết kế như một bản mẫu (prototype) cho nền tảng thương mại điện tử, mô phỏng giao diện và phong cách thẩm mỹ của [GearVN](https://gearvn.com). Dự án tập trung vào giao diện người dùng sạch sẽ, phản hồi nhanh (responsive) để duyệt các mặt hàng linh kiện máy tính.

### Công nghệ cốt lõi
*   **Java:** 26 (LTS)
*   **Spring Boot:** 4.0.5
*   **Template Engine:** Thymeleaf
*   **Công cụ xây dựng:** Maven (mvnw)
*   **Định dạng giao diện:** Vanilla CSS với Flexbox và CSS Grid
*   **Biểu tượng:** Font Awesome 6.0.0 (CDN)

### Kiến trúc hệ thống
Dự án tuân thủ mô hình kiến trúc **MVC (Model-View-Controller)** tiêu chuẩn:
*   **Models:** Nằm tại `src/main/java/com/example/DoAn/model/` (ví dụ: `Product.java`, `FilterGroup.java`).
*   **Controllers:** Nằm tại `src/main/java/com/example/DoAn/controller/` (ví dụ: `ProductController.java`).
*   **Templates:** Nằm tại `src/main/resources/templates/` (ví dụ: `linh-kien-may-tinh.html`).
*   **Tài nguyên tĩnh:** Nằm tại `src/main/resources/static/` (ví dụ: `css/style.css`).

## Xây dựng và Chạy ứng dụng
Sử dụng Maven Wrapper (`mvnw`) đi kèm cho tất cả các tác vụ phát triển:

*   **Chạy ứng dụng:** `./mvnw spring-boot:run`
*   **Biên dịch:** `./mvnw compile`
*   **Chạy kiểm thử:** `./mvnw test`
*   **Đóng gói:** `./mvnw package` (Tạo tệp JAR trong thư mục `target/`)

Ứng dụng mặc định chạy trên cổng `8080`. Bạn có thể truy cập trang danh mục sản phẩm tại:
`http://localhost:8080/linh-kien-may-tinh`

## Quy ước phát triển
*   **Ngôn ngữ giao tiếp:** Luôn luôn giao tiếp và phản hồi bằng **tiếng Việt**.
*   **Phong cách lập trình:** Tuân thủ các quy ước tiêu chuẩn của Java và Spring Boot.
*   **Dữ liệu mẫu:** Phiên bản hiện tại sử dụng dữ liệu giả (mock data) được tạo trực tiếp trong controller để phục vụ mục đích trình diễn và kiểm tra bố cục.
*   **Định dạng (Styling):** Ưu tiên sử dụng **Vanilla CSS** thay vì các framework tiện ích như Tailwind để có khả năng kiểm soát tối đa và hiệu năng nhẹ. Sử dụng biến CSS trong `:root` để đảm bảo tính nhất quán của thương hiệu.
*   **Hình ảnh:** Các thành phần động hiện đang sử dụng Picsum API (`https://picsum.photos/400/400?random=X`) để tạo hình ảnh đại diện ngẫu nhiên.
*   **Thiết kế phản hồi:** Ưu tiên thiết kế cho Desktop với container 1200px, sử dụng CSS Grid cho lưới sản phẩm.

## Lộ trình phát triển tương lai
- [ ] Triển khai cơ sở dữ liệu lưu trữ (ví dụ: H2 hoặc MySQL).
- [ ] Thêm các kho lưu trữ Spring Data JPA.
- [ ] Xây dựng trang chi tiết sản phẩm.
- [ ] Tích hợp hệ thống quản lý giỏ hàng.
- [ ] Thêm tính năng xác thực người dùng (Spring Security).
