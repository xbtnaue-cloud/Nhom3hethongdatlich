package com.nhakhoa.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model; // 🌟 BẮT BUỘC: Thêm thư viện Model để truyền dữ liệu sang Thymeleaf
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class HomeController {

    // --- 1. ĐIỀU HƯỚNG TRANG CHỦ ---
    @GetMapping({"/", "/index"})
    public String index(Model model) { // 🛠️ Sửa: Nạp Model vào tham số
        // 🌟 CHỐT CHẶN: Gửi từ khóa 'home' để th:classappend trong header.html tô sáng "Trang chủ"
        model.addAttribute("activePage", "home"); 
        return "index"; // Điều hướng về templates/index.html
    }
    
    

    // --- 2. ĐIỀU HƯỚNG TRANG LIÊN HỆ ---
    @GetMapping("/contact")
    public String contact(Model model) { // 🛠️ Sửa: Nạp Model vào tham số
        // 🌟 CHỐT CHẶN: Gửi từ khóa 'contact' để th:classappend trong header.html tô sáng "Liên hệ"
        model.addAttribute("activePage", "contact"); 
        return "contact"; // Điều hướng về templates/contact.html
    }
}