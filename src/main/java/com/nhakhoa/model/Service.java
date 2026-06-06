package com.nhakhoa.model;

import jakarta.persistence.*; // Sử dụng jakarta cho Spring Boot 3.x trở lên

@Entity
@Table(name = "Services") // Khớp chính xác với tên bảng trong DB của bạn
public class Service {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY) // Tự động tăng ID
    private int serviceID;

    @Column(name = "ServiceName")
    private String serviceName;

    @Column(name = "Description")
    private String description;

    @Column(name = "Price")
    private double price;

    @Column(name = "ServiceImage")
    private String serviceImage;

    // Các trường không có trong bảng Services (để dùng trong kết quả query JOIN)
    @Transient 
    private String doctorIdsJson;
    
    @Transient
    private String doctorNames;

    public Service() {}

    // Getter và Setter (giữ nguyên hoặc dùng Lombok @Data để đỡ phải viết)
    public int getServiceID() { return serviceID; }
    public void setServiceID(int serviceID) { this.serviceID = serviceID; }

    public String getServiceName() { return serviceName; }
    public void setServiceName(String serviceName) { this.serviceName = serviceName; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public double getPrice() { return price; }
    public void setPrice(double price) { this.price = price; }

    public String getServiceImage() { return serviceImage; }
    public void setServiceImage(String serviceImage) { this.serviceImage = serviceImage; }

    public String getDoctorIdsJson() { return doctorIdsJson; }
    public void setDoctorIdsJson(String doctorIdsJson) { this.doctorIdsJson = doctorIdsJson; }

    public String getDoctorNames() { return doctorNames; }
    public void setDoctorNames(String doctorNames) { this.doctorNames = doctorNames; }
}