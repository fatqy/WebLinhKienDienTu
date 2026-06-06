package com.example.DoAn.security;

import com.example.DoAn.model.Role;
import com.example.DoAn.model.User;
import com.example.DoAn.repository.RoleRepository;
import com.example.DoAn.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

@Service
public class CustomOAuth2UserService extends DefaultOAuth2UserService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private RoleRepository roleRepository;

    @Override
    public OAuth2User loadUser(OAuth2UserRequest userRequest) throws OAuth2AuthenticationException {
        OAuth2User oAuth2User = super.loadUser(userRequest);

        try {
            return processOAuth2User(userRequest, oAuth2User);
        } catch (Exception ex) {
            // Throwing an instance of AuthenticationException will trigger the OAuth2AuthenticationFailureHandler
            throw new OAuth2AuthenticationException(ex.getMessage());
        }
    }

    private OAuth2User processOAuth2User(OAuth2UserRequest userRequest, OAuth2User oAuth2User) {
        // Lấy thông tin từ Google
        String email = oAuth2User.getAttribute("email");
        String name = oAuth2User.getAttribute("name");
        
        // Google trả về 'sub' làm ID, Facebook trả về 'id'
        String providerId = oAuth2User.getAttribute("sub");
        if (providerId == null) {
            providerId = oAuth2User.getAttribute("id");
        }

        // Kiểm tra xem email đã tồn tại trong hệ thống chưa
        Optional<User> userOptional = userRepository.findByEmail(email);
        User user;

        if (userOptional.isPresent()) {
            user = userOptional.get();
            // Nếu người dùng đã đăng ký bằng Form (chưa có providerId), ta có thể cập nhật
            // Nhưng hiện tại ta cứ cho phép đăng nhập nếu trùng email
        } else {
            // Đăng ký người dùng mới
            user = new User();
            user.setEmail(email);
            // Dùng providerId (hoặc email) làm username do Spring Security cần
            user.setUsername(email); 
            user.setFullName(name);
            user.setPassword(""); // OAuth2 không cần mật khẩu
            user.setEnabled(true);

            // Gán quyền mặc định ROLE_USER
            Set<Role> roles = new HashSet<>();
            Role userRole = roleRepository.findByName("ROLE_USER")
                    .orElseGet(() -> roleRepository.save(new Role("ROLE_USER")));
            roles.add(userRole);
            user.setRoles(roles);

            userRepository.save(user);
        }

        // Trả về CustomOAuth2User để Spring Security quản lý
        return new CustomOAuth2User(oAuth2User, user);
    }
}
