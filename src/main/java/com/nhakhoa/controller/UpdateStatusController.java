package com.nhakhoa.controller;

import com.nhakhoa.model.User;
import com.nhakhoa.service.AppointmentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

@Controller
public class UpdateStatusController {

    @Autowired
    private AppointmentService appointmentService;

    @RequestMapping(value = "/update-status", method = {RequestMethod.GET, RequestMethod.POST})
    public String updateStatus(
            @SessionAttribute(value = "acc", required = false) User acc,
            @RequestParam(value = "id", required = false) Integer id,
            @RequestParam(value = "status", required = false) String status,
            @RequestParam(value = "reason", required = false) String reason) {

        // Kiểm tra quyền
        if (acc == null || (acc.getRoleID() != 1 && acc.getRoleID() != 2)) {
            return "redirect:/login";
        }

        // SỬA Ở ĐÂY: Trỏ đúng về /admin/dashboard
        String targetPage = (acc.getRoleID() == 1) ? "redirect:/admin/dashboard" : "redirect:/dentist/dashboard";

        if (id != null && status != null) {
            try {
                boolean success = appointmentService.updateStatus(id, status, reason);
                
                if (!success && "Confirmed".equals(status)) {
                    return targetPage + "?error=busy";
                }
                
                return targetPage + (success ? "?msg=success" : "?msg=fail");

            } catch (Exception e) {
                e.printStackTrace();
                return targetPage;
            }
        }
        return targetPage;
    }
}