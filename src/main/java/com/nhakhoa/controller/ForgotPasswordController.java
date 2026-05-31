package com.nhakhoa.controller;

import com.nhakhoa.dao.UserDAO;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
@Controller // Đổi sang Annotation của Spring
public class ForgotPasswordController {

    // Gom cả GET và POST chung một hàm xử lý rẽ nhánh theo step như processRequest cũ của ní
    @RequestMapping(value = "/forgot-password", method = {RequestMethod.GET, RequestMethod.POST})
    public String handleForgotPassword(
            @RequestParam(value = "step", required = false) String step,
            @RequestParam(value = "email", required = false) String email,
            @RequestParam(value = "newPassword", required = false) String newPass,
            @RequestParam(value = "confirmPassword", required = false) String confirmPass,
            Model model) {

        UserDAO dao = new UserDAO();

        try {
            // ── BƯỚC 1: Kiểm tra Email tồn tại trong CSDL ─────────────────
            if ("1".equals(step)) {
                if (email == null || email.isBlank()) {
                    model.addAttribute("error", "Vui lòng nhập Email!");
                } else if (dao.checkEmailExist(email)) {
                    // Nếu đúng Email, gửi cờ xác nhận về để Thymeleaf hiển thị form nhập Pass mới
                    model.addAttribute("isEmailValid", true);
                    model.addAttribute("email", email);
                    model.addAttribute("mess", "Gmail xác thực thành công! Vui lòng đặt mật khẩu mới.");
                } else {
                    model.addAttribute("error", "Email này không tồn tại trong hệ thống!");
                }
                return "forgot-password"; // Trả về file forgot-password.html trong templates

            } 
            // ── BƯỚC 2: Nhận mật khẩu mới và thực hiện cập nhật ──────────
            else if ("2".equals(step)) {
                if (newPass != null && newPass.equals(confirmPass)) {
                    // Gọi hàm cập nhật mật khẩu từ DAO cũ của ní
                    boolean isUpdated = dao.updatePassword(email, newPass);
                    
                    if (isUpdated) {
                        model.addAttribute("mess", "Đặt lại mật khẩu thành công! Vui lòng đăng nhập.");
                        return "login"; // Thành công đá thẳng sang trang login.html
                    } else {
                        model.addAttribute("error", "Có lỗi xảy ra trong quá trình cập nhật!");
                        model.addAttribute("isEmailValid", true);
                        model.addAttribute("email", email);
                        return "forgot-password";
                    }
                } else {
                    // Mật khẩu xác nhận bị lệch
                    model.addAttribute("error", "Mật khẩu xác nhận không trùng khớp!");
                    model.addAttribute("isEmailValid", true); // Giữ lại form nhập pass mới
                    model.addAttribute("email", email);
                    return "forgot-password";
                }
            } 
            
            // ── MẶC ĐỊNH: Nếu truy cập trực tiếp không truyền step, hiện Bước 1 ──
            else {
                return "forgot-password";
            }

        } catch (Exception e) {
            e.printStackTrace();
            model.addAttribute("error", "Lỗi hệ thống: " + e.getMessage());
            return "error"; // Mở file error.html trong templates
        }
    }
}