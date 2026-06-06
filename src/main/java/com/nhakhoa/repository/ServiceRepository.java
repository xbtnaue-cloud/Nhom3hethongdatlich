package com.nhakhoa.repository;

import com.nhakhoa.model.Service;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface ServiceRepository extends JpaRepository<Service, Integer> {
    
    // Chuyển logic từ hàm getAllServices cũ vào đây
    @Query(value = "SELECT s.ServiceID, s.ServiceName, s.Description, s.Price, s.ServiceImage, " +
           "GROUP_CONCAT(u.FullName) AS doctorNames, " +
           "CONCAT('[', IFNULL(GROUP_CONCAT(u.UserID), ''), ']') AS doctorIdsJson " +
           "FROM Services s " +
           "LEFT JOIN DentistServices ds ON s.ServiceID = ds.ServiceID " +
           "LEFT JOIN Users u ON ds.DentistID = u.UserID " +
           "GROUP BY s.ServiceID, s.ServiceName, s.Description, s.Price, s.ServiceImage", 
           nativeQuery = true)
    List<Object[]> findAllServicesWithDoctorInfo();
}