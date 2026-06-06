package com.nhakhoa.controller;

import com.nhakhoa.model.Contact;
import com.nhakhoa.model.User;
import com.nhakhoa.service.ContactService; // Import Service mới
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.SessionAttribute;
import jakarta.servlet.http.HttpSession;

import java.util.List;

@Controller
public class ContactController {

    @Autowired
    private ContactService contactService; // Sử dụng Service thay vì DAO

    // ── 1. GET: Admin xem danh sách liên hệ ──────────────────────────────
    @GetMapping("/admin-contacts")
    public String showAdminContacts(Model model) {
        List<Contact> listC = contactService.getAllContacts();
        model.addAttribute("listC", listC);
        model.addAttribute("activePage", "contacts");
        return "admin_contacts";
    }

    // ── 2. GET: Trang form liên hệ ──────────────────────────────────────
    @GetMapping("/send-contact")
    public String showContactPage(Model model) {
        model.addAttribute("activePage", "contact");
        return "contact";
    }

    // ── 3. POST: Xử lý form liên hệ ──────────────────────────────────────
    @PostMapping("/send-contact")
    public String handleSendContact(
            @SessionAttribute(value = "acc", required = false) User acc,
            @RequestParam(value = "name", required = false) String name,
            @RequestParam(value = "email", required = false) String email,
            @RequestParam(value = "message", required = false) String message,
            HttpSession session,
            Model model) {

        // Validate dữ liệu
        if (acc != null) {
            if (name == null || name.isBlank()) name = acc.getFullName();
            if (email == null || email.isBlank()) email = acc.getEmail();
        }

        if (name == null || name.isBlank() || 
            email == null || email.isBlank() || 
            message == null || message.isBlank()) {
            
            model.addAttribute("error", "Vui lòng nhập đầy đủ thông tin liên hệ!");
            model.addAttribute("activePage", "contact");
            return "contact";
        }

        try {
            Integer userId = (acc != null) ? acc.getUserID() : null;

            // Gọi Service để lưu dữ liệu
            int generatedId = contactService.insertContactAndReturnID(userId, name, email, message);
            
            if (generatedId > 0) {
                session.setAttribute("myContactID", generatedId);
                model.addAttribute("success", "Cảm ơn " + name + "! Lời nhắn của bạn đã được gửi.");
            } else {
                model.addAttribute("error", "Có lỗi xảy ra khi gửi lời nhắn.");
            }

        } catch (Exception e) {
            e.printStackTrace();
            model.addAttribute("error", "Lỗi hệ thống: " + e.getMessage());
        }

        model.addAttribute("activePage", "contact");
        return "contact";
    }
}