package com.nhakhoa.controller;

import com.nhakhoa.dao.AppointmentDAO;
import com.nhakhoa.model.Appointment;
import com.nhakhoa.model.User;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.SessionAttribute;

import java.util.ArrayList;
import java.util.List;

@Controller // Đổi sang Annotation của Spring
public class DentistDashboardController { 

    // Thay thế hoàn toàn cho doGet, doPost và urlPatterns = {"/dentist-dashboard"}
    @GetMapping("/dentist-dashboard")
    public String showDentistDashboard(
            @SessionAttribute(value = "acc", required = false) User acc, 
            Model model) {

        // 1 & 2. Kiểm tra đăng nhập và phân quyền (Bắt buộc phải là Bác sĩ - RoleID = 2)
        if (acc == null || acc.getRoleID() != 2) {
            return "redirect:/login"; // Đá thẳng về url login nếu không hợp lệ
        }

        // 3. Lấy ID bác sĩ đang đăng nhập hệ thống
        int dentistID = acc.getUserID();

        // Giữ lại log debug trên Console cho ní dễ theo dõi luồng chạy
        System.out.println("=== DENTIST DASHBOARD DEBUG ===");
        System.out.println("Login Dentist ID: " + dentistID);
        System.out.println("Doctor Name: " + acc.getFullName());

        // 4. Gọi AppointmentDAO cũ để lấy lịch hẹn của riêng bác sĩ này
        AppointmentDAO dao = new AppointmentDAO();
        List<Appointment> list = dao.getAppointmentsByDentist(dentistID);

        // 5. Cơ chế bảo vệ Null-safe tránh lỗi crash giao diện hiển thị
        if (list == null) {
            list = new ArrayList<>();
        }

        System.out.println("Total appointments found: " + list.size());

        // 6. Đẩy dữ liệu ra Model (Khớp 100% tên biến listApp cũ của ní)
        model.addAttribute("listApp", list);
        model.addAttribute("activePage", "appointments"); // Đánh dấu mục sáng trên menu Sidebar

        // 7. Mở file giao diện tương ứng (Đổi từ views/dentist-schedule.jsp thành dentist-schedule.html)
        return "dentist-schedule";
    }

    // Đề phòng trường hợp form hoặc link nào submit bằng POST qua url này
    @PostMapping("/dentist-dashboard")
    public String handlePostDashboard(@SessionAttribute(value = "acc", required = false) User acc, Model model) {
        return showDentistDashboard(acc, model); // Gọi lại hàm GET ở trên để xử lý chung logic
    }
}