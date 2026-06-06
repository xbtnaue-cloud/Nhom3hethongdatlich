package com.nhakhoa.controller;

import com.nhakhoa.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
public class AddUserController {

    @Autowired
    private UserService userService;

    @GetMapping("/add-user")
    public String handleGetRequest() {
        return "redirect:/manage-users";
    }

    @PostMapping("/add-user")
    public String addUser(
            @RequestParam("username") String user,
            @RequestParam("password") String pass,
            @RequestParam("fullName") String name,
            @RequestParam("email") String email,
            @RequestParam("phone") String phone,
            @RequestParam("roleID") int roleID) {

        try {
            // Kiểm tra username đã tồn tại chưa
            if (!userService.checkUserExist(user)) {
                // Sử dụng hàm register từ Service
                boolean success = userService.register(user, pass, name, email, phone, roleID);
                
                if (success) {
                    return "redirect:/manage-users?msg=success";
                } else {
                    return "redirect:/manage-users?msg=error";
                }
            } else {
                return "redirect:/manage-users?msg=existed";
            }
        } catch (Exception e) {
            e.printStackTrace();
            return "redirect:/manage-users?msg=system_error";
        }
    }
}