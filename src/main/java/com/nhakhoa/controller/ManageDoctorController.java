package com.nhakhoa.controller;

import com.nhakhoa.model.User;
import com.nhakhoa.service.SpecialtyService;
import com.nhakhoa.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.List;

@Controller
public class ManageDoctorController {

    @Autowired
    private UserService userService;

    @Autowired
    private SpecialtyService specialtyService;

    @GetMapping("/manage-doctors")
    public String manageDoctors(Model model) {
        try {
            model.addAttribute("activePage", "doctors");

            // Lấy danh sách bác sĩ qua UserService
            List<User> list = userService.getAllDoctors();
            model.addAttribute("listD", list);

            // Lấy danh sách chuyên khoa qua SpecialtyService
            List<String> specialties = specialtyService.getAllSpecialties();
            model.addAttribute("listSpecialty", specialties);

            return "doctors";

        } catch (Exception e) {
            e.printStackTrace();
            model.addAttribute("error", "Lỗi hệ thống khi tải trang quản lý bác sĩ: " + e.getMessage());
            return "error";
        }
    }
}