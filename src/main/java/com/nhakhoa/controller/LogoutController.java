package com.nhakhoa.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import jakarta.servlet.http.HttpSession;

@Controller // Đổi sang Annotation của Spring
public class LogoutController {

    // Thay thế hoàn toàn cho doGet và urlPatterns = {"/logout"}
    @GetMapping("/logout")
    public String logout(HttpSession session) {
        
        // Hủy toàn bộ dữ liệu phiên làm việc (session) nếu nó đang tồn tại
        if (session != null) {
            session.invalidate();
        }

        // Đuổi người dùng về lại trang đăng nhập URL chuẩn Spring Boot
        return "redirect:/login"; 
    }
}