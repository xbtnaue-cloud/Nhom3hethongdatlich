package com.nhakhoa.controller;

import com.nhakhoa.model.Appointment;
import com.nhakhoa.model.User;
import com.nhakhoa.repository.UserRepository;
import com.nhakhoa.service.AppointmentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import java.security.Principal;
import java.util.ArrayList;
import java.util.List;
import com.nhakhoa.dto.AppointmentDTO;
import java.util.stream.Collectors;
@Controller
public class DentistDashboardController {

    @Autowired
    private AppointmentService appointmentService;

    @Autowired
    private UserRepository userRepository;

    @GetMapping("/dentist/dashboard")
    public String showDentistDashboard(Principal principal, Model model) {
        // 1. Lấy thông tin user đăng nhập
        String username = principal.getName();
        User doctor = userRepository.findByUsername(username); 
        
        // 2. Kiểm tra NULL và quyền (RoleID = 2 là bác sĩ)
        if (doctor == null || doctor.getRoleID() != 2) {
            return "redirect:/login";
        }
        
        // 3. KHAI BÁO BIẾN dentistID TRƯỚC KHI DÙNG
        // Lấy ID từ đối tượng doctor vừa tìm được (sử dụng getUserID() theo class User của bạn)
        int dentistID = doctor.getUserID(); 
        
        // 4. Truyền đối tượng doctor vào model để file HTML dùng
        model.addAttribute("doctor", doctor);
        
        // 5. Lấy danh sách lịch hẹn
        List<Appointment> list = appointmentService.getAppointmentsByDentistEntity(dentistID);

        // 6. CHUYỂN ĐỔI SANG DTO
        List<AppointmentDTO> listDTO = list.stream()
                .map(appointmentService::convertToDTO)
                .collect(Collectors.toList());

        model.addAttribute("listApp", listDTO);
        model.addAttribute("activePage", "appointments");

        return "dentist-schedule";
    }
}