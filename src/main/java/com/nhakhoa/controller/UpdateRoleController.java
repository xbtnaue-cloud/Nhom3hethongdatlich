package com.nhakhoa.controller;

import com.nhakhoa.dao.UserDAO;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller // Đổi sang Annotation của Spring
public class UpdateRoleController {

    // Thay thế hoàn toàn cho doGet và @WebServlet(urlPatterns = {"/update-role"})
    @GetMapping("/update-role")
    public String updateRole(
            @RequestParam("id") int id,
            @RequestParam("roleID") int roleID) {
        
        try {
            // Gọi UserDAO cũ để thực thi lệnh UPDATE quyền (Role) trong CSDL
            UserDAO dao = new UserDAO();
            dao.updateUserRole(id, roleID);

            // Cập nhật thành công, điều hướng thẳng về lại trang danh sách người dùng
            return "redirect:/manage-users";
            
        } catch (Exception e) {
            e.printStackTrace();
            // Nếu vướng lỗi ngoại lệ, vẫn an toàn quay về trang danh sách
            return "redirect:/manage-users";
        }
    }
}