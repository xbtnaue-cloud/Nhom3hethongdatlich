package com.nhakhoa.model;

import jakarta.persistence.*;
import lombok.Data;
import java.sql.Timestamp;

@Entity
@Table(name = "Contacts")
@Data // Tự động tạo Getter, Setter, toString...
public class Contact {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int contactID;

    @Column(name = "FullName")
    private String fullName;

    @Column(name = "Email")
    private String email;

    @Column(name = "Message")
    private String message;

    @Column(name = "Status")
    private String status;

    @Column(name = "CreatedAt")
    private Timestamp createdAt;

    @Column(name = "ReplyMessage")
    private String replyMessage;

    @Column(name = "UserID")
    private Integer userID;

    // Các trường không có trong bảng Contacts, dùng để hiển thị dữ liệu từ JOIN
    @Transient 
    private String username;

    // Constructor không đối số (bắt buộc cho JPA)
    public Contact() {}
}