package com.nhakhoa.controller;

import com.nhakhoa.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
public class DeleteDoctorController {

    @Autowired
    private UserService userService;

    @GetMapping("/delete-doctor")
    public String deleteDoctor(@RequestParam(value = "id", required = false) Integer id) {
        
        if (id == null) {
            return "redirect:/manage-doctors?error=invalidid";
        }

        // Gọi trực tiếp qua Service, không dùng DAO nữa
        boolean isDeleted = userService.deleteUser(id);

        if (isDeleted) {
            return "redirect:/manage-doctors?success=deleted";
        } else {
            return "redirect:/manage-doctors?error=cannotdelete";
        }
    }
}