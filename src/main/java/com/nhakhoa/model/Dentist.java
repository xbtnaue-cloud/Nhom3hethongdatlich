package com.nhakhoa.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "Dentists")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Dentist {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "dentistid") // Phải khớp với tên cột trong DB của bạn
    private int dentistID; // Tự động khớp với dentisid nếu dùng naming strategy

    @Column(name = "full_name") // Sửa thành full_name (chữ thường)
    private String fullName;

    @Column(name = "specialization") // Sửa thành specialization
    private String specialization;

    @Column(name = "phone") // Sửa thành phone
    private String phone;

    @Column(name = "email") // Sửa thành email
    private String email;

    @Column(name = "image") // Sửa thành image
    private String image;
}