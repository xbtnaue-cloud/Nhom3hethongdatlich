package com.nhakhoa.dto;

public class PatientDTO {
	private int userID;
    private String fullName;
    private String phone;
    private String email;

    // Constructor mặc định
    public PatientDTO() {}

    // Constructor đầy đủ
    public PatientDTO(String fullName, String phone, String email) {
        this.fullName = fullName;
        this.phone = phone;
        this.email = email;
    }

    // Getters và Setters
    public String getFullName() { return fullName; }
    public void setFullName(String fullName) { this.fullName = fullName; }

    public String getPhone() { return phone; }
    public void setPhone(String phone) { this.phone = phone; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }
    
    public int getUserID() { return userID; }
    public void setUserID(int userID) { this.userID = userID; }
}