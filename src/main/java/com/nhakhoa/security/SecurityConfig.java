package com.nhakhoa.security;

import com.nhakhoa.model.User;
import com.nhakhoa.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.crypto.password.NoOpPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Autowired
    private UserRepository userRepository;

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
            .csrf(csrf -> csrf.disable())
            .authorizeHttpRequests(auth -> auth
            	    // 1. Chỉ Admin
            	    .requestMatchers("/admin/**", "/manage-doctors/**", "/manage-services/**", "/manage-users/**").hasRole("ADMIN")
            	    
            	    // 2. Admin và Dentist
            	    .requestMatchers("/dentist/**", "/patients/**", "/admin-stats/**", "/admin-contacts/**").hasAnyRole("ADMIN", "DENTIST")
            	    
            	    // 3. MỞ KHÓA TẤT CẢ CÁC TRANG CỦA KHÁCH (Index, Booking, Dịch vụ, Bác sĩ, Liên hệ, v.v.)
            	    // Hãy thêm mọi đường dẫn trên header của bạn vào đây!
            	    .requestMatchers(
            	    	    "/login", 
            	    	    "/index", 
            	    	    "/", 
            	    	    "/booking", 
            	    	    "/getDentists", 
            	    	    "/service-list",   // Đã thêm: khớp với <a th:href="@{/service-list}">
            	    	    "/dentist-list",   // Đã thêm: khớp với <a th:href="@{/dentist-list}">
            	    	    "/contact",        // Đã thêm: khớp với <a th:href="@{/contact}">
            	    	    "/lookup-result", 
            	    	    "/lookup-appointment", 
            	    	    "/css/**", 
            	    	    "/js/**",
            	    	    "/assets/**",
            	    	    "/images/**",
            	    	    "/uploads/**"      // Đã thêm: Quan trọng để ảnh hiển thị được!
            	    	).permitAll()
            	    
            	    .requestMatchers(
            	    	    "/send-contact-ajax",   // API gửi tin nhắn
            	    	    "/get-latest-reply"     // API lấy lịch sử/phản hồi
            	    	).permitAll()
            	    // 4. Các đường dẫn còn lại cần đăng nhập
            	    .anyRequest().authenticated()
            	)
            .formLogin(form -> form
                .loginPage("/login")
                .successHandler((request, response, authentication) -> {
                    // 1. Lấy thông tin user từ database để lưu vào Session
                    String username = authentication.getName();
                    User user = userRepository.findByUsername(username); 
                    
                    if (user != null) {
                        request.getSession().setAttribute("acc", user);
                    }

                    // 2. Chuyển hướng dựa trên quyền
                    var authorities = authentication.getAuthorities();
                    if (authorities.stream().anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN"))) {
                        response.sendRedirect("/admin/dashboard");
                    } else if (authorities.stream().anyMatch(a -> a.getAuthority().equals("ROLE_DENTIST"))) {
                        response.sendRedirect("/dentist/dashboard");
                    } else {
                        response.sendRedirect("/index");
                    }
                })
                .permitAll()
            )
            .logout(logout -> logout
                .logoutSuccessUrl("/login")
                .invalidateHttpSession(true)
                .deleteCookies("JSESSIONID")
                .permitAll()
            );
        return http.build();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return NoOpPasswordEncoder.getInstance();
    }
}