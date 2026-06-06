package com.nhakhoa.controller;

import com.nhakhoa.model.Service;
import com.nhakhoa.service.ServiceService; // Import Service chuẩn
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;

import java.util.List;

@Controller
public class ServiceClientController {

    @Autowired
    private ServiceService serviceService; // Sử dụng DI của Spring

    @GetMapping("/service-list")
    public String getServiceListClient(Model model) {
        try {
            model.addAttribute("activePage", "services");

            // Gọi Service để lấy dữ liệu thay vì new DAO
            List<Service> listS = serviceService.getAllServices();

            if (listS != null && !listS.isEmpty()) {
                model.addAttribute("listS", listS);
            } else {
                model.addAttribute("message", "Hiện tại phòng khám đang cập nhật danh mục dịch vụ mới.");
            }

            return "service-list";

        } catch (Exception e) {
            e.printStackTrace();
            model.addAttribute("error", "Không thể tải danh sách dịch vụ lúc này.");
            return "error";
        }
    }

    @PostMapping("/service-list")
    public String postServiceListClient(Model model) {
        return getServiceListClient(model);
    }
}