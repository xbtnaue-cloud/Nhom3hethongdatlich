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
public class DentistClientController {

    @Autowired
    private UserService userService;

    @GetMapping("/dentist-list")
    public String showDentistList(Model model) {
        try {
            // Sử dụng Service thay vì UserDAO
            List<User> listD = userService.getAllDoctors();

            if (listD != null && !listD.isEmpty()) {
                model.addAttribute("listD", listD);
            } else {
                model.addAttribute("mess", "Hiện tại danh sách bác sĩ đang được cập nhật.");
            }

            model.addAttribute("activePage", "doctors");
            return "dentist-list";

        } catch (Exception e) {
            e.printStackTrace();
            model.addAttribute("error", "Lỗi hệ thống: " + e.getMessage());
            return "error";
        }
    }

    @PostMapping("/dentist-list")
    public String handlePostRequest(Model model) {
        return showDentistList(model);
    }
}