package com.nhakhoa.controller;

import com.nhakhoa.dao.UserDAO;
import com.nhakhoa.model.User;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller // Đổi sang Annotation của Spring
public class EditPatientController {

    // 1. GET: Lấy thông tin bệnh nhân cũ đưa lên form sửa
    @GetMapping("/edit-patient")
    public String showEditPatientForm(@RequestParam(value = "id", required = false) Integer id, Model model) {
        if (id == null) {
            return "redirect:/patients"; // Nếu không có ID, quay lại trang danh sách bệnh nhân
        }

        try {
            UserDAO dao = new UserDAO();
            User p = dao.getUserByID(id);

            if (p != null) {
                // Đẩy dữ liệu bệnh nhân sang Model (Khớp 100% với tên biến p cũ của ní)
                model.addAttribute("p", p);
                return "edit_patient"; // Mở file edit_patient.html trong thư mục templates
            } else {
                return "redirect:/patients";
            }
        } catch (Exception e) {
            e.printStackTrace();
            return "redirect:/patients";
        }
    }

    // 2. POST: Nhận dữ liệu mới từ Form và cập nhật vào CSDL
    @PostMapping("/edit-patient")
    public String updatePatient(
            @RequestParam("id") int id,
            @RequestParam("name") String name,
            @RequestParam("email") String email,
            @RequestParam("phone") String phone) {

        try {
            // Gọi DAO cũ của ní để thực thi lệnh UPDATE
            UserDAO dao = new UserDAO();
            dao.updatePatient(id, name, email, phone);

            // Cập nhật thành công điều hướng về đúng URL danh sách kèm thông báo thành công
            return "redirect:/patients?message=success";

        } catch (Exception e) {
            e.printStackTrace();
            // Lỗi thì đá về lại trang quản lý kèm mã lỗi
            return "redirect:/patients?error=updatefail";
        }
    }
}