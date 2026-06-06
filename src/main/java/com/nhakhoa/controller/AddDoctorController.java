package com.nhakhoa.controller;

import com.nhakhoa.service.UserService; // Import Service
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
public class AddDoctorController {

    @Autowired
    private UserService userService; // Sử dụng DI thay vì new DAO

    @GetMapping("/add-doctor")
    public String showAddDoctorForm(@RequestParam(value = "error", required = false) String error, Model model) {
        if ("1".equals(error)) {
            model.addAttribute("error", "Đã xảy ra lỗi hệ thống khi thêm bác sĩ!");
        }
        return "add_doctor";
    }

    @PostMapping("/add-doctor")
    public String addDoctor(
            @RequestParam("name") String name,
            @RequestParam("email") String email,
            @RequestParam("phone") String phone,
            @RequestParam("username") String username,
            @RequestParam("password") String pass,
            @RequestParam(value = "specialty", required = false, defaultValue = "") String specialty,
            Model model) {

        try {
            // Kiểm tra trùng tài khoản hoặc email thông qua Service
            if (userService.checkUserExist(username) || userService.checkEmailExist(email)) {
                model.addAttribute("error", "Username hoặc Email này đã được sử dụng!");
                
                model.addAttribute("name", name);
                model.addAttribute("email", email);
                model.addAttribute("phone", phone);
                model.addAttribute("username", username);
                model.addAttribute("specialty", specialty);
                
                return "add_doctor"; 
            }

            // Gọi hàm chèn bác sĩ qua Service
            userService.insertDoctor(username, pass, name, email, phone, specialty);

            return "redirect:/manage-doctors";

        } catch (Exception e) {
            e.printStackTrace();
            return "redirect:/add-doctor?error=1";
        }
    }
}