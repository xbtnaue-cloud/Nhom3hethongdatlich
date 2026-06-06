package com.nhakhoa.controller;

import com.nhakhoa.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
public class UserStatusController {

    @Autowired
    private UserService userService;

    @GetMapping("/user-status")
    public String updateUserStatus(
            @RequestParam("id") int id,
            @RequestParam("status") int status) {
        
        try {
            // Sử dụng Service thay vì DAO
            userService.updateUserStatus(id, status);
            return "redirect:/manage-users";
            
        } catch (Exception e) {
            e.printStackTrace();
            return "redirect:/manage-users";
        }
    }
}