package com.nhakhoa.service;

import com.nhakhoa.model.Contact;
import com.nhakhoa.model.ChatMessage;
import com.nhakhoa.repository.ContactRepository;
import com.nhakhoa.repository.ChatMessageRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class ContactService {

    @Autowired
    private ContactRepository contactRepo;
    
    @Autowired
    private ChatMessageRepository chatRepo;

    @Transactional
    public int insertContactAndReturnID(Integer userId, String name, String email, String msg) {
        Contact contact = new Contact();
        contact.setUserID(userId);
        contact.setFullName(name);
        contact.setEmail(email);
        contact.setMessage(msg);
        contact.setStatus("Pending");
        
        Contact savedContact = contactRepo.save(contact);
        insertChatMessage(savedContact.getContactID(), "Patient", msg);
        return savedContact.getContactID();
    }

    public void insertChatMessage(int contactID, String role, String content) {
        ChatMessage msg = new ChatMessage();
        msg.setContactID(contactID);
        msg.setSenderRole(role);
        msg.setContent(content);
        chatRepo.save(msg);
        
        if ("Patient".equals(role)) {
            updateStatus(contactID, "Pending");
        }
    }

    @Transactional
    public void replyContact(int id, String reply) {
        insertChatMessage(id, "Doctor", reply);
        updateStatus(id, "Replied");
    }

    public void updateStatus(int id, String status) {
        contactRepo.findById(id).ifPresent(c -> {
            c.setStatus(status);
            contactRepo.save(c);
        });
    }

    public List<Contact> getAllContacts() {
        return contactRepo.findAllByOrderByCreatedAtDesc();
    }

    public List<ChatMessage> getListMessagesByContactID(int contactID) {
        return chatRepo.findByContactIDOrderByCreatedAtAsc(contactID);
    }
    
    public int countNewMessages(int contactID) {
        return chatRepo.countByContactIDAndSenderRole(contactID, "Doctor");
    }
    
    public void deleteContact(int id) {
        contactRepo.deleteById(id);
    }
}