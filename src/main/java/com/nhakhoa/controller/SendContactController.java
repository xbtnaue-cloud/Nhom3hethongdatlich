package com.nhakhoa.controller;

import com.nhakhoa.model.User;
import com.nhakhoa.service.ContactService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import jakarta.servlet.http.HttpSession;

@Controller
public class SendContactController {

    @Autowired
    private ContactService contactService; // Sử dụng Service chuẩn

    // ── 1. POST: Xử lý gửi tin nhắn Chat qua Ajax ─────────────────────────────
    @PostMapping("/send-contact-ajax")
    @ResponseBody 
    public String sendContactAjax(
            @RequestParam(value = "message", required = false) String message,
            HttpSession session) { // Bỏ @SessionAttribute, dùng HttpSession trực tiếp

        if (message == null || message.isBlank()) {
            return "empty";
        }

        // Lấy thông tin user từ session một cách an toàn
        User acc = (User) session.getAttribute("acc");
        Integer contactID = (Integer) session.getAttribute("myContactID");

        try {
            if (contactID == null) {
                // Khách chưa từng chat trong session này
                Integer userId = (acc != null) ? acc.getUserID() : null;
                String name = (acc != null) ? acc.getFullName() : "Khách vãng lai";
                String email = (acc != null) ? acc.getEmail() : "guest@dhkna.edu.vn";

                contactID = contactService.insertContactAndReturnID(userId, name, email, message.trim());
                
                if (contactID != null && contactID > 0) {
                    session.setAttribute("myContactID", contactID);
                }
            } else {
                // Khách đang chat tiếp
                contactService.insertChatMessage(contactID, "Patient", message.trim());
            }
            
            return "success";
        } catch (Exception e) {
            e.printStackTrace();
            return "error";
        }
    }

    // ── 2. GET: Chặn truy cập trực tiếp ─────────────────────────────────────────
    @GetMapping("/send-contact-ajax")
    public String handleGetRequest() {
        return "redirect:/index";
    }
}