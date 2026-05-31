package com.nhakhoa.controller;

import com.nhakhoa.dao.UserDAO;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller // Đổi sang Annotation của Spring
public class UserStatusController {

    // Thay thế hoàn toàn cho doGet và @WebServlet(urlPatterns = {"/user-status"})
    @GetMapping("/user-status")
    public String updateUserStatus(
            @RequestParam("id") int id,
            @RequestParam("status") int status) {
        
        try {
            // Gọi UserDAO cũ để thực thi lệnh UPDATE trạng thái tài khoản (StatusID) trong CSDL
            UserDAO dao = new UserDAO();
            dao.updateUserStatus(id, status);

            // Cập nhật thành công, điều hướng thẳng về lại trang danh sách người dùng để làm mới dữ liệu
            return "redirect:/manage-users";
            
        } catch (Exception e) {
            e.printStackTrace();
            // Nếu vướng lỗi ngoại lệ, vẫn an toàn quay về trang danh sách để tránh treo trang
            return "redirect:/manage-users";
        }
    }
}