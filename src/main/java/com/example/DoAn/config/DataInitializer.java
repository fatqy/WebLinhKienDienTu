package com.example.DoAn.config;

import com.example.DoAn.model.Category;
import com.example.DoAn.model.Product;
import com.example.DoAn.model.Role;
import com.example.DoAn.model.User;
import com.example.DoAn.repository.CategoryRepository;
import com.example.DoAn.repository.ProductRepository;
import com.example.DoAn.repository.RoleRepository;
import com.example.DoAn.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.util.Collections;
import java.util.HashSet;

@Component
public class DataInitializer implements CommandLineRunner {

    @Autowired
    private RoleRepository roleRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private CategoryRepository categoryRepository;

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Override
    public void run(String... args) throws Exception {
        // 1. Khởi tạo Role
        if (roleRepository.findByName("ROLE_ADMIN").isEmpty()) {
            roleRepository.save(new Role("ROLE_ADMIN"));
        }
        if (roleRepository.findByName("ROLE_USER").isEmpty()) {
            roleRepository.save(new Role("ROLE_USER"));
        }

        // 2. Khởi tạo Admin
        if (userRepository.findByUsername("admin").isEmpty()) {
            User admin = new User();
            admin.setUsername("admin");
            admin.setPassword(passwordEncoder.encode("admin123"));
            admin.setEmail("admin@gearvn.com");
            admin.setFullName("Quản trị viên");
            admin.setRoles(new HashSet<>(Collections.singletonList(roleRepository.findByName("ROLE_ADMIN").get())));
            userRepository.save(admin);
        }

        // 3. Khởi tạo Danh mục
        Category vga = categoryRepository.findByName("Card màn hình").orElseGet(() -> categoryRepository.save(new Category("Card màn hình")));
        Category cpu = categoryRepository.findByName("CPU").orElseGet(() -> categoryRepository.save(new Category("CPU")));
        Category main = categoryRepository.findByName("Mainboard").orElseGet(() -> categoryRepository.save(new Category("Mainboard")));
        Category ram = categoryRepository.findByName("RAM").orElseGet(() -> categoryRepository.save(new Category("RAM")));
        Category ssd = categoryRepository.findByName("Ổ cứng SSD").orElseGet(() -> categoryRepository.save(new Category("Ổ cứng SSD")));
        Category psu = categoryRepository.findByName("Nguồn máy tính").orElseGet(() -> categoryRepository.save(new Category("Nguồn máy tính")));
        Category cases = categoryRepository.findByName("Vỏ Case").orElseGet(() -> categoryRepository.save(new Category("Vỏ Case")));
        Category cooling = categoryRepository.findByName("Tản nhiệt").orElseGet(() -> categoryRepository.save(new Category("Tản nhiệt")));
        
        // Thêm danh mục mới theo yêu cầu
        Category pcGvn = categoryRepository.findByName("PC GVN").orElseGet(() -> categoryRepository.save(new Category("PC GVN")));
        Category laptop = categoryRepository.findByName("Laptop").orElseGet(() -> categoryRepository.save(new Category("Laptop")));
        Category gamingGear = categoryRepository.findByName("Góc Gaming").orElseGet(() -> categoryRepository.save(new Category("Góc Gaming")));

        // 4. Khởi tạo Sản phẩm (Thêm ít nhất 30 sản phẩm tổng cộng)
        if (productRepository.count() < 30) {
            productRepository.deleteAll(); 

            // --- PC GVN ---
            saveP("PC GVN Phantom Plus i4070Ti", 45990000, 42990000, 5, "50", "PC Bán Chạy", pcGvn);
            saveP("PC GVN G-STORM i4060", 22500000, 19900000, 10, "51", "Giá Hời", pcGvn);

            // --- LAPTOP ---
            saveP("Laptop Gaming ASUS ROG Strix G16", 38990000, 35500000, 7, "52", "New 2024", laptop);
            saveP("Laptop MSI Katana 15 B13V", 28500000, 26900000, 12, "53", "Hot", laptop);
            saveP("Laptop Acer Predator Helios Neo 16", 35000000, 32900000, 5, "54", "", laptop);

            // --- GÓC GAMING ---
            saveP("Ghế Gaming Corsair T3 Rush", 6500000, 5900000, 15, "55", "Premium", gamingGear);
            saveP("Bàn chữ Z Gaming Pro", 2500000, 1950000, 20, "56", "", gamingGear);
            saveP("Đèn LED RGB dán bàn Smart Wi-Fi", 850000, 650000, 100, "57", "Decor", gamingGear);

            // --- VGA ---
            saveP("ASUS ROG Strix GeForce RTX 4090 OC 24GB", 65990000, 59990000, 5, "11", "Siêu phẩm", vga);
            saveP("MSI GeForce RTX 4080 SUPER EXPERT 16G", 32990000, 30990000, 8, "12", "New", vga);
            saveP("GIGABYTE RTX 4070 Ti SUPER GAMING OC", 25990000, 23500000, 12, "13", "Hot Deal", vga);
            saveP("ASUS Dual GeForce RTX 4060 Ti 8GB", 12490000, 11290000, 20, "10", "", vga);

            // --- CPU ---
            saveP("Intel Core i9-14900K / 6.0GHz / 24 Nhân", 16490000, 15890000, 10, "14", "Hot", cpu);
            saveP("AMD Ryzen 7 7800X3D / 5.0GHz / 8 Nhân", 11500000, 10200000, 15, "15", "Gaming", cpu);
            saveP("Intel Core i7-14700K / 5.6GHz / 20 Nhân", 11500000, 10890000, 20, "16", "", cpu);
            saveP("Intel Core i5-13400F / 4.6GHz / 10 Nhân", 5490000, 4990000, 30, "4", "Bán chạy", cpu);

            // --- MAINBOARD ---
            saveP("ASUS ROG MAXIMUS Z790 HERO", 18990000, 17500000, 4, "17", "High-end", main);
            saveP("MSI MAG B760M MORTAR WIFI II", 4990000, 4590000, 25, "18", "Bán chạy", main);
            saveP("GIGABYTE Z790 AORUS ELITE AX", 8500000, 7900000, 10, "25", "", main);

            // --- RAM ---
            saveP("Corsair Vengeance RGB 32GB (2x16GB) 6000MHz", 3590000, 3290000, 30, "19", "RGB", ram);
            saveP("G.Skill Trident Z5 RGB 32GB 6400MHz", 4200000, 3850000, 15, "20", "Top", ram);
            saveP("Kingston FURY Beast 16GB 5600MHz", 1850000, 1650000, 50, "26", "", ram);

            // --- SSD ---
            saveP("Samsung 990 Pro 2TB M.2 NVMe PCIe 5.0", 5990000, 5490000, 10, "21", "Tốc độ cao", ssd);
            saveP("WD Black SN850X 1TB M.2 NVMe", 2850000, 2490000, 40, "22", "", ssd);
            saveP("Crucial T700 1TB PCIe Gen5", 4500000, 4100000, 5, "27", "New Gen", ssd);

            // --- PSU ---
            saveP("Nguồn Corsair RM1000e - 80 Plus Gold", 4290000, 3950000, 15, "23", "", psu);
            saveP("ASUS ROG Thor 1200W Platinum II", 9500000, 8900000, 3, "28", "OLED", psu);

            // --- COOLING ---
            saveP("ASUS ROG RYUJIN III 360 ARGB", 10500000, 9800000, 5, "24", "LCD", cooling);
            saveP("Deepcool AK620 Digital", 1850000, 1650000, 20, "29", "Digital", cooling);

            // --- CASE ---
            saveP("Lian Li O11 Dynamic EVO RGB - White", 5500000, 4990000, 8, "30", "Luxury", cases);
            saveP("NZXT H9 Flow White", 4500000, 4200000, 10, "31", "Best Airflow", cases);
        }
    }

    private void saveP(String name, double op, double sp, int st, String imgId, String badge, Category cat) {
        Product p = new Product();
        p.setName(name);
        p.setOriginalPrice(op);
        p.setSalePrice(sp);
        p.setStock(st);
        p.setImageUrl("https://picsum.photos/400/400?random=" + imgId);
        p.setBadge(badge);
        p.setStatus("Còn hàng");
        p.setCategory(cat);
        productRepository.save(p);
    }
}
