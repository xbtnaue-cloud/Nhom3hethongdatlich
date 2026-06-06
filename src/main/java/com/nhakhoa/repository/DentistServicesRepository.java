package com.nhakhoa.repository;


import com.nhakhoa.model.DentistServices;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import com.nhakhoa.model.User;
import java.util.List;

@Repository
public interface DentistServicesRepository extends JpaRepository<DentistServices, Integer> {

    // Truy vấn danh sách Dentist thông qua bảng trung gian DentistServices
    // ds.service.serviceID: truy cập vào serviceID thông qua mối quan hệ @ManyToOne trong DentistServices
	@Query("SELECT ds.dentist FROM DentistServices ds WHERE ds.service.serviceID = :serviceID")
    List<User> findDentistsByServiceID(@Param("serviceID") int serviceID);
	
	@Modifying
    @Query("DELETE FROM DentistServices d WHERE d.serviceID = :serviceID")
    void deleteByServiceID(@Param("serviceID") int serviceID);
}