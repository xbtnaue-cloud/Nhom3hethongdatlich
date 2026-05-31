package com.nhakhoa.config;

import com.nhakhoa.filter.AdminFilter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration // Đánh dấu đây là file cấu hình hệ thống Spring Boot
public class WebConfig implements WebMvcConfigurer {

    @Autowired
    private AdminFilter adminInterceptor; // Tiêm em Interceptor vừa sửa ở Bước 1 vào đây

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        // Khai báo chính xác các URL muốn chốt chặn bảo mật (Khớp 100% danh sách cũ)
        registry.addInterceptor(adminInterceptor)
                .addPathPatterns(
                        "/admin-dashboard",
                        "/admin-stats",
                        "/patients",
                        "/dentist-dashboard",
                        "/update-status"
                );
    }
}