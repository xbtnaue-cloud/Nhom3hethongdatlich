package com.nhakhoa.model;

import java.sql.Date;
import java.sql.Time;

public class Appointment {
    private int appointmentID;
    private int patientID;
    private int dentistID;
    private Date appointmentDate;
    private Time appointmentTime;
    private String status; // Pending, Confirmed, Cancelled, Completed
    private String notes;

    // --- THUỘC TÍNH BỔ SUNG ĐỂ HIỂN THỊ (Logic JOIN SQL) ---
    private String patientName;
    private String phoneNumber;
    private String dentistName;
    private String serviceName;
    private double price;
    public Appointment() {
    }

    // Constructor gốc (Dùng cho các logic cơ bản)
    public Appointment(int appointmentID, int patientID, int dentistID, Date appointmentDate, Time appointmentTime, String status, String notes) {
        this.appointmentID = appointmentID;
        this.patientID = patientID;
        this.dentistID = dentistID;
        this.appointmentDate = appointmentDate;
        this.appointmentTime = appointmentTime;
        this.status = status;
        this.notes = notes;
    }

    // Constructor đầy đủ (Dùng khi lấy dữ liệu cho Dashboard Admin)
    public Appointment(int appointmentID, int patientID, int dentistID, Date appointmentDate, Time appointmentTime, String status, String notes, String patientName, String phoneNumber, String dentistName, String serviceName) {
        this.appointmentID = appointmentID;
        this.patientID = patientID;
        this.dentistID = dentistID;
        this.appointmentDate = appointmentDate;
        this.appointmentTime = appointmentTime;
        this.status = status;
        this.notes = notes;
        this.patientName = patientName;
        this.phoneNumber = phoneNumber;
        this.dentistName = dentistName;
        this.serviceName = serviceName;
    }

    // --- GETTERS AND SETTERS CHO CÁC BIẾN MỚI ---
    public String getPatientName() { return patientName; }
    public void setPatientName(String patientName) { this.patientName = patientName; }

    public String getPhoneNumber() { return phoneNumber; }
    public void setPhoneNumber(String phoneNumber) { this.phoneNumber = phoneNumber; }

    public String getDentistName() { return dentistName; }
    public void setDentistName(String dentistName) { this.dentistName = dentistName; }

    public String getServiceName() { return serviceName; }
    public void setServiceName(String serviceName) { this.serviceName = serviceName; }

    // --- GIỮ NGUYÊN CÁC GETTER/SETTER CŨ CỦA BẠN ---
    public int getAppointmentID() { return appointmentID; }
    public void setAppointmentID(int appointmentID) { this.appointmentID = appointmentID; }

    public int getPatientID() { return patientID; }
    public void setPatientID(int patientID) { this.patientID = patientID; }

    public int getDentistID() { return dentistID; }
    public void setDentistID(int dentistID) { this.dentistID = dentistID; }

    public Date getAppointmentDate() { return appointmentDate; }
    public void setAppointmentDate(Date appointmentDate) { this.appointmentDate = appointmentDate; }

    public Time getAppointmentTime() { return appointmentTime; }
    public void setAppointmentTime(Time appointmentTime) { this.appointmentTime = appointmentTime; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public String getNotes() { return notes; }
    public void setNotes(String notes) { this.notes = notes; }
    
    public double getPrice() { return price; }
    public void setPrice(double price) { this.price = price; }
    
}