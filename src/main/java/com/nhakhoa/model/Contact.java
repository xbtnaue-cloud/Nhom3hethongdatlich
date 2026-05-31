package com.nhakhoa.model;

import java.sql.Timestamp;

public class Contact {
    private int contactID;
    private String fullName;
    private String email;
    private String message;
    private String status;
    private Timestamp createdAt;
    private String replyMessage;
    private int userID;    // ID tài khoản (nếu có)
    private String username; // Tên đăng nhập để hiển thị cho Admin biết
    public String getReplyMessage() { return replyMessage; }
    // --- Constructor không đối số ---
    public Contact() {
    }

    // --- Constructor đầy đủ đối số (Tùy chọn) ---
    public Contact(int contactID, String fullName, String email, String message, String status, Timestamp createdAt) {
        this.contactID = contactID;
        this.fullName = fullName;
        this.email = email;
        this.message = message;
        this.status = status;
        this.createdAt = createdAt;
    }

    // --- GETTER VÀ SETTER (Cực kỳ quan trọng để hết lỗi đỏ) ---

    public int getContactID() {
        return contactID;
    }

    public void setContactID(int contactID) {
        this.contactID = contactID;
    }

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }
    
    public int getUserID() { return userID; }
    public void setUserID(int userID) { this.userID = userID; }
    
    public String getUsername() { return username; }
    public void setUsername(String username) { this.username = username; }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public Timestamp getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Timestamp createdAt) {
        this.createdAt = createdAt;
    }
    public void setReplyMessage(String replyMessage) { this.replyMessage = replyMessage; }
}