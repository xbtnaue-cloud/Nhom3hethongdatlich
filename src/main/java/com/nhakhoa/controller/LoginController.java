package com.nhakhoa.controller;

import com.nhakhoa.dao.UserDAO;
import com.nhakhoa.model.User;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import jakarta.servlet.http.HttpSession;

@Controller // Đổi sang Annotation của Spring
public class LoginController {

    // 1. GET: Hiển thị trang đăng nhập lần đầu
    @GetMapping("/login")
    public String showLoginForm() {
        return "login"; // Mở file login.html trong thư mục templates
    }

    // 2. POST: Xử lý dữ liệu khi người dùng bấm nút "Đăng nhập"
    @PostMapping("/login")
    public String handleLogin(
            @RequestParam("username") String user,
            @RequestParam("password") String pass,
            HttpSession session, // Nhận session để lưu thông tin đăng nhập giống Servlet cũ
            Model model) {

        // Kiểm tra đăng nhập qua UserDAO cũ của ní
        UserDAO dao = new UserDAO();
        User account = dao.login(user, pass);

        if (account != null) {
            
            // --- BƯỚC 3: KIỂM TRA TRẠNG THÁI KHÓA TÀI KHOẢN ---
            if (account.getStatusID() == 0) {
                model.addAttribute("error", "Tài khoản của bạn hiện đang bị khóa. Vui lòng liên hệ Quản trị viên!");
                return "login"; // Ở lại trang đăng nhập và báo lỗi
            }

            // 4. Đăng nhập thành công và không bị khóa -> Tạo Session
            session.setAttribute("acc", account);

            // Log kiểm tra luồng chạy trên Console
            System.out.println("Đăng nhập thành công: " + account.getFullName() + " [Role: " + account.getRoleID() + "]");

            // 5. Điều hướng đúng theo vai trò (Role)
            switch (account.getRoleID()) {
                case 1: // Quản trị viên
                    return "redirect:/admin-dashboard";

                case 2: // Bác sĩ / Nha sĩ
                    return "redirect:/dentist-dashboard";

                case 3: // Bệnh nhân / Khách hàng (Đá thẳng về trang chủ hệ thống)
                    return "redirect:/index"; 

                default: // Trường hợp vai trò không hợp lệ
                    return "redirect:/login";
            }

        } else {
            // 6. Sai tài khoản hoặc mật khẩu
            model.addAttribute("error", "Tên đăng nhập hoặc mật khẩu không chính xác!");
            return "login"; // Forward về lại trang login
        }
    }
}