package com.example.DoAn.config;

import java.util.HashMap;
import java.util.Map;

/**
 * File quản lý tập trung toàn bộ hình ảnh.
 * Bạn có thể tìm đúng TÊN SẢN PHẨM bên dưới để thay đổi link URL.
 */
public class ImageAssets {

    // --- Banner & Logo (Sử dụng Getter để Thymeleaf truy cập dễ dàng) ---
    private final String logo = "https://gearvn.com/cdn/shop/files/logo-gearvn.png?v=1614324700";
    private final String bannerHome = "blob:https://gemini.google.com/f4a672ad-bc0c-40c2-9712-566848e49a18";
    
    public String getLogo() { return logo; }
    public String getBannerHome() { return bannerHome; }

    // --- Map lưu trữ ảnh theo Tên Sản Phẩm ---
    private static final Map<String, String> PRODUCT_IMAGES = new HashMap<>();

    static {
        // --- PC GVN ---
        PRODUCT_IMAGES.put("PC GVN Phantom Plus i4070Ti", "https://i.pinimg.com/736x/80/8c/2b/808c2b2d0afabbc2200d97336250d585.jpg");
        PRODUCT_IMAGES.put("PC GVN G-STORM i4060", "https://product.hstatic.net/200000722513/product/6969_2d5fecbdba0b41ada9493842ba1699b3_master.png");

        // --- LAPTOP ---
        PRODUCT_IMAGES.put("Laptop Gaming ASUS ROG Strix G16", "https://encrypted-tbn1.gstatic.com/shopping?q=tbn:ANd9GcTXuUtTMRm2wifVO-Qx2KGTklro4PRyAFtgzZ40mgsZkVecGDpQK4PbBnsu5ndrkEP7sLm6aTOwe_vY8VyS4pjctBgmG6kk83feTPx1IyVv1SvJalqj5HqV8sDNJqMZzEzYAzUq0HQgIw&usqp=CAc");
        PRODUCT_IMAGES.put("Laptop MSI Katana 15 B13V", "https://i.pinimg.com/736x/1d/82/bf/1d82bf6a34cbbc8c9f7eedd73771245c.jpg");
        PRODUCT_IMAGES.put("Laptop Acer Predator Helios Neo 16", "https://encrypted-tbn3.gstatic.com/shopping?q=tbn:ANd9GcRT9JoUhTwBwNFSQqbX3DcdzNPpBEWSfsg0Pxl7RVJr2RdNTDJ4vKo_Sq7bzgiaFFwuAxohYKg86Qfec_vqpYMT_gTRw5UHQ4JNoimWXqV4ca2Kapxo7-pDWyYeZAnBMHvbIE5KdCU7Kw&usqp=CAc");
        PRODUCT_IMAGES.put("Màn hình ASUS ROG Swift PG279QM", "https://dlcdnwebimgs.asus.com/gain/72C16A36-4EE3-4AC4-A58A-35F6B8A2FB6F/w717/h525/fwebp");
        PRODUCT_IMAGES.put("Màn hình Samsung Odyssey G7", "https://cdn2.cellphones.com.vn/insecure/rs:fill:0:358/q:90/plain/https://cellphones.com.vn/media/catalog/product/m/h/mhv_1_14.png");
        PRODUCT_IMAGES.put("Màn hình LG UltraGear 27GP850-B","https://cdn2.cellphones.com.vn/insecure/rs:fill:0:358/q:90/plain/https://cellphones.com.vn/media/catalog/product/t/e/text_ng_n_5__10_61.png");
        // --- VGA ---
        PRODUCT_IMAGES.put("ASUS ROG Strix GeForce RTX 4090 OC 24GB", "https://encrypted-tbn2.gstatic.com/shopping?q=tbn:ANd9GcRXuYmnFkWcDdedeee9rWDMMvvGY40b9kS9d_UCiW1WEnByYaXUWXSGXAducFeX6Yd8BoDOSBwM8on8rvLZ0LFT8LB-2yX2woBCi8QukbaiL5mArx4O0bgeBJBEUq8lfodOw12eEMk8gw&usqp=CAc");
        PRODUCT_IMAGES.put("MSI GeForce RTX 4080 SUPER EXPERT 16G", "https://i.pinimg.com/1200x/c1/b3/8e/c1b38e9e5891c6651696b9d01b0855a1.jpg");
        PRODUCT_IMAGES.put("GIGABYTE RTX 4070 Ti SUPER GAMING OC", "https://encrypted-tbn3.gstatic.com/shopping?q=tbn:ANd9GcTsJA9KCoi0N8VqXkz-rQzYbxT6oDbiYnhi6bMbjUCW3nLW1X5wlZOKS-vxRgna44caVq4FTd5HKmPIDoEdc9c6W-dpwEOhzAK9s9xNQI7skMXpbeijmbCVxRP7Ei5sfMuGN3oeAl5_&usqp=CAc");
        PRODUCT_IMAGES.put("ASUS Dual GeForce RTX 4060 Ti 8GB", "https://encrypted-tbn3.gstatic.com/shopping?q=tbn:ANd9GcQu-_6KfYrE6QJcTE4uK5b_1siW17zIT3u15qnCijGvahbu7-kHVYKOV3AagmDLk1_GitklJy9aKvvqp7SXlWRI7yVdHo7aQ71RcezllxpfDsSBGWlFEZ9ZTj4iGGxOoWABx5aSVr4&usqp=CAc");

        // --- CPU ---
        PRODUCT_IMAGES.put("Intel Core i9-14900K / 6.0GHz / 24 Nhân", "https://encrypted-tbn2.gstatic.com/shopping?q=tbn:ANd9GcQyuEJj0hdrNyy_61PtntXihnZtU_00DqlWBfyzNpEwEwptc6fk3vFHapsjs2ggR-rz7e7vHtqWpb7biHFsUV3bg9RY8bmyoiqbIBAjiPiDymMw0MiW9dzPsw33fM9RPBTlRq5-mi_XmA&usqp=CAc");
        PRODUCT_IMAGES.put("AMD Ryzen 7 7800X3D / 5.0GHz / 8 Nhân", "https://i.pinimg.com/1200x/1a/37/3a/1a373a79e28c9c76185293dcdc429b34.jpg");
        PRODUCT_IMAGES.put("Intel Core i7-14700K / 5.6GHz / 20 Nhân", "https://encrypted-tbn0.gstatic.com/shopping?q=tbn:ANd9GcShcqU57TJAxgJ0EGxrCQIurVfrvRM_e213ffzM662TbJmEawqrAk0VFypm2wHazlS8AjWXi7OyOtZG1OX5gEms_fXn7CrdpZYuJXKU1lgdSYAU7CLQtG7Q8JCjppakVUmnx7qTpi0&usqp=CAc");
        PRODUCT_IMAGES.put("Intel Core i5-13400F / 4.6GHz / 10 Nhân", "https://encrypted-tbn2.gstatic.com/shopping?q=tbn:ANd9GcQGVcz8j7znxy3tIAJZfvdKSijqYnE3mxQuRHiHHPpd6T7MCOx58XDqGMaxC_ksGSD_lCpqUstJgrnp9tk0gRjYLEMR9nieeDNn4kfV0Qw4gL3x2llEtcxkPXlIcFl_Xdim87gjWCs&usqp=CAc");

        // --- MAINBOARD ---
        PRODUCT_IMAGES.put("ASUS ROG MAXIMUS Z790 HERO", "https://i.pinimg.com/736x/72/7b/87/727b87d6cf82c5f30e5221201d47e931.jpg");
        PRODUCT_IMAGES.put("MSI MAG B760M MORTAR WIFI II", "https://encrypted-tbn1.gstatic.com/shopping?q=tbn:ANd9GcStimlremaplPcnpgNm3UGdd7c0gNFk4H7mWTKe4wu57oQrw6WOtI6q604Nz9GcGAxLtpgaeSxYL2Lqv8Lc3WKRBnvz3vAuTwwOAXMxGTc8qCmyVeMDRYpG1U4kNJnKtYJTIlYlfw&usqp=CAc");
        PRODUCT_IMAGES.put("GIGABYTE Z790 AORUS ELITE AX", "https://i.pinimg.com/1200x/45/7a/97/457a978e58bdf01e2fd1b37d89dffdd1.jpg");

        // --- RAM ---
        PRODUCT_IMAGES.put("Corsair Vengeance RGB 32GB (2x16GB) 6000MHz", "https://encrypted-tbn1.gstatic.com/shopping?q=tbn:ANd9GcTSxcPGlJym0rwB_KGYrjKqkX2GvYfc2AMjglVReuvZ7FVQHOn_R5Ri4wdq2-REBo9aw10kuLSy-1nsomlOR484ZD8XVrs7S3FMUsQKlGeiGo8PRZCDIiWr");
        PRODUCT_IMAGES.put("G.Skill Trident Z5 RGB 32GB 6400MHz", "https://i.pinimg.com/736x/87/0d/cb/870dcbd305117bc13e01940be13d4a0b.jpg");
        PRODUCT_IMAGES.put("Kingston FURY Beast 16GB 5600MHz", "https://i.pinimg.com/736x/d3/08/36/d308366a89ddbdde2bd836b8fad0887e.jpg");

        // --- SSD ---
        PRODUCT_IMAGES.put("Samsung 990 Pro 2TB M.2 NVMe PCIe 5.0", "https://i.pinimg.com/1200x/5b/44/f5/5b44f57aa50be31ff0360514ca0b1bcf.jpg");
        PRODUCT_IMAGES.put("WD Black SN850X 1TB M.2 NVMe", "https://i.pinimg.com/1200x/1f/01/f1/1f01f18372112b8fda4ce7dbae04d0b1.jpg");
        PRODUCT_IMAGES.put("Crucial T700 1TB PCIe Gen5", "https://i.pinimg.com/1200x/7b/99/c2/7b99c24df890ab5b2bd12738a912fdaa.jpg");

        // --- PSU ---
        PRODUCT_IMAGES.put("Nguồn Corsair RM1000e - 80 Plus Gold", "https://i.pinimg.com/736x/ba/a7/bb/baa7bbbbfdf8db4d686d8af60f5522e2.jpg");
        PRODUCT_IMAGES.put("ASUS ROG Thor 1200W Platinum II", "https://i.pinimg.com/1200x/4e/a6/a6/4ea6a65c9602a3cf1152455b640ee8ab.jpg");

        // --- COOLING ---
        PRODUCT_IMAGES.put("ASUS ROG RYUJIN III 360 ARGB", "https://i.pinimg.com/736x/c7/e1/ae/c7e1ae76e3bd98c6f02c6a613c8889ae.jpg");
        PRODUCT_IMAGES.put("Deepcool AK620 Digital", "https://i.pinimg.com/736x/31/47/78/314778f3e9d4efe4356cbe6df77c05e6.jpg");

        // --- CASE ---
        PRODUCT_IMAGES.put("Lian Li O11 Dynamic EVO RGB - White", "https://i.pinimg.com/736x/0e/08/97/0e0897d3d8b2e4ae0e223954ea18c656.jpg");
        PRODUCT_IMAGES.put("NZXT H9 Flow White", "https://i.pinimg.com/1200x/94/a6/c4/94a6c4a02e32d6f9400d042f7cf65871.jpg");

        // --- GÓC GAMING ---
        PRODUCT_IMAGES.put("Ghế Gaming Corsair T3 Rush", "https://i.pinimg.com/1200x/3a/a6/97/3aa69747f68463b54db821e3cc2ad7e9.jpg");
        PRODUCT_IMAGES.put("Bàn chữ Z Gaming Pro", "https://i.pinimg.com/1200x/a5/ff/90/a5ff90d622864bfc7e33b756ed7792ad.jpg");
        PRODUCT_IMAGES.put("Đèn LED RGB dán bàn Smart Wi-Fi", "https://i.pinimg.com/1200x/3f/ed/11/3fed110bf0156c52b355c419c852c3cc.jpg");
    }

    /**
     * Ưu tiên lấy ảnh theo Tên Sản Phẩm. 
     * Nếu không có, sẽ lấy theo từ khóa mặc định.
     */
    public static String getProductImage(String productName, String keywords) {
        if (PRODUCT_IMAGES.containsKey(productName)) {
            return PRODUCT_IMAGES.get(productName);
        }
        // Fallback nếu không tìm thấy tên cụ thể
        return "https://loremflickr.com/400/400/technology," + keywords + "/all";
    }
}
