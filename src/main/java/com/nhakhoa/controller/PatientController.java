package com.nhakhoa.controller;

import com.nhakhoa.dao.AppointmentDAO;
import com.nhakhoa.dao.UserDAO;
import com.nhakhoa.model.User;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.SessionAttribute;

import java.util.List;

@Controller // Đổi sang Annotation của Spring
public class PatientController {

    // Thay thế hoàn toàn cho doGet, doPost và @WebServlet(urlPatterns = {"/patients"})
    @GetMapping("/patients")
    public String managePatients(
            @SessionAttribute(value = "acc", required = false) User acc,
            @RequestParam(value = "message", required = false) String msg,
            Model model) {

        try {
            // 1. Kiểm tra quyền truy cập (Chỉ cho phép Admin=1 hoặc Bác sĩ=2)
            if (acc == null || (acc.getRoleID() != 1 && acc.getRoleID() != 2)) {
                return "redirect:/login"; // Đá thẳng về trang đăng nhập nếu trái phép
            }

            List<User> listP;
            AppointmentDAO appDao = new AppointmentDAO();
            UserDAO uDao = new UserDAO();

            // 2. Phân tách logic lấy dữ liệu theo Vai trò (Role) giống hệt cấu trúc cũ
            if (acc.getRoleID() == 1) {
                // Admin: Lấy toàn bộ bệnh nhân hệ thống
                listP = uDao.getAllPatients();
            } else {
                // Bác sĩ: Lấy tất cả bệnh nhân từng khám với mình (dùng SQL UNION cũ)
                listP = appDao.getPatientsByDentist(acc.getUserID());
            }

            // 3. Xử lý thông báo phản hồi từ Param truyền về
            if ("success".equals(msg)) {
                model.addAttribute("succMsg", "Thao tác trên hồ sơ bệnh nhân thành công!");
            }

            // 4. Đẩy dữ liệu ra Spring Model (Thay thế cho request.setAttribute)
            model.addAttribute("listP", listP);
            model.addAttribute("activePage", "patients"); // Để làm sáng menu "Bệnh nhân" ở Sidebar
            
            // Mở file patients.html nằm trong thư mục src/main/resources/templates/
            return "patients";
            
        } catch (Exception e) {
            e.printStackTrace();
            // Nếu có lỗi, đẩy sang trang thông báo lỗi error.html
            model.addAttribute("errorMessage", "Đã xảy ra lỗi khi tải danh sách bệnh nhân.");
            return "error";
        }
    }

    // Đề phòng trường hợp hệ thống cũ có submit form hoặc link bằng POST qua url này
    @PostMapping("/patients")
    public String managePatientsPost(@SessionAttribute(value = "acc", required = false) User acc, 
                                     @RequestParam(value = "message", required = false) String msg, 
                                     Model model) {
        return managePatients(acc, msg, model); // Gọi lại hàm GET ở trên để dùng chung logic
    }
}