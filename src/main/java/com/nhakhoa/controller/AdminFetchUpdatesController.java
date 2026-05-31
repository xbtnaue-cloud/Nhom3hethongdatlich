package com.nhakhoa.controller;

import com.nhakhoa.dao.ContactDAO;
import com.nhakhoa.model.Contact;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController // 1. Dùng RestController để Spring tự động convert dữ liệu trả về thành JSON
public class AdminFetchUpdatesController {

    // Xử lý chung cho cả yêu cầu GET và POST tới endpoint /admin-fetch-updates
    @GetMapping("/admin-fetch-updates")
    public List<Contact> fetchUpdatesGet() {
        return getContactList();
    }

    @PostMapping("/admin-fetch-updates")
    public List<Contact> fetchUpdatesPost() {
        return getContactList();
    }

    // Hàm bổ trợ để gọi sang DAO lấy dữ liệu
    private List<Contact> getContactList() {
        try {
            ContactDAO dao = new ContactDAO();
            return dao.getAllContacts(); // Trả thẳng List về, Spring tự chuyển thành mảng JSON []
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
}