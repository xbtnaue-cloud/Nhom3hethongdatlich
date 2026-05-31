package com.nhakhoa.controller;

import com.nhakhoa.dao.AppointmentDAO;
import com.nhakhoa.model.Appointment;
import com.nhakhoa.model.User;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.SessionAttribute;

import java.util.List;

@Controller // Đổi sang Annotation của Spring
public class AdminController {

    // Gom cả xử lý GET và POST chung một logic giống như processRequest ngày xưa của ní
    @GetMapping("/admin-dashboard")
    public String showAdminDashboard(
            // Tự động bốc đối tượng "acc" trong Session ra, nếu chưa đăng nhập thì trả về null chứ không crash
            @SessionAttribute(value = "acc", required = false) User acc, 
            Model model) {

        try {
            // --- KIỂM TRA BẢO MẬT (SECURITY) ---
            // Nếu chưa đăng nhập hoặc không phải Role Admin (RoleID != 1)
            if (acc == null || acc.getRoleID() != 1) {
                // Trong Spring Boot, trang login.html của ní nằm trong templates nên redirect thẳng qua url login
                return "redirect:/login"; 
            }

            // 2. Lấy dữ liệu lịch hẹn từ AppointmentDAO cũ
            AppointmentDAO dao = new AppointmentDAO();
            List<Appointment> listA = dao.getAllAppointments(); 

            // 3. Đẩy dữ liệu vào Model (Thay thế cho request.setAttribute)
            // Tên "listApp" giữ nguyên để đồng bộ với vòng lặp th:each trong file html mới
            model.addAttribute("listApp", listA);
            
            // 4. Chuyển hướng tới trang Dashboard (Mở file dashboard.html trong thư mục templates)
            return "dashboard";
            
        } catch (Exception e) {
            e.printStackTrace();
            // Đẩy thông báo lỗi sang giao diện nếu có sự cố
            model.addAttribute("error", "Lỗi tải dữ liệu Dashboard: " + e.getMessage());
            return "error"; // Mở file error.html trong thư mục templates
        }
    }

    // Nếu hệ thống cũ có form nào POST tới /admin-dashboard thì nó sẽ chạy vào đây
    @PostMapping("/admin-dashboard")
    public String handlePostDashboard(
            @SessionAttribute(value = "acc", required = false) User acc, 
            Model model) {
        return showAdminDashboard(acc, model); // Gọi lại hàm GET ở trên để xử lý chung logic
    }
}