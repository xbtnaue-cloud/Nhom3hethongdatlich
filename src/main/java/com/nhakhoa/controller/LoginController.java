package com.nhakhoa.controller;

import com.nhakhoa.model.User;
import com.nhakhoa.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import jakarta.servlet.http.HttpSession;

@Controller
public class LoginController {

    @Autowired
    private UserService userService;

    @GetMapping("/login")
    public String showLoginForm() {
        return "login";
    }

    @PostMapping("/login")
    public String handleLogin(
            @RequestParam("username") String username,
            @RequestParam("password") String password,
            HttpSession session,
            Model model) {

        // Sử dụng UserService thay vì UserDAO
        User account = userService.login(username, password);

        if (account != null) {
            // Kiểm tra trạng thái tài khoản
            if (account.getStatusID() == 0) {
                model.addAttribute("error", "Tài khoản của bạn hiện đang bị khóa!");
                return "login";
            }

            // Tạo Session
            session.setAttribute("acc", account);

            // Điều hướng theo Role
            switch (account.getRoleID()) {
                case 1: return "redirect:/admin-dashboard";
                case 2: return "redirect:/dentist-dashboard";
                case 3: return "redirect:/index";
                default: return "redirect:/login";
            }
        } else {
            model.addAttribute("error", "Tên đăng nhập hoặc mật khẩu không chính xác!");
            return "login";
        }
    }
}