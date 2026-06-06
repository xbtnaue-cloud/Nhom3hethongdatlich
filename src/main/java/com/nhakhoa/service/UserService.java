package com.nhakhoa.service;

import com.nhakhoa.dto.AppointmentDTO;
import com.nhakhoa.model.User;
import com.nhakhoa.repository.UserRepository;
import com.nhakhoa.repository.AppointmentRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.nhakhoa.model.Appointment;
import java.util.List;
import java.util.stream.Collectors;
import com.nhakhoa.dto.PatientDTO;
@Service
public class UserService {

    @Autowired
    private UserRepository userRepository;
    

    @Autowired
    private AppointmentRepository appointmentRepository;

    public User getUserByID(int id) { return userRepository.findById(id).orElse(null); }
    public User login(String username, String password) { return userRepository.findByUsernameAndPassword(username, password).orElse(null); }
    public List<User> getAllDoctors() { return userRepository.findByRoleID(2); }
    public boolean checkEmailExist(String email) { return userRepository.existsByEmail(email); }
    public boolean checkUserExist(String username) { return userRepository.existsByUsername(username); }
    public List<User> getAllUsers() {
        return userRepository.findAll(); // Sử dụng hàm mặc định của JpaRepository
    }

    // Các hàm thống kê cho Dashboard
    public long countPatients() { return userRepository.countByRoleID(3); }
    public long countActiveDoctors() { return userRepository.countByRoleIDAndStatusID(2, 1); }
 // Trong UserService.java
    public List<User> getAllPatients() {
        return userRepository.findByRoleID(3); // Giả sử RoleID=3 là Bệnh nhân
    }

    // Trong AppointmentService.java
    public List<PatientDTO> getAllPatientsDTO() {
        return userRepository.findByRoleID(3).stream().map(u -> {
            PatientDTO dto = new PatientDTO();
            dto.setUserID(u.getUserID());
            dto.setFullName(u.getFullName());
            dto.setPhone(u.getPhone());
            dto.setEmail(u.getEmail());
            return dto;
        }).collect(Collectors.toList());
    }

    @Transactional
    public void insertDoctor(String user, String pass, String name, String email, String phone, String specialty) {
        User u = new User();
        u.setUsername(user); u.setPassword(pass); u.setFullName(name);
        u.setEmail(email); u.setPhone(phone); u.setSpecialty(specialty);
        u.setRoleID(2); u.setStatusID(1);
        userRepository.save(u);
    }

    @Transactional
    public boolean register(String user, String pass, String name, String email, String phone, int roleID) {
        try {
            User newUser = new User();
            newUser.setUsername(user); newUser.setPassword(pass);
            newUser.setFullName(name); newUser.setEmail(email);
            newUser.setPhone(phone); newUser.setRoleID(roleID); newUser.setStatusID(1);
            userRepository.save(newUser);
            return true;
        } catch (Exception e) { return false; }
    }
 // Thêm vào UserService.java
    public boolean deleteUser(int id) {
        try {
            if (userRepository.existsById(id)) {
                userRepository.deleteById(id);
                return true;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }
    @Transactional
    public void updateDoctor(int id, String name, String email, String phone, String username, String specialty, int statusID, String password) {
        User user = userRepository.findById(id).orElse(null);
        if (user != null) {
            user.setFullName(name);
            user.setEmail(email);
            user.setPhone(phone);
            user.setUsername(username);
            user.setSpecialty(specialty);
            user.setStatusID(statusID);
            if (password != null && !password.isEmpty()) {
                user.setPassword(password);
            }
            userRepository.save(user);
        }
    }
 // Thêm vào UserService.java
    @Transactional
    public void updatePatient(int id, String name, String email, String phone) {
        User user = userRepository.findById(id).orElse(null);
        if (user != null) {
            user.setFullName(name);
            user.setEmail(email);
            user.setPhone(phone);
            userRepository.save(user);
        }
    }
    @Transactional
    public boolean updatePassword(String email, String newPassword) {
        User user = userRepository.findByEmail(email); // Bạn cần thêm hàm này vào UserRepository
        if (user != null) {
            user.setPassword(newPassword);
            userRepository.save(user);
            return true;
        }
        return false;
    }
 // Thêm vào UserService.java
    public List<User> getDentistsBySpecialty(String specialtyName) {
        // Gọi hàm đã đổi tên trong Repository
        return userRepository.findDentistsBySpecialty(specialtyName);
    }
    
    // Lấy lịch sử khám bệnh (dùng AppointmentRepository)
    public List<AppointmentDTO> getPatientMedicalHistory(int patientId) {
        List<Appointment> list = appointmentRepository.findByPatientID(patientId);
        // Chuyển đổi sang DTO
        return list.stream().map(app -> {
            AppointmentDTO dto = new AppointmentDTO();
            dto.setAppointmentID(app.getAppointmentID());
            dto.setAppointmentDate(app.getAppointmentDate());
            dto.setStatus(app.getStatus());
            // Map thêm các trường khác nếu cần
            return dto;
        }).collect(Collectors.toList());
    }
    
 // Thêm vào UserService.java
    public boolean isUsernameExists(String username) {
        return userRepository.existsByUsername(username);
    }

    public void registerUser(String username, String password, String fullName, String email, String phone) {
        User user = new User();
        user.setUsername(username);
        user.setPassword(password); // Lưu ý: Nên băm mật khẩu ở đây nếu cần
        user.setFullName(fullName);
        user.setEmail(email);
        user.setPhone(phone);
        user.setRoleID(3); // Mặc định là bệnh nhân
        user.setStatusID(1); // Mặc định đang hoạt động
        userRepository.save(user);
    }
    
    @Transactional
    public void updateProfile(int userId, String fullName, String phone, String password) {
        User user = userRepository.findById(userId).orElse(null);
        if (user != null) {
            user.setFullName(fullName);
            user.setPhone(phone);
            user.setPassword(password);
            userRepository.save(user); // Cập nhật xuống Database
        }
    }
    
    @Transactional
    public void updateUserRole(int userId, int roleID) {
        User user = userRepository.findById(userId).orElse(null);
        if (user != null) {
            user.setRoleID(roleID);
            userRepository.save(user);
        }
    }
    
    @Transactional
    public void updateUserByAdmin(int id, String fullName, String email, String phone, int roleID, boolean isReset) {
        User user = userRepository.findById(id).orElse(null);
        if (user != null) {
            user.setFullName(fullName);
            user.setEmail(email);
            user.setPhone(phone);
            user.setRoleID(roleID);
            
            if (isReset) {
                user.setPassword("123456"); // Mật khẩu mặc định
            }
            
            userRepository.save(user);
        }
    }
    
    @Transactional
    public void updateUserStatus(int userId, int statusID) {
        User user = userRepository.findById(userId).orElse(null);
        if (user != null) {
            user.setStatusID(statusID);
            userRepository.save(user);
        }
    }
    
}