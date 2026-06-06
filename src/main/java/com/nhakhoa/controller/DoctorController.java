package com.nhakhoa.controller;

import com.nhakhoa.model.User;
import com.nhakhoa.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;

import java.util.List;

@Controller
public class DoctorController {

    @Autowired
    private UserService userService;

    @GetMapping("/doctors")
    public String showDoctorsList(Model model) {
        try {
            // Lấy danh sách bác sĩ thông qua UserService thay vì DAO cũ
            List<User> listD = userService.getAllDoctors(); 

            model.addAttribute("listD", listD);
            model.addAttribute("activePage", "doctors"); 

            return "doctors";

        } catch (Exception e) {
            e.printStackTrace();
            model.addAttribute("error", "Lỗi tải danh sách bác sĩ: " + e.getMessage());
            return "error";
        }
    }

    @PostMapping("/doctors")
    public String handlePostDoctors(Model model) {
        return showDoctorsList(model);
    }
}