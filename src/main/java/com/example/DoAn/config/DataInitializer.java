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
        if (productRepository.count() < 10) {
            // --- PC GVN ---
            saveP("PC GVN Phantom Plus i4070Ti", 45990000, 42990000, 5, "gaming,pc,desktop", "PC Bán Chạy", pcGvn, "GEARVN");
            saveP("PC GVN G-STORM i4060", 22500000, 19900000, 10, "computer,case,rgb", "Giá Hời", pcGvn, "GEARVN");

            // --- LAPTOP ---
            saveP("Laptop Gaming ASUS ROG Strix G16", 38990000, 35500000, 7, "gaming,laptop,rog", "New 2024", laptop, "ASUS");
            saveP("Laptop MSI Katana 15 B13V", 28500000, 26900000, 12, "laptop,msi,gaming", "Hot", laptop, "MSI");
            saveP("Laptop Acer Predator Helios Neo 16", 35000000, 32900000, 5, "laptop,acer,predator", "", laptop, "Acer");

            // --- GÓC GAMING ---
            saveP("Ghế Gaming Corsair T3 Rush", 6500000, 5900000, 15, "gaming,chair", "Premium", gamingGear, "Corsair");
            saveP("Bàn chữ Z Gaming Pro", 2500000, 1950000, 20, "gaming,desk", "", gamingGear, "GEARVN");
            saveP("Đèn LED RGB dán bàn Smart Wi-Fi", 850000, 650000, 100, "rgb,led,strip", "Decor", gamingGear, "Smart");

            // --- VGA ---
            saveP("ASUS ROG Strix GeForce RTX 4090 OC 24GB", 65990000, 59990000, 5, "nvidia,rtx,4090,vga", "Siêu phẩm", vga, "ASUS");
            saveP("MSI GeForce RTX 4080 SUPER EXPERT 16G", 32990000, 30990000, 8, "msi,rtx,gpu", "New", vga, "MSI");
            saveP("GIGABYTE RTX 4070 Ti SUPER GAMING OC", 25990000, 23500000, 12, "gigabyte,rtx,vga", "Hot Deal", vga, "GIGABYTE");
            saveP("ASUS Dual GeForce RTX 4060 Ti 8GB", 12490000, 11290000, 20, "asus,dual,rtx", "", vga, "ASUS");

            // --- CPU ---
            saveP("Intel Core i9-14900K / 6.0GHz / 24 Nhân", 16490000, 15890000, 10, "intel,processor,i9", "Hot", cpu, "Intel");
            saveP("AMD Ryzen 7 7800X3D / 5.0GHz / 8 Nhân", 11500000, 10200000, 15, "amd,ryzen,cpu", "Gaming", cpu, "AMD");
            saveP("Intel Core i7-14700K / 5.6GHz / 20 Nhân", 11500000, 10890000, 20, "intel,i7,cpu", "", cpu, "Intel");
            saveP("Intel Core i5-13400F / 4.6GHz / 10 Nhân", 5490000, 4990000, 30, "intel,i5,cpu", "Bán chạy", cpu, "Intel");

            // --- MAINBOARD ---
            saveP("ASUS ROG MAXIMUS Z790 HERO", 18990000, 17500000, 4, "asus,z790,motherboard", "High-end", main, "ASUS");
            saveP("MSI MAG B760M MORTAR WIFI II", 4990000, 4590000, 25, "msi,b760,mainboard", "Bán chạy", main, "MSI");
            saveP("GIGABYTE Z790 AORUS ELITE AX", 8500000, 7900000, 10, "gigabyte,aorus,motherboard", "", main, "GIGABYTE");

            // --- RAM ---
            saveP("Corsair Vengeance RGB 32GB (2x16GB) 6000MHz", 3590000, 3290000, 30, "corsair,vengeance,ram", "RGB", ram, "Corsair");
            saveP("G.Skill Trident Z5 RGB 32GB 6400MHz", 4200000, 3850000, 15, "gskill,trident,ram", "Top", ram, "G.Skill");
            saveP("Kingston FURY Beast 16GB 5600MHz", 1850000, 1650000, 50, "kingston,fury,ram", "", ram, "Kingston");

            // --- SSD ---
            saveP("Samsung 990 Pro 2TB M.2 NVMe PCIe 5.0", 5990000, 5490000, 10, "samsung,990,ssd", "Tốc độ cao", ssd, "Samsung");
            saveP("WD Black SN850X 1TB M.2 NVMe", 2850000, 2490000, 40, "wd,black,ssd", "", ssd, "Western Digital");
            saveP("Crucial T700 1TB PCIe Gen5", 4500000, 4100000, 5, "crucial,nvme,ssd", "New Gen", ssd, "Crucial");

            // --- PSU ---
            saveP("Nguồn Corsair RM1000e - 80 Plus Gold", 4290000, 3950000, 15, "corsair,psu,power", "", psu, "Corsair");
            saveP("ASUS ROG Thor 1200W Platinum II", 9500000, 8900000, 3, "asus,thor,psu", "OLED", psu, "ASUS");

            // --- COOLING ---
            saveP("ASUS ROG RYUJIN III 360 ARGB", 10500000, 9800000, 5, "asus,ryujin,cooler", "LCD", cooling, "ASUS");
            saveP("Deepcool AK620 Digital", 1850000, 1650000, 20, "deepcool,air,cooler", "Digital", cooling, "Deepcool");

            // --- CASE ---
            saveP("Lian Li O11 Dynamic EVO RGB - White", 5500000, 4990000, 8, "lianli,case,pc", "Luxury", cases, "Lian Li");
            saveP("NZXT H9 Flow White", 4500000, 4200000, 10, "nzxt,h9,case", "Best Airflow", cases, "NZXT");
        }
    }

    private void saveP(String name, double op, double sp, int st, String keywords, String badge, Category cat, String brand) {
        Product p = new Product();
        p.setName(name);
        p.setOriginalPrice(op);
        p.setSalePrice(sp);
        p.setStock(st);
        // Kết hợp từ khóa tech và sản phẩm cụ thể để có ảnh sát nhất
        p.setImageUrl("https://loremflickr.com/400/400/technology," + keywords + "/all?lock=" + Math.abs(name.hashCode() % 1000));
        p.setBadge(badge);
        p.setStatus("Còn hàng");
        p.setCategory(cat);
        p.setBrand(brand);
        productRepository.save(p);
    }
}
