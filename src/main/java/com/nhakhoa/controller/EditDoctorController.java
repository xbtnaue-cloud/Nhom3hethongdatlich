package com.nhakhoa.controller;

import com.nhakhoa.dao.UserDAO;
import com.nhakhoa.model.User;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller // Đổi sang Annotation của Spring
public class EditDoctorController {

    // 1. GET: Lấy thông tin bác sĩ cũ đưa lên form sửa
    @GetMapping("/edit-doctor")
    public String showEditForm(@RequestParam(value = "id", required = false) Integer id, Model model) {
        if (id == null) {
            return "redirect:/manage-doctors";
        }

        try {
            UserDAO dao = new UserDAO();
            User doc = dao.getUserByID(id);

            if (doc != null) {
                model.addAttribute("doc", doc); // Đẩy đối tượng bác sĩ ra giao diện
                
                // Mở file giao diện. 
                // LƯU Ý: Giữ nguyên tên file trả về khớp với file view cũ (ở đây code cũ của ní đang gọi doctors.jsp)
                return "doctors"; 
            } else {
                return "redirect:/manage-doctors";
            }
        } catch (Exception e) {
            e.printStackTrace();
            return "redirect:/manage-doctors";
        }
    }

    // 2. POST: Hứng dữ liệu từ Form gửi lên để thực hiện Update
    @PostMapping("/edit-doctor")
    public String updateDoctor(
            @RequestParam("id") int id,
            @RequestParam("name") String name,
            @RequestParam("email") String email,
            @RequestParam("phone") String phone,
            @RequestParam("username") String username,
            @RequestParam("password") String password,
            @RequestParam(value = "specialty", required = false, defaultValue = "") String specialty,
            @RequestParam(value = "statusID", required = false) Integer statusID,
            Model model) {

        try {
            // Xử lý giá trị mặc định cho trạng thái giống logic cũ của ní
            if (statusID == null) {
                statusID = 1;
            }

            // Gọi DAO cũ để thực thi cập nhật dữ liệu xuống DB
            UserDAO dao = new UserDAO();
            dao.updateDoctor(id, name, email, phone, username, specialty, statusID, password);

            // Thành công quay về trang danh sách
            return "redirect:/manage-doctors";

        } catch (Exception e) {
            e.printStackTrace();
            // Thất bại quay lại kèm tham số báo lỗi
            return "redirect:/manage-doctors?error=1";
        }
    }
}