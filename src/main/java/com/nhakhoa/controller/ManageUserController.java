package com.nhakhoa.controller;

import com.nhakhoa.model.User;
import com.nhakhoa.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.List;

@Controller
public class ManageUserController {

    @Autowired
    private UserService userService;

    @GetMapping("/manage-users")
    public String manageUsers(Model model) {
        try {
            // Lấy danh sách toàn bộ người dùng qua UserService
            List<User> list = userService.getAllUsers();
            
            // Đẩy dữ liệu vào Model
            model.addAttribute("listU", list);
            model.addAttribute("activePage", "users");
            
            return "admin-users";
            
        } catch (Exception e) {
            e.printStackTrace();
            model.addAttribute("error", "Lỗi tải danh sách người dùng: " + e.getMessage());
            return "error";
        }
    }
}