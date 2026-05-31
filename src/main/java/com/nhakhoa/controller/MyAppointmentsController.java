package com.nhakhoa.controller;

import com.nhakhoa.dao.AppointmentDAO;
import com.nhakhoa.model.Appointment;
import com.nhakhoa.model.User;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.SessionAttribute;

import java.util.List;

@Controller // Đổi sang Annotation của Spring
public class MyAppointmentsController {

    // Thay thế hoàn toàn cho doGet và @WebServlet(urlPatterns = {"/my-appointments"})
    @GetMapping("/my-appointments")
    public String getMyAppointments(
            @SessionAttribute(value = "acc", required = false) User acc, 
            Model model) {

        // 1. Kiểm tra đăng nhập (Nếu chưa đăng nhập, đá thẳng về url login chuẩn)
        if (acc == null) {
            return "redirect:/login";
        }

        try {
            // 2. Khởi tạo DAO cũ và lấy danh sách lịch hẹn theo UserID của tài khoản đang đăng nhập
            AppointmentDAO dao = new AppointmentDAO();
            List<Appointment> list = dao.getAppointmentsByUserId(acc.getUserID());
            
            // 3. Đẩy danh sách lịch hẹn vào Model (Khớp 100% tên biến listApp cũ của ní)
            model.addAttribute("listApp", list);
            
            // 4. Mở giao diện tương ứng (Đổi từ /views/my-appointments.jsp thành my-appointments.html trong templates)
            return "my-appointments";
            
        } catch (Exception e) {
            e.printStackTrace();
            // Nếu có sự cố hệ thống, đẩy về trang lỗi error.html
            model.addAttribute("error", "Lỗi tải lịch sử đặt lịch: " + e.getMessage());
            return "error";
        }
    }
}