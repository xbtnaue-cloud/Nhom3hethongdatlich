package com.nhakhoa.model;

import java.sql.Timestamp;

public class ChatMessage {
    private int messageID;
    private int contactID;
    private String senderRole; // 'Patient' hoặc 'Doctor'
    private String content;
    private Timestamp createdAt;

    public ChatMessage() {
    }

    // Constructor đầy đủ
    public ChatMessage(int messageID, int contactID, String senderRole, String content, Timestamp createdAt) {
        this.messageID = messageID;
        this.contactID = contactID;
        this.senderRole = senderRole;
        this.content = content;
        this.createdAt = createdAt;
    }

    // Getter và Setter (Bắt buộc phải có để DAO và Servlet gọi được)
    public int getMessageID() { return messageID; }
    public void setMessageID(int messageID) { this.messageID = messageID; }

    public int getContactID() { return contactID; }
    public void setContactID(int contactID) { this.contactID = contactID; }

    public String getSenderRole() { return senderRole; }
    public void setSenderRole(String senderRole) { this.senderRole = senderRole; }

    public String getContent() { return content; }
    public void setContent(String content) { this.content = content; }

    public Timestamp getCreatedAt() { return createdAt; }
    public void setCreatedAt(Timestamp createdAt) { this.createdAt = createdAt; }
}