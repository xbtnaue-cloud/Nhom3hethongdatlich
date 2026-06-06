package com.nhakhoa.controller;

import com.nhakhoa.service.ServiceService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
public class DeleteServiceController {

    @Autowired
    private ServiceService serviceService; // Sử dụng Service chuẩn Spring

    @GetMapping("/delete-service")
    public String deleteService(@RequestParam(value = "id", required = false) Integer id) {
        
        // Kiểm tra hợp lệ
        if (id == null) {
            return "redirect:/manage-services?error=deletefail";
        }

        try {
            // Gọi Service để xử lý xóa
            serviceService.deleteService(id);
            
            return "redirect:/manage-services";
            
        } catch (Exception e) {
            e.printStackTrace();
            return "redirect:/manage-services?error=deletefail";
        }
    }
}