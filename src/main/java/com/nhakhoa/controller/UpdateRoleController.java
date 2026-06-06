package com.nhakhoa.controller;

import com.nhakhoa.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
public class UpdateRoleController {

    @Autowired
    private UserService userService;

    @GetMapping("/update-role")
    public String updateRole(
            @RequestParam("id") int id,
            @RequestParam("roleID") int roleID) {
        
        try {
            // Sử dụng Service thay vì DAO
            userService.updateUserRole(id, roleID);
            return "redirect:/manage-users";
            
        } catch (Exception e) {
            e.printStackTrace();
            return "redirect:/manage-users";
        }
    }
}