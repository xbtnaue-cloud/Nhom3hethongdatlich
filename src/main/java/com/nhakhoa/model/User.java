package com.nhakhoa.model;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "Users") // Khớp với tên bảng trong SQL
@Data
@NoArgsConstructor
@AllArgsConstructor
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "UserID") // VIẾT HOA ĐÚNG NHƯ TRONG SQL
    private int userID;

    @Column(name = "Username", nullable = false)
    private String username;

    @Column(name = "Password", nullable = false)
    private String password;

    @Column(name = "FullName") // ĐÚNG VỚI script SQL của bạn
    private String fullName;

    @Column(name = "Email")
    private String email;

    @Column(name = "Phone")
    private String phone;

    @Column(name = "RoleID") // ĐÚNG VỚI script SQL
    private int roleID;

    @Column(name = "Specialty")
    private String specialty;

    @Column(name = "StatusID") // ĐÚNG VỚI script SQL
    private int statusID;
}