package com.example.DoAn.security;

import com.example.DoAn.model.User;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.core.user.OAuth2User;

import java.util.Collection;
import java.util.Map;
import java.util.stream.Collectors;

public class CustomOAuth2User implements OAuth2User {

    private OAuth2User oAuth2User;
    private User user; // Đối tượng User trong Database của chúng ta

    public CustomOAuth2User(OAuth2User oAuth2User, User user) {
        this.oAuth2User = oAuth2User;
        this.user = user;
    }

    @Override
    public Map<String, Object> getAttributes() {
        return oAuth2User.getAttributes();
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        // Trả về quyền từ Database của chúng ta thay vì mặc định của Google
        return user.getRoles().stream()
                .map(role -> new SimpleGrantedAuthority(role.getName()))
                .collect(Collectors.toList());
    }

    @Override
    public String getName() {
        // Spring Security dùng Name này làm tên định danh (Principal)
        return user.getUsername(); 
    }

    public String getEmail() {
        return oAuth2User.getAttribute("email");
    }

    public String getFullName() {
        return user.getFullName();
    }
}
