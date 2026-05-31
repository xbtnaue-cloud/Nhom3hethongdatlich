package com.nhakhoa.controller;

import com.nhakhoa.dao.ServiceDAO;
import com.nhakhoa.dao.UserDAO;
import com.nhakhoa.model.Service;
import com.nhakhoa.model.User;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;

import java.util.List;

@Controller // Đổi sang Annotation của Spring
public class ManageServiceController {

    // Thay thế hoàn toàn cho doGet và @WebServlet(urlPatterns = {"/manage-services"})
    @GetMapping("/manage-services")
    public String manageServices(Model model) {
        
        // 1. Khởi tạo các DAO cần thiết
        ServiceDAO serviceDao = new ServiceDAO();
        UserDAO userDao = new UserDAO(); 
        
        try {
            // 2. Lấy danh sách dịch vụ (listS) để hiển thị lên bảng
            List<Service> listS = serviceDao.getAllServices();
            
            // 3. Lấy danh sách bác sĩ (listD) phục vụ cho Checkbox ở Modal Add/Edit
            List<User> listD = userDao.getAllDoctors(); 
            
            // 4. Đẩy dữ liệu vào Spring Model thay vì HttpServletRequest
            model.addAttribute("listS", listS); // Danh sách dịch vụ cho bảng
            model.addAttribute("listD", listD); // Danh sách bác sĩ cho Checkbox
            
            // 5. Đánh dấu menu đang active cho sidebar Admin sáng đèn
            model.addAttribute("activePage", "services");

            // 6. Mở file giao diện services.html nằm trong thư mục templates
            return "services";
            
        } catch (Exception e) {
            e.printStackTrace();
            // Nếu lỗi, đẩy thông điệp lỗi và đá sang trang error.html
            model.addAttribute("error", "Lỗi khi tải dữ liệu dịch vụ: " + e.getMessage());
            return "error";
        }
    }

    // Map thêm phương thức POST trỏ về chung logic như doGet cũ của ní
    @PostMapping("/manage-services")
    public String manageServicesPost(Model model) {
        return manageServices(model);
    }
}