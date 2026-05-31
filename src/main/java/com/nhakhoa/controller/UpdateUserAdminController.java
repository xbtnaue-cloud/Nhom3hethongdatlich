package com.nhakhoa.controller;

import com.nhakhoa.dao.UserDAO;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller // Đổi sang Annotation của Spring
public class UpdateUserAdminController {

    // 1. POST: Xử lý dữ liệu cập nhật & Reset mật khẩu từ Form Modal của Admin gửi lên
    @PostMapping("/update-user-admin")
    public String updateUserAdmin(
            @RequestParam("id") int id,
            @RequestParam("fullName") String fullName,
            @RequestParam("email") String email,
            @RequestParam("phone") String phone,
            @RequestParam("roleID") int roleID,
            @RequestParam(value = "isReset", required = false, defaultValue = "false") boolean isReset) {

        try {
            UserDAO dao = new UserDAO();

            // 2. Thực hiện cập nhật thông tin cơ bản & vai trò qua DAO cũ của ní
            dao.updateUserByAdmin(id, fullName.trim(), email.trim(), phone.trim(), roleID);

            // 3. Kiểm tra nếu cờ isReset ăn giá trị true (Admin nhấn nút Reset)
            if (isReset) {
                String passwordDefault = "123456"; // Mật khẩu mặc định sau khi reset
                dao.updatePasswordById(id, passwordDefault);
            }

            // 4. Quay trở lại trang quản lý kèm theo tham số báo thành công
            return "redirect:/manage-users?status=updateSuccess";

        } catch (Exception e) {
            e.printStackTrace();
            // Quay trở lại trang quản lý với thông báo lỗi hệ thống
            return "redirect:/manage-users?status=error";
        }
    }

    // 2. GET: Chặn người dùng cố tình truy cập trực tiếp URL này bằng phương thức GET
    @GetMapping("/update-user-admin")
    public String handleGetRequest() {
        return "redirect:/manage-users";
    }
}