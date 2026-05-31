package com.nhakhoa.model;

import java.util.Date;

public class Patient {
    private int patientID;
    private String fullName;
    private String phone;
    private String email;
    private String address;
    private Date dateOfBirth;
    private String gender;

    // 1. Constructor không đối số
    public Patient() {
    }

    // 2. Constructor có đối số (Dùng để lấy dữ liệu từ Database lên)
    public Patient(int patientID, String fullName, String phone, String email, String address, Date dateOfBirth, String gender) {
        this.patientID = patientID;
        this.fullName = fullName;
        this.phone = phone;
        this.email = email;
        this.address = address;
        this.dateOfBirth = dateOfBirth;
        this.gender = gender;
    }

    // 3. Getters and Setters
    public int getPatientID() {
        return patientID;
    }

    public void setPatientID(int patientID) {
        this.patientID = patientID;
    }

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public Date getDateOfBirth() {
        return dateOfBirth;
    }

    public void setDateOfBirth(Date dateOfBirth) {
        this.dateOfBirth = dateOfBirth;
    }

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    // 4. toString để kiểm tra dữ liệu
    @Override
    public String toString() {
        return "Patient{" + "fullName=" + fullName + ", phone=" + phone + '}';
    }
}