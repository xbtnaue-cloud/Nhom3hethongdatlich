package com.nhakhoa.controller;

import com.nhakhoa.model.User;
import com.nhakhoa.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
public class EditDoctorController {

    @Autowired
    private UserService userService;

    @GetMapping("/edit-doctor")
    public String showEditForm(@RequestParam(value = "id", required = false) Integer id, Model model) {
        if (id == null) return "redirect:/manage-doctors";

        User doc = userService.getUserByID(id);
        if (doc != null) {
            model.addAttribute("doc", doc);
            return "edit-doctor"; // Trả về trang edit-doctor.html thay vì dùng chung doctors.jsp
        }
        return "redirect:/manage-doctors";
    }

    @PostMapping("/edit-doctor")
    public String updateDoctor(
            @RequestParam("id") int id,
            @RequestParam("name") String name,
            @RequestParam("email") String email,
            @RequestParam("phone") String phone,
            @RequestParam("username") String username,
            @RequestParam("password") String password,
            @RequestParam(value = "specialty", defaultValue = "") String specialty,
            @RequestParam(value = "statusID", required = false, defaultValue = "1") Integer statusID) {

        try {
            userService.updateDoctor(id, name, email, phone, username, specialty, statusID, password);
            return "redirect:/manage-doctors";
        } catch (Exception e) {
            e.printStackTrace();
            return "redirect:/manage-doctors?error=1";
        }
    }
}