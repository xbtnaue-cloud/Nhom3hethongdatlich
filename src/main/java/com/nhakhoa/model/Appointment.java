package com.nhakhoa.model;

import jakarta.persistence.*;
import lombok.*;
import java.sql.Date;
import java.sql.Time;

@Entity
@Table(name = "Appointments")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Appointment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int appointmentID;
    
    @Column(name = "patientID")
    private Integer patientID;
    
    @Column(name = "dentistID")
    private Integer dentistID;
    
    @Column(name = "serviceID")
    private Integer serviceID;
    
    private String patientName;
    private String phoneNumber;
    private Date appointmentDate;
    private Time appointmentTime;
    private String status;
    private String notes;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "patientID", insertable = false, updatable = false)
    private User patient;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "serviceID", insertable = false, updatable = false)
    private Service service;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "dentistID", insertable = false, updatable = false)
    private User dentist;

    // --- LOGIC HIỂN THỊ HỢP LÝ ---
    
    // Ưu tiên lấy từ User (nếu có tài khoản), nếu không lấy từ cột patientName (vãng lai)
    public String getPatientName() {
        if (this.patient != null && this.patient.getFullName() != null) {
            return this.patient.getFullName();
        }
        return (this.patientName != null && !this.patientName.isEmpty()) ? this.patientName : "Khách vãng lai";
    }
    
    public String getPhoneNumber() {
        if (this.patient != null && this.patient.getPhone() != null) {
            return this.patient.getPhone();
        }
        return (this.phoneNumber != null && !this.phoneNumber.isEmpty()) ? this.phoneNumber : "N/A";
    }
    
    public String getDentistName() {
        return (this.dentist != null) ? this.dentist.getFullName() : "Chưa chỉ định";
    }
    
    public String getServiceName() {
        return (this.service != null) ? this.service.getServiceName() : "Khám tổng quát";
    }
}