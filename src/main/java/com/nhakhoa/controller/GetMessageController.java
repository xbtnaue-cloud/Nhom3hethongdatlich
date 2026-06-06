package com.nhakhoa.controller;

import com.nhakhoa.model.ChatMessage;
import com.nhakhoa.service.ContactService; // Import Service
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
public class GetMessageController {

    @Autowired
    private ContactService contactService; // Sử dụng Service thay vì DAO

    @GetMapping("/get-chat")
    public List<Map<String, Object>> getChatMessagesGet(@RequestParam(value = "id", required = false) Integer id) {
        return loadChatHistory(id);
    }

    @PostMapping("/get-chat")
    public List<Map<String, Object>> getChatMessagesPost(@RequestParam(value = "id", required = false) Integer id) {
        return loadChatHistory(id);
    }

    private List<Map<String, Object>> loadChatHistory(Integer id) {
        List<Map<String, Object>> jsonResponse = new ArrayList<>();

        if (id == null) {
            return jsonResponse;
        }

        try {
            // Gọi qua Service
            List<ChatMessage> list = contactService.getListMessagesByContactID(id);

            for (ChatMessage m : list) {
                Map<String, Object> messageMap = new HashMap<>();
                messageMap.put("role", m.getSenderRole());
                messageMap.put("content", m.getContent());
                messageMap.put("time", m.getCreatedAt() != null ? m.getCreatedAt().toString() : "");
                
                jsonResponse.add(messageMap);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return jsonResponse;
    }
}