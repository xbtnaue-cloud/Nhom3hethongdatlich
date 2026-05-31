package com.nhakhoa.controller;

import com.nhakhoa.dao.SpecialtyDAO;
import com.nhakhoa.dao.UserDAO;
import com.nhakhoa.model.User;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.List;

@Controller // Đổi sang Annotation của Spring
public class ManageDoctorController {

    // Thay thế hoàn toàn cho doGet và @WebServlet(urlPatterns = {"/manage-doctors"})
    @GetMapping("/manage-doctors")
    public String manageDoctors(Model model) {
        try {
            // 1. Đánh dấu active menu "doctors" để sidebar Admin sáng đèn
            model.addAttribute("activePage", "doctors");

            // 2. Lấy danh sách bác sĩ thông qua UserDAO cũ
            UserDAO dao = new UserDAO();
            List<User> list = dao.getAllDoctors();
            model.addAttribute("listD", list);

            // 3. Lấy danh sách chuyên khoa từ CSDL thông qua SpecialtyDAO cũ
            SpecialtyDAO specialtyDAO = new SpecialtyDAO();
            List<String> specialties = specialtyDAO.getAllSpecialties();
            model.addAttribute("listSpecialty", specialties);

            // 4. Mở file giao diện doctors.html nằm trong thư mục templates
            return "doctors";

        } catch (Exception e) {
            e.printStackTrace();
            // Nếu xảy ra sự cố hệ thống, đá về trang lỗi error.html
            model.addAttribute("error", "Lỗi hệ thống khi tải trang quản lý bác sĩ: " + e.getMessage());
            return "error";
        }
    }
}