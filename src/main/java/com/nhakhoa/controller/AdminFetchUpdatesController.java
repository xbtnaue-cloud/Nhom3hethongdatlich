package com.nhakhoa.controller;

import com.nhakhoa.model.Contact;
import com.nhakhoa.service.ContactService; // Import Service
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Collections;
import java.util.List;

@RestController
public class AdminFetchUpdatesController {

    @Autowired
    private ContactService contactService; // Sử dụng Service thay vì DAO

    @GetMapping("/admin-fetch-updates")
    public List<Contact> fetchUpdatesGet() {
        return getContactList();
    }

    @PostMapping("/admin-fetch-updates")
    public List<Contact> fetchUpdatesPost() {
        return getContactList();
    }

    private List<Contact> getContactList() {
        try {
            // Gọi Service để lấy danh sách liên hệ
            return contactService.getAllContacts();
        } catch (Exception e) {
            e.printStackTrace();
            // Trả về danh sách rỗng nếu có lỗi thay vì null để frontend không bị crash
            return Collections.emptyList();
        }
    }
}