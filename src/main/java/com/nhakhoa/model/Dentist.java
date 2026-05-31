package com.nhakhoa.model;

public class Dentist {
    private int dentistID;
    private String fullName;
    private String specialization; // Chuyên khoa (như Niềng răng, Nhổ răng...)
    private String phone;
    private String email;
    private String image; // Đường dẫn ảnh đại diện bác sĩ

    // 1. Constructor không đối số
    public Dentist() {
    }

    // 2. Constructor có đối số
    public Dentist(int dentistID, String fullName, String specialization, String phone, String email, String image) {
        this.dentistID = dentistID;
        this.fullName = fullName;
        this.specialization = specialization;
        this.phone = phone;
        this.email = email;
        this.image = image;
    }

    // 3. Getters and Setters
    public int getDentistID() {
        return dentistID;
    }

    public void setDentistID(int dentistID) {
        this.dentistID = dentistID;
    }

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public String getSpecialization() {
        return specialization;
    }

    public void setSpecialization(String specialization) {
        this.specialization = specialization;
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

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    // 4. toString
    @Override
    public String toString() {
        return "Dentist{" + "fullName=" + fullName + ", spec=" + specialization + '}';
    }
}