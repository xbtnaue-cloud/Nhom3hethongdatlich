package com.nhakhoa.controller;

import com.nhakhoa.model.User;
import com.nhakhoa.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.SessionAttribute;
import jakarta.servlet.http.HttpSession;

@Controller
public class UpdateProfileController {

    @Autowired
    private UserService userService;

    @GetMapping("/profile")
    public String showProfile(@SessionAttribute(value = "acc", required = false) User acc) {
        if (acc == null) return "redirect:/login";
        return "profile";
    }

    @PostMapping("/update-profile")
    public String updateProfile(
            @SessionAttribute(value = "acc", required = false) User acc,
            @RequestParam("fullName") String fullName,
            @RequestParam("phone") String phone,
            @RequestParam(value = "newPass", required = false) String newPass,
            HttpSession session,
            Model model) {

        if (acc == null) return "redirect:/login";

        String password = (newPass == null || newPass.isBlank()) ? acc.getPassword() : newPass.trim();

        try {
            // Cập nhật qua Service
            userService.updateProfile(acc.getUserID(), fullName.trim(), phone.trim(), password);

            // Đồng bộ Session
            acc.setFullName(fullName.trim());
            acc.setPhone(phone.trim());
            acc.setPassword(password);
            session.setAttribute("acc", acc);

            model.addAttribute("success", "Cập nhật thông tin thành công!");
            return "profile";

        } catch (Exception e) {
            e.printStackTrace();
            model.addAttribute("error", "Có lỗi xảy ra khi cập nhật!");
            return "profile";
        }
    }
}