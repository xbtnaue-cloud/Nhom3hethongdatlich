package com.nhakhoa.controller;

import com.nhakhoa.dao.ContactDAO;
import com.nhakhoa.model.Contact;
import com.nhakhoa.model.User;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.SessionAttribute;
import jakarta.servlet.http.HttpSession;

import java.util.List;

@Controller // Đổi sang Annotation của Spring
public class ContactController {

    // ── 1. GET: Admin xem danh sách liên hệ ──────────────────────────────
    @GetMapping("/admin-contacts")
    public String showAdminContacts(Model model) {
        ContactDAO dao = new ContactDAO();
        List<Contact> listC = dao.getAllContacts();

        model.addAttribute("listC", listC);
        model.addAttribute("activePage", "contacts"); // Để sáng đèn menu Sidebar Admin
        
        return "admin_contacts"; // Mở file admin_contacts.html trong templates
    }

    // ── 2. GET: Khách truy cập trực tiếp url bằng GET thì trả về trang form ──
    @GetMapping("/send-contact")
    public String showContactPage(Model model) {
        model.addAttribute("activePage", "contact");
        return "contact"; // Mở file contact.html trong templates
    }

    // ── 3. POST: Xử lý dữ liệu khi người dùng gửi form liên hệ ──────────────
    @PostMapping("/send-contact")
    public String handleSendContact(
            @SessionAttribute(value = "acc", required = false) User acc,
            @RequestParam(value = "name", required = false) String name,
            @RequestParam(value = "email", required = false) String email,
            @RequestParam(value = "message", required = false) String message,
            HttpSession session, // Vẫn giữ HttpSession để lưu myContactID cho chatbot nhận diện
            Model model) {

        // Kiểm tra Session để điền tự động thông tin nếu khách đã đăng nhập
        if (acc != null) {
            if (name == null || name.isBlank()) name = acc.getFullName();
            if (email == null || email.isBlank()) email = acc.getEmail();
        }

        // Kiểm tra dữ liệu trống (Validation bằng hàm có sẵn của Java 17)
        if (name == null || name.isBlank() || 
            email == null || email.isBlank() || 
            message == null || message.isBlank()) {
            
            model.addAttribute("error", "Vui lòng nhập đầy đủ thông tin liên hệ!");
            model.addAttribute("activePage", "contact");
            return "contact";
        }

        try {
            Integer userId = (acc != null) ? acc.getUserID() : null;

            // Gọi DAO cũ để lưu vào Database
            ContactDAO dao = new ContactDAO();
            int generatedId = dao.insertContactAndReturnID(userId, name, email, message);
            
            if (generatedId > 0) {
                // Lưu ID cuộc hội thoại vào session để chatbot nhận diện giống logic cũ
                session.setAttribute("myContactID", generatedId);
                model.addAttribute("success", "Cảm ơn " + name + "! Lời nhắn của bạn đã được gửi. Chúng tôi sẽ phản hồi sớm nhất.");
            } else {
                model.addAttribute("error", "Có lỗi xảy ra khi gửi lời nhắn. Vui lòng thử lại!");
            }

        } catch (Exception e) {
            e.printStackTrace();
            model.addAttribute("error", "Lỗi hệ thống: " + e.getMessage());
        }

        model.addAttribute("activePage", "contact");
        return "contact";
    }
}