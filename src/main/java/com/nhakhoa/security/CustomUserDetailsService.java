package com.nhakhoa.security;

import com.nhakhoa.model.User;
import com.nhakhoa.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
public class CustomUserDetailsService implements UserDetailsService {

    @Autowired
    private UserRepository userRepository;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        User user = userRepository.findByUsername(username);
        
        if (user == null) {
            throw new UsernameNotFoundException("Không tìm thấy tên đăng nhập: " + username);
        }

        // Định nghĩa các Role: ADMIN (1), DENTIST (2), PATIENT (3)
        String roleName;
        switch (user.getRoleID()) {
            case 1: roleName = "ADMIN"; break;
            case 2: roleName = "DENTIST"; break;
            default: roleName = "PATIENT"; break;
        }

        // Trả về đối tượng User của Spring Security
        return org.springframework.security.core.userdetails.User
                .withUsername(user.getUsername())
                .password(user.getPassword()) 
                .roles(roleName) // Spring tự thêm tiền tố ROLE_ -> ROLE_ADMIN, ROLE_DENTIST, ROLE_PATIENT
                .build();
    }
}