package com.nhakhoa.repository;

import com.nhakhoa.model.Appointment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import com.nhakhoa.model.User;
import java.sql.Date; // Bổ sung import
import java.sql.Time; // Bổ sung import
import java.util.List;

@Repository
public interface AppointmentRepository extends JpaRepository<Appointment, Integer> {

    // --- Các hàm hiện có ---
    @Query("SELECT a.appointmentDate, s.serviceName, s.price, u.fullName " +
           "FROM Appointment a " +
           "LEFT JOIN Service s ON a.serviceID = s.serviceID " +
           "LEFT JOIN User u ON a.dentistID = u.userID " +
           "WHERE a.patientID = :patientId AND a.status = 'Completed' " +
           "ORDER BY a.appointmentDate DESC")
    List<Object[]> getMedicalHistoryData(@Param("patientId") int patientId);

    List<Appointment> findByPatientIDOrderByAppointmentDateDesc(int patientID);
 // Thêm vào AppointmentRepository.java
    @Query("SELECT a FROM Appointment a " +
    	       "LEFT JOIN FETCH a.patient " + // Ép nạp thông tin bệnh nhân
    	       "WHERE a.dentistID = :dentistID")
    	List<Appointment> findByDentistID(@Param("dentistID") int dentistID);
    // --- Các hàm thống kê (Dashboard) ---
    long countByStatus(String status);
    long countByDentistID(int dentistID);
    long countByStatusAndDentistID(String status, int dentistID);

    // Hàm kiểm tra lịch trùng (Dùng cho Booking)
    long countByDentistIDAndAppointmentDateAndAppointmentTime(int dentistID, Date date, Time time);

    @Query("SELECT COALESCE(SUM(s.price), 0.0) FROM Appointment a JOIN Service s ON a.serviceID = s.serviceID WHERE a.status = 'Completed'")
    double sumRevenue();

    @Query("SELECT COALESCE(SUM(s.price), 0.0) FROM Appointment a JOIN Service s ON a.serviceID = s.serviceID WHERE a.dentistID = :dId AND a.status = 'Completed'")
    double sumRevenueByDentist(@Param("dId") int dId);
    
    @Query("SELECT COUNT(DISTINCT a.patientID) FROM Appointment a WHERE a.dentistID = :dId")
    int countDistinctPatientByDentistID(@Param("dId") int dId);

    // --- Hàm tìm kiếm theo điện thoại ---
    @Query("SELECT a.appointmentDate, a.appointmentTime, a.status, s.serviceName, uD.fullName, s.price, a.notes, uP.fullName " +
           "FROM Appointment a " +
           "LEFT JOIN User uP ON a.patientID = uP.userID " +
           "LEFT JOIN User uD ON a.dentistID = uD.userID " +
           "LEFT JOIN Service s ON a.serviceID = s.serviceID " +
           "WHERE uP.phone = :phone OR a.notes LIKE %:phone% " +
           "ORDER BY a.appointmentDate DESC, a.appointmentTime DESC")
    List<Object[]> findByPhoneOrNotesLike(@Param("phone") String phone);
    
    @Query("SELECT COUNT(DISTINCT CASE " +
    	       "WHEN a.patientID IS NOT NULL THEN CAST(a.patientID AS string) " +
    	       "ELSE CONCAT('v-', a.phoneNumber) " + // Dùng prefix 'v-' cho vãng lai để phân biệt
    	       "END) FROM Appointment a")
    	long countAllUniquePatients();
    
 // Đếm bệnh nhân duy nhất của riêng một bác sĩ
    @Query(value = "SELECT COUNT(*) FROM (" +
           "  SELECT patientID FROM Appointments WHERE dentistID = :dId AND patientID IS NOT NULL " +
           "  UNION " +
           "  SELECT phoneNumber FROM Appointments WHERE dentistID = :dId AND patientID IS NULL AND phoneNumber IS NOT NULL" +
           ") AS unique_patients_dentist", nativeQuery = true)
    long countUniquePatientsByDentist(@Param("dId") int dId);
    
    @Query("SELECT COUNT(a) FROM Appointment a WHERE MONTH(a.appointmentDate) = :month AND YEAR(a.appointmentDate) = :year")
    long countByMonth(@Param("month") int month, @Param("year") int year);

    @Query("SELECT COUNT(a) FROM Appointment a WHERE MONTH(a.appointmentDate) = :month AND YEAR(a.appointmentDate) = :year AND a.dentistID = :dId")
    long countByMonthAndDentist(@Param("month") int month, @Param("year") int year, @Param("dId") int dId);
    
 // Thêm vào AppointmentRepository.java
    @Query("SELECT a FROM Appointment a WHERE a.patient.phone = :phone")
    List<Appointment> findByPhoneNumber(@Param("phone") String phone);
    List<Appointment> findByPatientID(int patientID);
    
 // Sử dụng @Query để lấy danh sách bệnh nhân duy nhất đã khám với một nha sĩ
    @Query("SELECT DISTINCT a.patientID, " +
            "COALESCE(u.fullName, a.patientName), " +
            "COALESCE(u.phone, a.phoneNumber), " +
            "COALESCE(u.email, 'N/A') " +
            "FROM Appointment a " +
            "LEFT JOIN a.patient u " +
            "WHERE a.dentistID = :dentistID")
     List<Object[]> findDistinctPatientsByDentistID(@Param("dentistID") int dentistID);
    
 // Thêm vào AppointmentRepository.java
    @Query("SELECT DISTINCT a.patient FROM Appointment a WHERE a.serviceID = :serviceID AND a.dentistID IS NOT NULL")
    // Lưu ý: Nếu 'dentistID' trong Appointment chính là đối tượng User bác sĩ, 
    // bạn nên sửa Entity Appointment để quan hệ @ManyToOne tới User (bác sĩ)
    List<User> findDentistsByServiceID(@Param("serviceID") int serviceID);
    
    List<Appointment> findByDentistIDAndAppointmentDateAndAppointmentTimeAndStatus(
            int dentistID, Date appointmentDate, Time appointmentTime, String status);
 // Trong AppointmentRepository.java
    List<Appointment> findByPhoneNumberAndStatusOrderByAppointmentDateDesc(String phoneNumber, String status);
    
    @Query("""
    		SELECT a
    		FROM Appointment a
    		WHERE a.patientID = :patientID
    		AND a.status = 'Completed'
    		ORDER BY a.appointmentDate DESC
    		""")
    		List<Appointment> findHistoryByPatientID(
    		        @Param("patientID") Integer patientID);
    
    @Query(value = "SELECT a.AppointmentID, a.AppointmentDate, a.AppointmentTime, a.Status, " +
            "s.ServiceName, u_dentist.FullName AS DentistName, " +
            "COALESCE(a.phoneNumber, u_patient.Phone) AS PatientPhone, " +
            "COALESCE(a.patientName, u_patient.FullName) AS PatientName, " +
            "a.notes, s.Price " + // <-- THÊM s.Price VÀO ĐÂY
            "FROM Appointments a " +
            "LEFT JOIN Services s ON a.ServiceID = s.ServiceID " +
            "LEFT JOIN Users u_dentist ON a.DentistID = u_dentist.UserID " +
            "LEFT JOIN Users u_patient ON a.PatientID = u_patient.UserID " +
            "WHERE (a.phoneNumber = :phone OR u_patient.Phone = :phone) " +
            "AND a.Status = 'Completed' " + // <-- THÊM ĐIỀU KIỆN ĐỂ LẤY LỊCH SỬ ĐÚNG
            "ORDER BY a.AppointmentDate DESC", nativeQuery = true)
    List<Object[]> findHistoryByPhoneNumber(@Param("phone") String phone);
}