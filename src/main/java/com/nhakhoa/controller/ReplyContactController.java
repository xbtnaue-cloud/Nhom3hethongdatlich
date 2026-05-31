package com.nhakhoa.controller;

import com.nhakhoa.dao.ContactDAO;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller // Đổi sang Annotation của Spring
public class ReplyContactController {

    // 1. Thay thế cho doPost: Xử lý khi Admin gửi nội dung phản hồi liên hệ
    @PostMapping("/reply-contact")
    public String replyContact(
            @RequestParam(value = "contactID", required = false) Integer id,
            @RequestParam(value = "reply", required = false) String replyMessage) {

        // Kiểm tra dữ liệu trống (Sử dụng hàm .isBlank() của Java 17 cực kỳ tối ưu)
        if (id != null && replyMessage != null && !replyMessage.isBlank()) {
            try {
                // 2. Gọi ContactDAO cũ của ní để thực thi ghi dữ liệu phản hồi vào DB
                ContactDAO dao = new ContactDAO();
                dao.replyContact(id, replyMessage.trim()); 
                
                // 3. Điều hướng Admin quay lại trang chat (Hệ thống tự động bù Context Path)
                return "redirect:/admin-contacts";
                
            } catch (Exception e) {
                e.printStackTrace();
                return "redirect:/admin-contacts";
            }
        }
        
        // Nếu dữ liệu không hợp lệ hoặc trống, quay lại trang quản lý liên hệ
        return "redirect:/admin-contacts";
    }

    // 2. Thay thế cho doGet: Đề phòng Admin cố tình truy cập URL này bằng phương thức GET
    @GetMapping("/reply-contact")
    public String handleGetRequest() {
        return "redirect:/admin-contacts";
    }
}