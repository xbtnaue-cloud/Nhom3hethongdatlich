package com.nhakhoa.controller;

import com.nhakhoa.dao.ContactDAO;
import com.nhakhoa.model.ChatMessage;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.SessionAttribute;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController // Đổi sang RestController để tự động xuất JSON
public class GetLatestReplyController {

    // Nhận cả GET và POST tới endpoint /get-latest-reply giống urlPatterns cũ
    @RequestMapping(value = "/get-latest-reply", method = {RequestMethod.GET, RequestMethod.POST})
    public List<Map<String, Object>> getLatestReply(
            @SessionAttribute(value = "myContactID", required = false) Integer sessionContactID,
            @RequestParam(value = "id", required = false) Integer paramContactID) {

        // 1. Lấy ID cuộc trò chuyện (Ưu tiên từ Session, nếu null thì lấy từ Parameter)
        Integer contactID = (sessionContactID != null) ? sessionContactID : paramContactID;

        // Tạo danh sách kết quả chứa các Map tương đương mảng [{}, {}] trong JSON
        List<Map<String, Object>> jsonResponse = new ArrayList<>();

        if (contactID != null) {
            try {
                ContactDAO dao = new ContactDAO();
                List<ChatMessage> list = dao.getListMessagesByContactID(contactID);

                // 2. Map dữ liệu sang cấu trúc Key-Value chuẩn (Khớp 100% key cũ của frontend)
                for (ChatMessage m : list) {
                    Map<String, Object> messageMap = new HashMap<>();
                    messageMap.put("role", m.getSenderRole());    // Khớp với "role" cũ
                    messageMap.put("content", m.getContent());    // Khớp với "content" cũ (Jackson tự xử lý ký tự đặc biệt)
                    messageMap.put("time", m.getCreatedAt().toString()); // Khớp với "time" cũ
                    
                    jsonResponse.add(messageMap);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        // Nếu jsonResponse rỗng, Spring tự động trả về mảng rỗng "[]" chuẩn chỉnh
        return jsonResponse; 
    }
}