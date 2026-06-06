package com.nhakhoa.controller;

import com.nhakhoa.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
public class RegisterController {

    @Autowired
    private UserService userService;

    @GetMapping("/register")
    public String showRegisterForm() {
        return "register";
    }

    @PostMapping("/register")
    public String handleRegister(
            @RequestParam("fullName") String fullName,
            @RequestParam("username") String username,
            @RequestParam("phone") String phone,
            @RequestParam("email") String email,
            @RequestParam("password") String password,
            @RequestParam("repassword") String repassword,
            Model model) {

        // Kiểm tra mật khẩu khớp
        if (!password.equals(repassword)) {
            model.addAttribute("error", "Mật khẩu xác nhận không khớp!");
            return "register";
        }

        // Kiểm tra tồn tại qua UserService
        if (userService.isUsernameExists(username)) {
            model.addAttribute("error", "Tên đăng nhập đã tồn tại!");
            return "register";
        }

        try {
            userService.registerUser(username, password, fullName, email, phone);
            return "redirect:/login";
        } catch (Exception e) {
            e.printStackTrace();
            model.addAttribute("error", "Có lỗi xảy ra: " + e.getMessage());
            return "register";
        }
    }
}