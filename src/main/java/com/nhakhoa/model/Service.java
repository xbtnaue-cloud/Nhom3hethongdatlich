package com.nhakhoa.model;

public class Service {
    private int serviceID;
    private String serviceName;
    private String description;
    private double price;
    private String doctorIdsJson;
    private String doctorNames;
    private String serviceImage;
    public Service() {
    }

    public Service(int serviceID, String serviceName, String description, double price, String doctorIdsJson) {
        this.serviceID = serviceID;
        this.serviceName = serviceName;
        this.description = description;
        this.price = price;
    }

    // Getter và Setter
    public int getServiceID() { return serviceID; }
    public void setServiceID(int serviceID) { this.serviceID = serviceID; }

    public String getServiceName() { return serviceName; }
    public void setServiceName(String serviceName) { this.serviceName = serviceName; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public double getPrice() { return price; }
    public void setPrice(double price) { this.price = price; }
    
    public String getDoctorIdsJson() { return doctorIdsJson; }
    public void setDoctorIdsJson(String doctorIdsJson) { this.doctorIdsJson = doctorIdsJson; }
    
    public String getDoctorNames() { return doctorNames; }
    public void setDoctorNames(String doctorNames) { this.doctorNames = doctorNames; }
    
    public String getServiceImage() { return serviceImage; }
    public void   setServiceImage(String serviceImage) { this.serviceImage = serviceImage; }
}