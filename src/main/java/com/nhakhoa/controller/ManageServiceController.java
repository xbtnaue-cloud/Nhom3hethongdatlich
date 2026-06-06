package com.nhakhoa.controller;

import com.nhakhoa.model.Service;
import com.nhakhoa.model.User;
import com.nhakhoa.service.ServiceService;
import com.nhakhoa.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;

import java.util.List;

@Controller
public class ManageServiceController {

    @Autowired
    private ServiceService serviceService;

    @Autowired
    private UserService userService;

    @GetMapping("/manage-services")
    public String manageServices(Model model) {
        try {
            // Lấy danh sách dịch vụ và danh sách bác sĩ thông qua Service
            List<Service> listS = serviceService.getAllServices();
            List<User> listD = userService.getAllDoctors();
            
            model.addAttribute("listS", listS);
            model.addAttribute("listD", listD);
            model.addAttribute("activePage", "services");

            return "services";
            
        } catch (Exception e) {
            e.printStackTrace();
            model.addAttribute("error", "Lỗi khi tải dữ liệu dịch vụ: " + e.getMessage());
            return "error";
        }
    }

    @PostMapping("/manage-services")
    public String manageServicesPost(Model model) {
        return manageServices(model);
    }
}