package com.nhakhoa.controller;

import com.nhakhoa.dao.UserDAO;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller // Đổi sang Annotation của Spring
public class AddUserController {

    // 1. Thay thế cho doGet: Nếu cố tình vào bằng GET thì đá về trang danh sách
    @GetMapping("/add-user")
    public String handleGetRequest() {
        return "redirect:/manage-users";
    }

    // 2. Thay thế cho doPost: Xử lý Form thêm tài khoản mới
    @PostMapping("/add-user")
    public String addUser(
            @RequestParam("username") String user,
            @RequestParam("password") String pass,
            @RequestParam("fullName") String name,
            @RequestParam("email") String email,
            @RequestParam("phone") String phone,
            @RequestParam("roleID") int roleID) {

        try {
            UserDAO dao = new UserDAO();

            // Kiểm tra username đã tồn tại chưa
            boolean isExist = dao.checkUserExist(user);

            if (!isExist) {
                // Tiến hành thêm mới tài khoản bằng hàm register cũ của ní
                boolean success = dao.register(user, pass, name, email, phone, roleID);
                
                if (success) {
                    // Thêm thành công -> Chuyển về trang danh sách kèm thông báo thành công
                    return "redirect:/manage-users?msg=success";
                } else {
                    // Lỗi thực thi SQL
                    return "redirect:/manage-users?msg=error";
                }
            } else {
                // Tài khoản đã tồn tại -> Thông báo lỗi trùng lặp
                return "redirect:/manage-users?msg=existed";
            }

        } catch (Exception e) {
            e.printStackTrace();
            // Lỗi hệ thống (ví dụ: lỗi ép kiểu dữ liệu từ form)
            return "redirect:/manage-users?msg=system_error";
        }
    }
}