package com.nhakhoa.controller;

import com.nhakhoa.dao.UserDAO;
import com.nhakhoa.model.User;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.List;

@Controller // Đổi sang Annotation của Spring
public class ManageUserController {

    // Thay thế hoàn toàn cho doGet và @WebServlet(urlPatterns = {"/manage-users"})
    @GetMapping("/manage-users")
    public String manageUsers(Model model) {
        try {
            UserDAO dao = new UserDAO();
            
            // Lấy toàn bộ danh sách User từ Database thông qua DAO cũ của ní
            List<User> list = dao.getAllUsers();
            
            // Đẩy dữ liệu vào Model (Khớp 100% tên biến listU cũ)
            model.addAttribute("listU", list);
            
            // Đánh dấu active để sáng đèn menu Sidebar "Người dùng" bên Admin
            model.addAttribute("activePage", "users");
            
            // Mở file giao diện admin-users.html trong thư mục src/main/resources/templates/
            return "admin-users";
            
        } catch (Exception e) {
            e.printStackTrace();
            // Nếu có lỗi, đẩy thông báo sang trang error.html phòng sập hệ thống
            model.addAttribute("error", "Lỗi tải danh sách người dùng: " + e.getMessage());
            return "error";
        }
    }
}