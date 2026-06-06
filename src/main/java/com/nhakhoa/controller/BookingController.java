package com.nhakhoa.controller;

import com.nhakhoa.model.User;
import com.nhakhoa.service.AppointmentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@Controller
public class BookingController {

    @Autowired
    private AppointmentService appointmentService;

    // 1. Hiển thị trang đặt lịch
    @GetMapping("/booking")
    public String showBookingPage(Model model) {
        model.addAttribute("listS", appointmentService.getAllServices());
        return "booking";
    }

    // 2. API lấy bác sĩ theo dịch vụ (Dùng User vì dữ liệu nằm ở bảng Users)
    @GetMapping("/getDentists")
    @ResponseBody
    public String getDentistsByService(@RequestParam(value = "serviceID", required = false) String serviceIDRaw) {
        if (serviceIDRaw == null || serviceIDRaw.isBlank()) {
            return "<option value=''>-- Chọn dịch vụ trước --</option>";
        }

        try {
            int serviceID = Integer.parseInt(serviceIDRaw);
            // Gọi service trả về List<User> (Bác sĩ nằm trong bảng Users)
            List<User> list = appointmentService.getDentistsByService(serviceID);

            if (list == null || list.isEmpty()) {
                return "<option value=''>-- Chưa có bác sĩ cho dịch vụ này --</option>";
            }

            StringBuilder htmlOptions = new StringBuilder("<option value=''>-- Chọn bác sĩ --</option>");
            for (User u : list) {
                // Dùng getUserID() và getFullName() của class User
                htmlOptions.append("<option value='").append(u.getUserID()).append("'>")
                           .append(u.getFullName()).append("</option>");
            }
            return htmlOptions.toString();
        } catch (Exception e) {
            e.printStackTrace();
            return "<option value=''>Lỗi tải bác sĩ</option>";
        }
    }

    // 3. Xử lý đặt lịch (POST)
    @PostMapping("/booking")
    public String handleBooking(
            @SessionAttribute(value = "acc", required = false) User user,
            @RequestParam("fullName") String fullName,
            @RequestParam("phone") String phone,
            @RequestParam("dentistID") int dentistID,
            @RequestParam("serviceID") int serviceID,
            @RequestParam("date") String date,
            @RequestParam("time") String time,
            @RequestParam(value = "notes", required = false) String notes,
            Model model) {

        // Kiểm tra đầu vào
        if (fullName.isBlank() || phone.isBlank() || date.isBlank() || time.isBlank()) {
            model.addAttribute("error", "Vui lòng điền đầy đủ thông tin!");
            model.addAttribute("listS", appointmentService.getAllServices());
            return "booking";
        }

        // Kiểm tra trùng lịch
        if (appointmentService.isDuplicateAppointment(dentistID, date, time)) {
            model.addAttribute("error", "Nha sĩ đã có lịch hẹn khung giờ này.");
            model.addAttribute("listS", appointmentService.getAllServices());
            return "booking";
        }

        // Xử lý ghi chú cho khách vãng lai
        Integer patientID = (user != null) ? user.getUserID() : null;
        String finalNotes = (user == null) 
            ? "【KHÁCH VÃNG LAI: " + fullName.toUpperCase() + " - SĐT: " + phone + "】" + (notes != null ? " | " + notes : "")
            : notes;

     // Sửa đoạn gọi này trong BookingController.java:
        appointmentService.addAppointment(patientID, dentistID, serviceID, date, time, notes, phone, fullName);
        
        model.addAttribute("mess", "✅ Đặt lịch thành công!");
        model.addAttribute("listS", appointmentService.getAllServices());
        return "booking";
    }
}