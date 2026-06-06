package com.nhakhoa.controller;

import com.nhakhoa.service.ContactService; // Import Service mới
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
public class ReplyContactController {

    @Autowired
    private ContactService contactService; // Inject Service thay vì new DAO

    // 1. Xử lý phản hồi liên hệ
    @PostMapping("/reply-contact")
    public String replyContact(
            @RequestParam(value = "contactID", required = false) Integer id,
            @RequestParam(value = "reply", required = false) String replyMessage) {

        if (id != null && replyMessage != null && !replyMessage.isBlank()) {
            try {
                // Gọi Service thay vì DAO
                contactService.replyContact(id, replyMessage.trim()); 
                return "redirect:/admin-contacts";
            } catch (Exception e) {
                e.printStackTrace();
                return "redirect:/admin-contacts";
            }
        }
        return "redirect:/admin-contacts";
    }

    // 2. Chặn truy cập GET
    @GetMapping("/reply-contact")
    public String handleGetRequest() {
        return "redirect:/admin-contacts";
    }
}