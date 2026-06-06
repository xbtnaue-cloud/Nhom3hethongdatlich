package com.nhakhoa.controller;

import com.nhakhoa.model.User;
import com.nhakhoa.service.UserService;
import com.nhakhoa.service.AppointmentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.SessionAttribute;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Controller
public class AdminDashboardController {

    @Autowired
    private UserService userService;

    @Autowired
    private AppointmentService appointmentService;

    @GetMapping("/admin-stats")
    public String showDashboardStats(@SessionAttribute(value = "acc", required = false) User acc, Model model) {
        
        if (acc == null || (acc.getRoleID() != 1 && acc.getRoleID() != 2)) {
            return "redirect:/login";
        }

        try {
            // 1. Thống kê số liệu thẻ (Cards)
            if (acc.getRoleID() == 1) {
                model.addAttribute("roleName", "Quản trị viên");
                model.addAttribute("totalPatients", appointmentService.countTotalPatients());
                model.addAttribute("totalDoctors", userService.countActiveDoctors());
                model.addAttribute("totalRevenue", appointmentService.getTotalRevenue());
                model.addAttribute("totalAppointments", appointmentService.countTotalAppointments());
                model.addAttribute("pending", appointmentService.countByStatus("Pending"));
                model.addAttribute("confirmed", appointmentService.countByStatus("Confirmed"));
                model.addAttribute("completed", appointmentService.countByStatus("Completed"));
                model.addAttribute("cancelled", appointmentService.countByStatus("Cancelled"));
            } else {
                model.addAttribute("roleName", "Bác sĩ chuyên khoa");
                int dID = acc.getUserID();
                model.addAttribute("totalPatients", appointmentService.countUniquePatientsByDentist(dID));
                model.addAttribute("totalDoctors", (acc.getStatusID() == 1) ? 1 : 0);
                model.addAttribute("totalRevenue", appointmentService.getRevenueByDentist(dID));
                model.addAttribute("totalAppointments", appointmentService.countAppointmentsByDentist(dID));
                model.addAttribute("pending", appointmentService.countByStatusAndDentist("Pending", dID));
                model.addAttribute("confirmed", appointmentService.countByStatusAndDentist("Confirmed", dID));
                model.addAttribute("completed", appointmentService.countByStatusAndDentist("Completed", dID));
                model.addAttribute("cancelled", appointmentService.countByStatusAndDentist("Cancelled", dID));
            }

            // 2. Tính toán dữ liệu biểu đồ (3 tháng gần nhất)
            List<String> labels = new ArrayList<>();
            List<Long> data = new ArrayList<>();
            LocalDate now = LocalDate.now();

            for (int i = 2; i >= 0; i--) {
                LocalDate d = now.minusMonths(i);
                labels.add("Tháng " + d.getMonthValue());
                
                if (acc.getRoleID() == 1) {
                    data.add(appointmentService.countByMonth(d.getMonthValue(), d.getYear()));
                } else {
                    data.add(appointmentService.countByMonthAndDentist(d.getMonthValue(), d.getYear(), acc.getUserID()));
                }
            }
            
            model.addAttribute("chartLabels", labels);
            model.addAttribute("chartData", data);
            model.addAttribute("activePage", "dashboard");

            return "admin-dashboard"; // Đặt return ở cuối sau khi đã thêm đầy đủ attributes

        } catch (Exception e) {
            e.printStackTrace();
            return "admin-dashboard";
        }
    }

    @PostMapping("/admin-stats")
    public String handlePostStats(@SessionAttribute(value = "acc", required = false) User acc, Model model) {
        return showDashboardStats(acc, model);
    }
}