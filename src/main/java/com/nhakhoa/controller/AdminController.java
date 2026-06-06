package com.nhakhoa.controller;

import com.nhakhoa.dto.AppointmentDTO;
import com.nhakhoa.service.AppointmentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import java.security.Principal;
import java.util.List;

@Controller
@RequestMapping("/admin") // Đường dẫn gốc cho tất cả các trang admin
public class AdminController {

    @Autowired
    private AppointmentService appointmentService;

    // Truy cập: /admin/dashboard
    @GetMapping("/dashboard")
    public String showAdminDashboard(Principal principal, Model model) {
        
        // 1. Spring Security tự động chặn các role không phải ADMIN ở SecurityConfig
        // Principal là đối tượng chứa thông tin user đang đăng nhập
        if (principal == null) {
            return "redirect:/login";
        }

        try {
            // 2. Gọi service lấy dữ liệu (không dùng DAO trực tiếp)
            List<AppointmentDTO> listA = appointmentService.getAllAppointments();

            // 3. Đẩy dữ liệu vào model
            model.addAttribute("listApp", listA);
            model.addAttribute("activePage", "appointments");
            
            // 4. Trả về file dashboard.html (đảm bảo file nằm ở templates/dashboard.html)
            return "dashboard";
            
        } catch (Exception e) {
            e.printStackTrace();
            model.addAttribute("error", "Lỗi tải dữ liệu Dashboard: " + e.getMessage());
            return "error";
        }
    }
}