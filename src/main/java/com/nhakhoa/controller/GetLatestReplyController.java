package com.nhakhoa.controller;

import com.nhakhoa.model.ChatMessage;
import com.nhakhoa.service.ContactService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.SessionAttribute;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
public class GetLatestReplyController {

    @Autowired
    private ContactService contactService; // Inject Service thay vì new DAO

    @RequestMapping(value = "/get-latest-reply", method = {RequestMethod.GET, RequestMethod.POST})
    public List<Map<String, Object>> getLatestReply(
            @SessionAttribute(value = "myContactID", required = false) Integer sessionContactID,
            @RequestParam(value = "id", required = false) Integer paramContactID) {

        Integer contactID = (sessionContactID != null) ? sessionContactID : paramContactID;
        List<Map<String, Object>> jsonResponse = new ArrayList<>();

        if (contactID != null) {
            try {
                // Gọi Service thay vì DAO
                List<ChatMessage> list = contactService.getListMessagesByContactID(contactID);

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
        }

        return jsonResponse; 
    }
}