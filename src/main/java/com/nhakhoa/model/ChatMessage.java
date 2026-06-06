package com.nhakhoa.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.sql.Timestamp;

@Entity
@Table(name = "ChatMessages") // Tên bảng trong DB
@Data // Tự động sinh Getter, Setter, toString, etc.
@NoArgsConstructor // Tạo constructor không tham số
@AllArgsConstructor // Tạo constructor đầy đủ tham số
public class ChatMessage {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int messageID;

    @Column(name = "ContactID")
    private int contactID;

    @Column(name = "SenderRole")
    private String senderRole; // 'Patient' hoặc 'Doctor'

    @Column(name = "Content")
    private String content;

    @Column(name = "CreatedAt")
    private Timestamp createdAt;
}