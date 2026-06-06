package com.nhakhoa.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.util.Date;

@Entity
@Table(name = "Patients") // Đảm bảo tên bảng khớp với DB của bạn
@Data // Tự động sinh Getter, Setter, toString, equals, hashCode
@NoArgsConstructor // Tạo constructor không tham số (bắt buộc cho JPA)
@AllArgsConstructor // Tạo constructor đầy đủ tham số
public class Patient {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int patientID;

    @Column(name = "FullName")
    private String fullName;

    @Column(name = "Phone")
    private String phone;

    @Column(name = "Email")
    private String email;

    @Column(name = "Address")
    private String address;

    @Column(name = "DateOfBirth")

    private Date dateOfBirth;

    @Column(name = "Gender")
    private String gender;
}