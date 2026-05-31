package com.nhakhoa.controller;

import com.nhakhoa.dao.UserDAO;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller // Đổi sang Annotation của Spring
public class RegisterController {

    // 1. GET: Hiển thị giao diện trang Đăng ký lần đầu
    @GetMapping("/register")
    public String showRegisterForm() {
        return "register"; // Mở file register.html trong thư mục templates
    }

    // 2. POST: Xử lý dữ liệu khi khách hàng nhấn nút "Đăng ký"
    @PostMapping("/register")
    public String handleRegister(
            @RequestParam("fullName") String fullName,
            @RequestParam("username") String username,
            @RequestParam("phone") String phone,
            @RequestParam("email") String email,
            @RequestParam("password") String password,
            @RequestParam("repassword") String repassword,
            Model model) {

        UserDAO dao = new UserDAO();

        try {
            // Kiểm tra logic cơ bản: Xem mật khẩu xác nhận có trùng khớp không
            if (!password.equals(repassword)) {
                model.addAttribute("error", "Mật khẩu xác nhận không khớp!");
                return "register"; // Ở lại trang đăng ký và thông báo lỗi
            }

            // Kiểm tra xem tài khoản (username) đã tồn tại trong hệ thống chưa
            if (dao.checkUserExist(username)) {
                model.addAttribute("error", "Tên đăng nhập đã tồn tại!");
                return "register";
            } else {
                // Thực hiện đăng ký (Mặc định giữ nguyên RoleID = 3 cho khách hàng/bệnh nhân)
                boolean success = dao.register(username, password, fullName, email, phone, 3);
                
                if (success) {
                    // Đăng ký thành công -> Chuyển hướng thẳng về trang đăng nhập (URL /login chuẩn)
                    return "redirect:/login";
                } else {
                    model.addAttribute("error", "Có lỗi xảy ra, vui lòng thử lại!");
                    return "register";
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            model.addAttribute("error", "Lỗi hệ thống: " + e.getMessage());
            return "register";
        }
    }
}