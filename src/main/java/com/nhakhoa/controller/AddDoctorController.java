package com.nhakhoa.controller;

import com.nhakhoa.dao.UserDAO;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller // 1. Đổi sang Annotation của Spring
public class AddDoctorController {

    // 2. Thay thế cho doGet: Hiển thị form thêm bác sĩ
    @GetMapping("/add-doctor")
    public String showAddDoctorForm(@RequestParam(value = "error", required = false) String error, Model model) {
        if ("1".equals(error)) {
            model.addAttribute("error", "Đã xảy ra lỗi hệ thống khi thêm bác sĩ!");
        }
        return "add_doctor"; // Trả về file add_doctor.html trong thư mục templates
    }

    // 3. Thay thế cho doPost: Xử lý dữ liệu gửi lên từ Form
    @PostMapping("/add-doctor")
    public String addDoctor(
            @RequestParam("name") String name,
            @RequestParam("email") String email,
            @RequestParam("phone") String phone,
            @RequestParam("username") String username,
            @RequestParam("password") String pass,
            @RequestParam(value = "specialty", required = false, defaultValue = "") String specialty,
            Model model,
            RedirectAttributes redirectAttributes) {

        try {
            UserDAO dao = new UserDAO();

            // Check trùng tài khoản hoặc email
            if (dao.checkUserExist(username) || dao.checkEmailExist(email)) {
                model.addAttribute("error", "Username hoặc Email này đã được sử dụng!");
                
                // Giữ lại các giá trị đã nhập để user không phải gõ lại từ đầu (UX chuẩn bài luôn)
                model.addAttribute("name", name);
                model.addAttribute("email", email);
                model.addAttribute("phone", phone);
                model.addAttribute("username", username);
                model.addAttribute("specialty", specialty);
                
                return "add_doctor"; 
            }

            // Gọi hàm chèn bác sĩ của ní từ DAO cũ qua
            dao.insertDoctor(username, pass, name, email, phone, specialty);

            // Chuyển hướng trang thành công (sendRedirect cũ)
            return "redirect:/manage-doctors";

        } catch (Exception e) {
            e.printStackTrace();
            // Nếu lỗi thì quay về trang add-doctor kèm parameter error=1
            return "redirect:/add-doctor?error=1";
        }
    }
}