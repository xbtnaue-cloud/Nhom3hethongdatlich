package com.nhakhoa.controller;

import com.nhakhoa.dao.ServiceDAO;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller // Đổi sang Annotation của Spring
public class DeleteServiceController {

    // Thay thế hoàn toàn cho doGet và @WebServlet(urlPatterns = {"/delete-service"})
    @GetMapping("/delete-service")
    public String deleteService(@RequestParam(value = "id", required = false) Integer id) {
        
        // Kiểm tra phòng hờ nếu URL không truyền id lên (?id=)
        if (id == null) {
            return "redirect:/manage-services?error=deletefail";
        }

        try {
            // Gọi ServiceDAO cũ của ní để xóa
            ServiceDAO dao = new ServiceDAO();
            dao.deleteService(id);
            
            // Xóa xong chuyển hướng về trang danh sách dịch vụ
            return "redirect:/manage-services";
            
        } catch (Exception e) {
            e.printStackTrace();
            // Lỗi (khóa ngoại, mất kết nối DB...) thì văng về kèm param báo lỗi
            return "redirect:/manage-services?error=deletefail";
        }
    }
}