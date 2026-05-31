package com.nhakhoa.controller;

import com.nhakhoa.dao.ContactDAO;
import com.nhakhoa.model.ChatMessage;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController // Đổi sang RestController để Spring Boot tự động xuất thẳng JSON ra cho Ajax
public class GetMessageController {

    // Hỗ trợ cả 2 phương thức GET và POST tới endpoint /get-chat tương thích Ajax cũ
    @GetMapping("/get-chat")
    public List<Map<String, Object>> getChatMessagesGet(@RequestParam(value = "id", required = false) Integer id) {
        return loadChatHistory(id);
    }

    @PostMapping("/get-chat")
    public List<Map<String, Object>> getChatMessagesPost(@RequestParam(value = "id", required = false) Integer id) {
        return loadChatHistory(id);
    }

    // Hàm bổ trợ xử lý bốc dữ liệu từ DAO và map sang định dạng JSON khớp frontend cũ
    private List<Map<String, Object>> loadChatHistory(Integer id) {
        List<Map<String, Object>> jsonResponse = new ArrayList<>();

        if (id == null) {
            return jsonResponse; // Trả về mảng rỗng [] ngay nếu không nhận được ID
        }

        try {
            ContactDAO dao = new ContactDAO();
            List<ChatMessage> list = dao.getListMessagesByContactID(id);

            // Tự động map sang cấu trúc key-value khớp 100% tên thuộc tính JSON cũ của ní
            for (ChatMessage m : list) {
                Map<String, Object> messageMap = new HashMap<>();
                messageMap.put("role", m.getSenderRole());      // Khớp với key "role"
                messageMap.put("content", m.getContent());      // Khớp với key "content" (Spring tự dọn dẹp ký tự đặc biệt)
                messageMap.put("time", m.getCreatedAt().toString()); // Khớp với key "time"
                
                jsonResponse.add(messageMap);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return jsonResponse; // Trả về danh sách, Spring Boot tự xuất thành JSON [{}, {}, {}]
    }
}