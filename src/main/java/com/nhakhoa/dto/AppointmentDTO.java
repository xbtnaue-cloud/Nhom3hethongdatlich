package com.nhakhoa.dto;

import lombok.Data; // Đảm bảo đã import Lombok
import java.sql.Date;
import java.sql.Time;

@Data // Annotation này tự động tạo Getter, Setter, toString, v.v.
public class AppointmentDTO {
    private int appointmentID;
    private Date appointmentDate;
    private Time appointmentTime;
    private String status;
    private String notes;
    private String serviceName;
    private String dentistName;
    private double price;
    
    // Đảm bảo có các trường này để Service gọi được
    private String patientName; 
    private String phoneNumber;
    public double getPrice() {
        return price;
    }

    public void setPrice(double price) {
        this.price = price;
    }
}