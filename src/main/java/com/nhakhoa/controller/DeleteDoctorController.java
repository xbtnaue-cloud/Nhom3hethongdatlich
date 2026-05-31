package com.nhakhoa.controller;

import com.nhakhoa.dao.UserDAO;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller // Đổi sang Annotation của Spring
public class DeleteDoctorController {

    // 1. Thay thế cho doGet: Xử lý xóa bác sĩ bằng phương thức GET (Từ thẻ <a> trên giao diện)
    @GetMapping("/delete-doctor")
    public String deleteDoctor(@RequestParam(value = "id", required = false) Integer id) {
        
        // Kiểm tra nếu ID truyền vào bị null
        if (id == null) {
            return "redirect:/manage-doctors?error=invalidid";
        }

        try {
            // Gọi sang UserDAO cũ của ní để thực hiện lệnh xóa
            UserDAO dao = new UserDAO();
            boolean isDeleted = dao.deleteUser(id);

            if (isDeleted) {
                // Xóa thành công quay về trang danh sách
                return "redirect:/manage-doctors";
            } else {
                // Thất bại (ví dụ: vướng khóa ngoại lịch hẹn) đá về kèm mã lỗi
                return "redirect:/manage-doctors?error=cannotdelete";
            }
            
        } catch (Exception e) {
            e.printStackTrace();
            return "redirect:/manage-doctors?error=invalidid";
        }
    }

    // 2. Thay thế cho doPost: Đề phòng trường hợp giao diện submit yêu cầu xóa bằng POST
    @PostMapping("/delete-doctor")
    public String deleteDoctorPost(@RequestParam(value = "id", required = false) Integer id) {
        return deleteDoctor(id); // Gọi lại hàm GET phía trên để dùng chung logic
    }
}