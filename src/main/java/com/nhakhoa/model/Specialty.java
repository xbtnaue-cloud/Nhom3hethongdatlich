package com.nhakhoa.model;

import jakarta.persistence.*;
import lombok.Data;

@Entity
@Table(name = "Specialties")
@Data
public class Specialty {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int specialtyID;

    @Column(name = "SpecialtyName")
    private String specialtyName;
}