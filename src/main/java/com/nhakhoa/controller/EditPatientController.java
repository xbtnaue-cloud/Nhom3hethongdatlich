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
public class EditPatientController {

    @Autowired
    private UserService userService;

    @GetMapping("/edit-patient")
    public String showEditPatientForm(@RequestParam(value = "id", required = false) Integer id, Model model) {
        if (id == null) {
            return "redirect:/patients";
        }

        User p = userService.getUserByID(id);
        if (p != null) {
            model.addAttribute("p", p);
            return "edit_patient";
        }
        return "redirect:/patients";
    }

    @PostMapping("/edit-patient")
    public String updatePatient(
            @RequestParam("id") int id,
            @RequestParam("name") String name,
            @RequestParam("email") String email,
            @RequestParam("phone") String phone) {

        try {
            userService.updatePatient(id, name, email, phone);
            return "redirect:/patients?message=success";
        } catch (Exception e) {
            e.printStackTrace();
            return "redirect:/patients?error=updatefail";
        }
    }
}