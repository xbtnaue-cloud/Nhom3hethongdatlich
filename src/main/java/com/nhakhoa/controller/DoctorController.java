package com.nhakhoa.controller;

import com.nhakhoa.dao.AppointmentDAO;
import com.nhakhoa.model.User;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;

import java.util.List;

@Controller // Đổi sang Annotation của Spring
public class DoctorController {

    // Thay thế hoàn toàn cho doGet, doPost và urlPatterns = {"/doctors"}
    @GetMapping("/doctors")
    public String showDoctorsList(Model model) {
        try {
            // 1. Khởi tạo AppointmentDAO cũ của ní
            AppointmentDAO dao = new AppointmentDAO();

            // 2. Lấy danh sách bác sĩ (RoleID = 2) từ DAO cũ
            List<User> listD = dao.getAllDoctors(); 

            // 3. Đẩy dữ liệu vào Model (Khớp 100% tên biến cũ để lặp dữ liệu)
            model.addAttribute("listD", listD);
            
            // Thuộc tính dùng để sáng menu "Bác sĩ" ở thanh sidebar bên Admin
            model.addAttribute("activePage", "doctors"); 

            // 4. Mở file giao diện (Đổi từ views/doctors.jsp thành doctors.html trong templates)
            return "doctors";

        } catch (Exception e) {
            e.printStackTrace();
            // Nếu có lỗi hệ thống, đẩy về trang báo lỗi error.html
            model.addAttribute("error", "Lỗi tải danh sách bác sĩ: " + e.getMessage());
            return "error";
        }
    }

    // Đề phòng trường hợp form tìm kiếm hoặc tính năng nào POST dữ liệu qua url này
    @PostMapping("/doctors")
    public String handlePostDoctors(Model model) {
        return showDoctorsList(model); // Gọi lại hàm GET ở trên để đồng bộ xử lý
    }
}