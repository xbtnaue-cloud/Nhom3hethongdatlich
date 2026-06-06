package com.nhakhoa.controller;

import com.nhakhoa.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
public class ForgotPasswordController {

    @Autowired
    private UserService userService;

    @RequestMapping(value = "/forgot-password", method = {RequestMethod.GET, RequestMethod.POST})
    public String handleForgotPassword(
            @RequestParam(value = "step", required = false) String step,
            @RequestParam(value = "email", required = false) String email,
            @RequestParam(value = "newPassword", required = false) String newPass,
            @RequestParam(value = "confirmPassword", required = false) String confirmPass,
            Model model) {

        try {
            // ── BƯỚC 1: Kiểm tra Email tồn tại ─────────────────
            if ("1".equals(step)) {
                if (email == null || email.isBlank()) {
                    model.addAttribute("error", "Vui lòng nhập Email!");
                } else if (userService.checkEmailExist(email)) {
                    model.addAttribute("isEmailValid", true);
                    model.addAttribute("email", email);
                    model.addAttribute("mess", "Gmail xác thực thành công! Vui lòng đặt mật khẩu mới.");
                } else {
                    model.addAttribute("error", "Email này không tồn tại!");
                }
                return "forgot-password";
            } 
            // ── BƯỚC 2: Nhận mật khẩu mới và cập nhật ──────────
            else if ("2".equals(step)) {
                if (newPass != null && newPass.equals(confirmPass)) {
                    if (userService.updatePassword(email, newPass)) {
                        model.addAttribute("mess", "Đặt lại mật khẩu thành công! Vui lòng đăng nhập.");
                        return "login";
                    } else {
                        model.addAttribute("error", "Có lỗi xảy ra!");
                        model.addAttribute("isEmailValid", true);
                        model.addAttribute("email", email);
                    }
                } else {
                    model.addAttribute("error", "Mật khẩu xác nhận không trùng khớp!");
                    model.addAttribute("isEmailValid", true);
                    model.addAttribute("email", email);
                }
                return "forgot-password";
            }
            
            return "forgot-password";

        } catch (Exception e) {
            e.printStackTrace();
            model.addAttribute("error", "Lỗi hệ thống: " + e.getMessage());
            return "error";
        }
    }
}