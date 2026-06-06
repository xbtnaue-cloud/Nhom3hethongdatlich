package com.nhakhoa.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "DentistServices")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class DentistServices {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;
    
    private int serviceID;
    private int dentistID;
    
 // PHẢI CÓ CONSTRUCTOR TRỐNG (ĐỂ HIBERNATE HOẠT ĐỘNG)
    

    // CÁC HÀM GETTER VÀ SETTER (Đây là thứ đang làm code của bạn bị đỏ)
    public int getServiceID() { return serviceID; }
    public void setServiceID(int serviceID) { this.serviceID = serviceID; }

    public int getDentistID() { return dentistID; }
    public void setDentistID(int dentistID) { this.dentistID = dentistID; }

    @ManyToOne
    @JoinColumn(name = "DentistID", nullable = false) // Khớp với cột DentistID trong bảng
    private User dentist; // Thay đổi từ Dentist sang User

    @ManyToOne
    @JoinColumn(name = "ServiceID", nullable = false)
    private Service service;
}