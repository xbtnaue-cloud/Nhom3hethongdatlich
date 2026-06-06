package com.nhakhoa.service;

import com.nhakhoa.dto.AppointmentDTO;
import com.nhakhoa.dto.PatientDTO;
import com.nhakhoa.model.Appointment;
// Không import com.nhakhoa.model.Service ở đây để tránh xung đột
import com.nhakhoa.repository.AppointmentRepository;
import com.nhakhoa.repository.ServiceRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service; // Đây là Annotation
import org.springframework.transaction.annotation.Transactional;
import java.time.LocalDateTime;
import java.sql.Date;
import java.sql.Time;
import java.util.List;
import java.util.stream.Collectors;
import com.nhakhoa.repository.DentistServicesRepository;
import com.nhakhoa.model.Dentist;
import com.nhakhoa.model.DentistServices;
import com.nhakhoa.model.User;

@Service // Spring sẽ nhận diện đây là một Service bean
public class AppointmentService {

    @Autowired
    private AppointmentRepository repo;
    
    
    @Autowired 
    private ServiceRepository serviceRepository;
    
    @Autowired
    private DentistServicesRepository dentistServicesRepository;
    
    @Autowired
    private AppointmentRepository appointmentRepository;
    
    public List<User> getDentistsByService(int serviceID) {
        return dentistServicesRepository.findDentistsByServiceID(serviceID);
    }

    // --- Các hàm cho Booking ---
    // Sử dụng tên đầy đủ để phân biệt với Annotation @Service
    public List<com.nhakhoa.model.Service> getAllServices() { 
        return serviceRepository.findAll(); 
    }
    
    public List<Appointment> findAppointmentsByPhone(String phone) {
        return repo.findByPhoneNumber(phone);
    }
    
 // Thêm vào AppointmentService.java
    public List<Appointment> getAppointmentsByUserId(int userId) {
        return repo.findByPatientID(userId);
    }


    // Trong AppointmentService.java
    public List<PatientDTO> getPatientsByDentist(int dentistID) {
        List<Object[]> results = repo.findDistinctPatientsByDentistID(dentistID);
        
        if (results == null) return new java.util.ArrayList<>();
        
        return results.stream().map(obj -> {
            PatientDTO dto = new PatientDTO();
            // Index 0: ID, 1: Name, 2: Phone, 3: Email
            dto.setUserID(obj[0] != null ? (Integer) obj[0] : 0);
            dto.setFullName(obj[1] != null ? obj[1].toString() : "Khách vãng lai");
            dto.setPhone(obj[2] != null ? obj[2].toString() : "N/A");
            dto.setEmail(obj[3] != null ? obj[3].toString() : "N/A");
            return dto;
        }).collect(Collectors.toList());
    }

    public boolean isDuplicateAppointment(int dentistID, String date, String time) {
        return repo.countByDentistIDAndAppointmentDateAndAppointmentTime(
                dentistID, Date.valueOf(date), Time.valueOf(time + ":00")) > 0;
    }

    public void addAppointment(Integer patientID, int dentistID, int serviceID, String date, String time, String notes, String phone, String name) {
        Appointment app = new Appointment();
        
        // Nếu là khách vãng lai (không có patientID), lưu trực tiếp vào cột
        if (patientID != null) {
            app.setPatientID(patientID);
        } else {
            app.setPhoneNumber(phone); // LƯU VÀO CỘT phoneNumber
            app.setPatientName(name);  // LƯU VÀO CỘT patientName
        }
        
        app.setDentistID(dentistID);
        app.setServiceID(serviceID);
        app.setAppointmentDate(Date.valueOf(date));
        app.setAppointmentTime(Time.valueOf(time + ":00"));
        app.setStatus("Pending");
        app.setNotes(notes);
        
        repo.save(app);
    }
    
 // Thêm vào AppointmentService.java
    public List<Appointment> getAppointmentsByDentistEntity(int dentistID) {
        return repo.findByDentistID(dentistID);
    }

    // --- Các hàm cho Dashboard & Quản lý ---
    public List<AppointmentDTO> getAllAppointments() {
        return repo.findAll().stream().map(this::convertToDTO).collect(Collectors.toList());
    }
    public long countTotalPatients() {
        return repo.countAllUniquePatients();
    }
    
    public long countUniquePatientsByDentist(int dentistID) {
        return repo.countUniquePatientsByDentist(dentistID);
    }
    
    public long countByMonth(int month, int year) { return repo.countByMonth(month, year); }
    public long countByMonthAndDentist(int month, int year, int dId) { return repo.countByMonthAndDentist(month, year, dId); }

    public double getTotalRevenue() { return repo.sumRevenue(); }
    public long countTotalAppointments() { return repo.count(); }
    public long countByStatus(String status) { return repo.countByStatus(status); }
    public int countPatientsByDentist(int dID) { return repo.countDistinctPatientByDentistID(dID); }
    public double getRevenueByDentist(int dID) { return repo.sumRevenueByDentist(dID); }
    public long countAppointmentsByDentist(int dID) { return repo.countByDentistID(dID); }
    public long countByStatusAndDentist(String status, int dID) { return repo.countByStatusAndDentistID(status, dID); }

    // --- Hàm bổ trợ ---
    public AppointmentDTO convertToDTO(Appointment app) {
        AppointmentDTO dto = new AppointmentDTO();
        dto.setAppointmentID(app.getAppointmentID());
        
        if (app.getAppointmentDate() != null) {
            dto.setAppointmentDate(new java.sql.Date(app.getAppointmentDate().getTime()));
        }
        
        dto.setAppointmentTime(app.getAppointmentTime());
        dto.setStatus(app.getStatus());
        dto.setNotes(app.getNotes());
        
        // --- ĐÂY LÀ LOGIC CẦN SỬA ---
        if (app.getPatient() != null) {
            dto.setPatientName(app.getPatient().getFullName());
            dto.setPhoneNumber(app.getPatient().getPhone());
        } else {
            // Nếu không có patient liên kết, lấy từ cột trực tiếp của bảng Appointment
            dto.setPatientName(app.getPatientName() != null ? app.getPatientName() : "Khách vãng lai");
            dto.setPhoneNumber(app.getPhoneNumber() != null ? app.getPhoneNumber() : "N/A");
        }
        
        if (app.getService() != null) {
            dto.setServiceName(app.getService().getServiceName());
        }
        if (app.getDentist() != null) {
            dto.setDentistName(app.getDentist().getFullName());
        }

        return dto;
    }

    private void processGuestNotes(String notes, String patientName, AppointmentDTO dto) {
        if (patientName != null && !patientName.trim().isEmpty()) {
            dto.setPatientName(patientName);
        } else if (notes != null && notes.contains("KHÁCH VÃNG LAI")) {
            try {
                String cleanInfo = notes.split("\\|")[0].trim();
                String temp = cleanInfo.replace("【", "").replace("】", "");
                dto.setPatientName(temp.split(":")[1].split("-")[0].trim());
                dto.setPhoneNumber(temp.split("SĐT:")[1].trim());
            } catch (Exception e) { 
                dto.setPatientName("Khách vãng lai"); 
                dto.setPhoneNumber("N/A"); 
            }
        } else { 
            dto.setPatientName("Khách hàng"); 
        }
    }
    @Transactional(readOnly = true)
    public List<AppointmentDTO> getHistoryByPatientID(Integer patientID) {

        List<Appointment> list =
                repo.findHistoryByPatientID(patientID);

        return list.stream().map(app -> {

            AppointmentDTO dto = new AppointmentDTO();

            dto.setAppointmentDate(app.getAppointmentDate());

            dto.setServiceName(
                app.getService() != null
                    ? app.getService().getServiceName()
                    : "N/A"
            );

            dto.setDentistName(
                app.getDentist() != null
                    ? app.getDentist().getFullName()
                    : "N/A"
            );

            dto.setPrice(
                app.getService() != null
                    ? app.getService().getPrice()
                    : 0
            );

            return dto;

        }).collect(Collectors.toList());
    }
    @Transactional
    public boolean cancelAppointment(int appointmentID, String reason) {
        Appointment app = repo.findById(appointmentID).orElse(null);
        if (app != null) {
            // Kiểm tra điều kiện 24h
            LocalDateTime appointmentDateTime = LocalDateTime.of(
                app.getAppointmentDate().toLocalDate(), 
                app.getAppointmentTime().toLocalTime()
            );
            if (LocalDateTime.now().isAfter(appointmentDateTime.minusHours(24))) {
                return false; // Quá hạn
            }
            app.setStatus("Cancelled");
            String oldNotes = (app.getNotes() != null) ? app.getNotes() : "";
            app.setNotes(oldNotes + (oldNotes.isEmpty() ? "" : " | ") + "Lý do hủy: " + reason);
            repo.save(app);
            return true;
        }
        return false;
    }
 // Thêm vào AppointmentService.java
    public List<AppointmentDTO> getAppointmentsByPhone(String phone) {
        // Gọi repository để lấy danh sách Appointment theo SĐT
        List<Appointment> list = repo.findByPhoneNumber(phone);
        
        // Chuyển đổi từ Appointment sang AppointmentDTO để Controller hiển thị
        return list.stream().map(app -> {
            AppointmentDTO dto = new AppointmentDTO();
            dto.setAppointmentID(app.getAppointmentID());
            dto.setAppointmentDate(new java.sql.Date(app.getAppointmentDate().getTime()));
            dto.setAppointmentTime(app.getAppointmentTime());
            dto.setStatus(app.getStatus());
            
            // SỬA TẠI ĐÂY:
            if (app.getPatient() != null) {
                dto.setPatientName(app.getPatient().getFullName());
                // Thay getPhoneNumber() bằng tên hàm đúng trong class User của bạn
                dto.setPhoneNumber(app.getPatient().getPhone()); 
            } else {
                dto.setPatientName("Khách vãng lai");
                dto.setPhoneNumber("N/A");
            }
            return dto;
        }).collect(Collectors.toList());
    }
    
    @Transactional
    public boolean updateStatus(int id, String status, String reason) {
        Appointment app = appointmentRepository.findById(id).orElse(null);
        if (app == null) return false;

        if ("Confirmed".equals(status)) {
            if (isDuplicateForUpdate(app.getDentistID(), app.getAppointmentDate(), app.getAppointmentTime(), id)) {
                return false;
            }
        }

        app.setStatus(status);
        
        // NẾU LÀ HỦY: chỉ lưu lý do hủy vào Notes, không xóa thông tin khách
        if ("Cancelled".equals(status) && reason != null && !reason.isEmpty()) {
            app.setNotes("Lý do hủy: " + reason); 
        }
        
        appointmentRepository.save(app);
        return true;
    }
    
    public boolean isDuplicateForUpdate(int dentistID, Date date, Time time, int currentAppointmentID) {
        // Sửa tên hàm cho khớp chính xác với khai báo trong Repository ở Bước 1
        List<Appointment> list = appointmentRepository.findByDentistIDAndAppointmentDateAndAppointmentTimeAndStatus(
                dentistID, date, time, "Confirmed");
        
        for (Appointment a : list) {
            if (a.getAppointmentID() != currentAppointmentID) {
                return true; // Tìm thấy lịch khác bị trùng
            }
        }
        return false;
    }
    
 // Thêm vào AppointmentService.java
    public List<AppointmentDTO> getHistoryByPhone(String phone) {
        List<Object[]> results = repo.findHistoryByPhoneNumber(phone);
        
        return results.stream().map(obj -> {
            AppointmentDTO dto = new AppointmentDTO();
            
            dto.setAppointmentID((Integer) obj[0]);
            
            // --- XỬ LÝ NGÀY THÁNG AN TOÀN ---
            if (obj[1] != null) {
                if (obj[1] instanceof java.sql.Date) {
                    dto.setAppointmentDate((java.sql.Date) obj[1]);
                } else if (obj[1] instanceof java.time.LocalDate) {
                    dto.setAppointmentDate(java.sql.Date.valueOf((java.time.LocalDate) obj[1]));
                }
            }
            
            // --- XỬ LÝ THỜI GIAN AN TOÀN ---
            if (obj[2] != null) {
                 if (obj[2] instanceof java.sql.Time) {
                     dto.setAppointmentTime((java.sql.Time) obj[2]);
                 } else if (obj[2] instanceof java.time.LocalTime) {
                     dto.setAppointmentTime(java.sql.Time.valueOf((java.time.LocalTime) obj[2]));
                 }
            }
            
            dto.setStatus((String) obj[3]);
            dto.setServiceName((String) obj[4]);
            dto.setDentistName((String) obj[5]);
            dto.setPhoneNumber((String) obj[6]);
            dto.setPatientName((String) obj[7]);
            dto.setNotes((String) obj[8]);
            dto.setPrice(obj[9] != null ? ((Number) obj[9]).doubleValue() : 0.0);
            return dto;
        }).collect(Collectors.toList());
    }
    
    
    @Transactional
    public void updateService(int id, String name, String desc, double price, String imagePath, String[] doctorIds) {
        // 1. Cập nhật thông tin dịch vụ
        com.nhakhoa.model.Service s = serviceRepository.findById(id).orElseThrow();
        s.setServiceName(name);
        s.setDescription(desc);
        s.setPrice(price);
        if (imagePath != null) s.setServiceImage(imagePath);
        serviceRepository.save(s);

        // 2. XÓA BÁC SĨ CŨ (Quan trọng: Để bỏ tích thì phải xóa hết cái cũ đi trước)
        dentistServicesRepository.deleteByServiceID(id);

        // 3. THÊM MỚI (Nếu có bác sĩ được chọn)
        if (doctorIds != null) {
            for (String dIdStr : doctorIds) {
                int dId = Integer.parseInt(dIdStr);
                DentistServices ds = new DentistServices();
                ds.setServiceID(id);
                ds.setDentistID(dId);
                dentistServicesRepository.save(ds);
            }
        }
    }
    
}
   