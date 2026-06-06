package com.nhakhoa.controller;

import com.nhakhoa.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
public class UpdateUserAdminController {

    @Autowired
    private UserService userService;

    @PostMapping("/update-user-admin")
    public String updateUserAdmin(
            @RequestParam("id") int id,
            @RequestParam("fullName") String fullName,
            @RequestParam("email") String email,
            @RequestParam("phone") String phone,
            @RequestParam("roleID") int roleID,
            @RequestParam(value = "isReset", required = false, defaultValue = "false") boolean isReset) {

        try {
            // Logic được xử lý tập trung trong Service
            userService.updateUserByAdmin(id, fullName.trim(), email.trim(), phone.trim(), roleID, isReset);
            return "redirect:/manage-users?status=updateSuccess";

        } catch (Exception e) {
            e.printStackTrace();
            return "redirect:/manage-users?status=error";
        }
    }

    @GetMapping("/update-user-admin")
    public String handleGetRequest() {
        return "redirect:/manage-users";
    }
}