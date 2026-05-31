package com.nhakhoa.controller;

import com.nhakhoa.dao.AppointmentDAO;
import com.nhakhoa.dao.UserDAO;
import com.nhakhoa.model.User;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.SessionAttribute;

@Controller // Đổi sang Annotation của Spring
public class AdminDashboardController {

    // Thay thế hoàn toàn cho doGet, doPost và @WebServlet(urlPatterns = {"/admin-stats"})
    @GetMapping("/admin-stats")
    public String showDashboardStats(
            @SessionAttribute(value = "acc", required = false) User acc, 
            Model model) {
        
        // 1. Kiểm tra quyền truy cập (Chỉ cho phép Admin=1 hoặc Bác sĩ=2)
        if (acc == null || (acc.getRoleID() != 1 && acc.getRoleID() != 2)) {
            return "redirect:/login"; // Đá về trang login nếu không đủ quyền
        }

        // 2. Khởi tạo các DAO cũ của ní
        UserDAO uDao = new UserDAO();
        AppointmentDAO aDao = new AppointmentDAO();

        try {
            // KHAI BÁO CÁC BIẾN THỐNG KÊ
            int totalPatients, totalDoctors, totalAppointments, pending, confirmed, completed, cancelled;
            double totalRevenue;

            if (acc.getRoleID() == 1) {
                // --- TRƯỜNG HỢP: QUẢN TRỊ VIÊN (ADMIN) ---
                totalPatients = uDao.getTotalPatients();
                totalDoctors = uDao.getActiveDoctorsCount(); 
                totalRevenue = aDao.getTotalRevenue();
                totalAppointments = aDao.getTotalAppointments();
                
                // Thống kê trạng thái toàn hệ thống
                pending = aDao.countByStatus("Pending");
                confirmed = aDao.countByStatus("Confirmed");
                completed = aDao.countByStatus("Completed");
                cancelled = aDao.countByStatus("Cancelled");
                
                model.addAttribute("roleName", "Quản trị viên");
            } else {
                // --- TRƯỜNG HỢP: BÁC SĨ CHUYÊN KHOA ---
                int dID = acc.getUserID();
                
                totalPatients = aDao.getPatientsByDentist(dID).size(); 
                totalDoctors = (acc.getStatusID() == 1) ? 1 : 0;
                totalRevenue = aDao.getRevenueByDentist(dID);
                totalAppointments = aDao.getAppointmentsByDentist(dID).size();
                
                // Thống kê trạng thái của riêng bác sĩ đó
                pending = aDao.countByStatusAndDentist("Pending", dID);
                confirmed = aDao.countByStatusAndDentist("Confirmed", dID);
                completed = aDao.countByStatusAndDentist("Completed", dID);
                cancelled = aDao.countByStatusAndDentist("Cancelled", dID);
                
                model.addAttribute("roleName", "Bác sĩ chuyên khoa");
            }

            // 3. ĐẨY DỮ LIỆU RA MODEL (Thay thế hoàn toàn cho request.setAttribute)
            model.addAttribute("totalPatients", totalPatients);
            model.addAttribute("totalDoctors", totalDoctors); 
            model.addAttribute("totalRevenue", totalRevenue);
            model.addAttribute("totalAppointments", totalAppointments);
            model.addAttribute("pending", pending);
            model.addAttribute("confirmed", confirmed);
            model.addAttribute("completed", completed);
            model.addAttribute("cancelled", cancelled);

            // Gán trạng thái active để file HTML xử lý sáng menu Sidebar
            model.addAttribute("activePage", "dashboard");
            
            // Trả về file admin-dashboard.html trong thư mục templates
            return "admin-dashboard";

        } catch (Exception e) {
            e.printStackTrace();
            // Nếu có lỗi hệ thống, vẫn mở trang dashboard trống để tránh sập web trống trải
            return "admin-dashboard";
        }
    }

    // Map thêm phương thức POST đề phòng trường hợp form cũ submit bằng POST qua url này
    @PostMapping("/admin-stats")
    public String handlePostStats(@SessionAttribute(value = "acc", required = false) User acc, Model model) {
        return showDashboardStats(acc, model);
    }
}