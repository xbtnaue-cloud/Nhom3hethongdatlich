package com.nhakhoa.repository;

import com.nhakhoa.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Optional;
import org.springframework.data.repository.query.Param;
import org.springframework.data.jpa.repository.Query;
@Repository
public interface UserRepository extends JpaRepository<User, Integer> {
	
	User findByUsername(String username);
    // Tìm theo username và password cho chức năng đăng nhập
    Optional<User> findByUsernameAndPassword(String username, String password);
    @Query("SELECT u FROM User u WHERE u.roleID = 2 AND u.specialty = :specialty")
    List<User> findDentistsBySpecialty(@Param("specialty") String specialty);
    // Tìm tất cả bác sĩ (RoleID = 2)
    List<User> findByRoleID(int roleID);
    
    // Kiểm tra tồn tại
    boolean existsByUsername(String username);
    boolean existsByEmail(String email);
    
    long countByRoleID(int roleID);
    long countByRoleIDAndStatusID(int roleID, int statusID);
    
    User findByEmail(String email);
    
}