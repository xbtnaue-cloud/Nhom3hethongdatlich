package com.nhakhoa.controller;

import com.nhakhoa.dao.UserDAO;
import com.nhakhoa.model.User;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;

import java.util.List;

@Controller // Đổi sang Annotation của Spring
public class DentistClientController {

    // 1. Thay thế hoàn toàn cho doGet, processRequest và @WebServlet(urlPatterns = {"/dentist-list"})
    @GetMapping("/dentist-list")
    public String showDentistList(Model model) {
        try {
            // Khởi tạo UserDAO cũ của ní
            UserDAO dao = new UserDAO();
            
            // Lấy danh sách những User có RoleID = 2 (Bác sĩ)
            List<User> listD = dao.getAllDoctors();

            // Kiểm tra dữ liệu và gửi sang Model (Thay thế cho request.setAttribute)
            if (listD != null && !listD.isEmpty()) {
                model.addAttribute("listD", listD);
            } else {
                model.addAttribute("mess", "Hiện tại danh sách bác sĩ đang được cập nhật.");
            }

            // Đánh dấu menu active cho thanh Header sáng đèn
            model.addAttribute("activePage", "doctors");

            // Mở file dentist-list.html trong thư mục templates
            return "dentist-list";

        } catch (Exception e) {
            e.printStackTrace();
            // Điều hướng sang trang báo lỗi nếu có sự cố
            model.addAttribute("error", "Lỗi hệ thống: " + e.getMessage());
            return "error"; // Mở file error.html trong thư mục templates
        }
    }

    // 2. Thay thế cho doPost: Đề phòng hệ thống cũ có submit dữ liệu gì bằng POST qua route này
    @PostMapping("/dentist-list")
    public String handlePostRequest(Model model) {
        return showDentistList(model); // Gọi lại hàm GET ở trên để đồng bộ xử lý
    }
}