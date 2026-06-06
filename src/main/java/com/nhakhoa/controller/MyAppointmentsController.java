package com.nhakhoa.controller;

import com.nhakhoa.model.Appointment;
import com.nhakhoa.model.User;
import com.nhakhoa.service.AppointmentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.SessionAttribute;

import java.util.List;

@Controller
public class MyAppointmentsController {

    @Autowired
    private AppointmentService appointmentService;

    @GetMapping("/my-appointments")
    public String getMyAppointments(
            @SessionAttribute(value = "acc", required = false) User acc, 
            Model model) {

        if (acc == null) {
            return "redirect:/login";
        }

        try {
            // Sử dụng Service thay vì DAO
            List<Appointment> list = appointmentService.getAppointmentsByUserId(acc.getUserID());
            
            model.addAttribute("listApp", list);
            return "my-appointments";
            
        } catch (Exception e) {
            e.printStackTrace();
            model.addAttribute("error", "Lỗi tải lịch sử đặt lịch: " + e.getMessage());
            return "error";
        }
    }
}