package com.nhakhoa.controller;

import com.nhakhoa.dao.AppointmentDAO;
import com.nhakhoa.model.Service;
import com.nhakhoa.model.User;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.SessionAttribute;

import java.util.List;

@Controller // Đổi sang Annotation của Spring
public class BookingController {

    // 1. GET: Hiển thị form đặt lịch lần đầu
    @GetMapping("/booking")
    public String showBookingPage(Model model) {
        try {
            AppointmentDAO dao = new AppointmentDAO();
            List<Service> listS = dao.getAllServices();
            
            model.addAttribute("listS", listS);
            
            if (listS == null || listS.isEmpty()) {
                model.addAttribute("error", "⚠️ Hiện không có dịch vụ nào khả dụng.");
            }
        } catch (Exception e) {
            e.printStackTrace();
            model.addAttribute("error", "Lỗi kết nối hệ thống dữ liệu.");
        }
        
        return "booking"; // Mở file booking.html trong thư mục templates
    }

    // 2. POST: Xử lý dữ liệu khi người dùng nhấn "Xác nhận đặt lịch"
    @PostMapping("/booking")
    public String handleBooking(
            @SessionAttribute(value = "acc", required = false) User user,
            @RequestParam("fullName") String fullName,
            @RequestParam("phone") String phone,
            @RequestParam("dentistID") String dentistIDRaw,
            @RequestParam("serviceID") String serviceIDRaw,
            @RequestParam("date") String date,
            @RequestParam("time") String time,
            @RequestParam(value = "notes", required = false) String notes,
            Model model) {

        AppointmentDAO dao = new AppointmentDAO();

        // Kiểm tra các trường bắt buộc (Sử dụng cú pháp gọn của Java 17 thay vì viết hàm isBlank thủ công)
        if (fullName == null || fullName.isBlank() || 
            phone == null || phone.isBlank() || 
            dentistIDRaw == null || dentistIDRaw.isBlank() || 
            serviceIDRaw == null || serviceIDRaw.isBlank() || 
            date == null || date.isBlank() || 
            time == null || time.isBlank()) {
            
            model.addAttribute("error", "Vui lòng điền đầy đủ tất cả thông tin bắt buộc!");
            model.addAttribute("listS", dao.getAllServices());
            return "booking";
        }

        try {
            int dentistID = Integer.parseInt(dentistIDRaw);
            int serviceID = Integer.parseInt(serviceIDRaw);

            // --- BƯỚC 3: KIỂM TRA TRÙNG LỊCH (Chốt chặn quan trọng) ---
            if (dao.isDuplicateAppointment(dentistID, date, time)) {
                model.addAttribute("error", "Rất tiếc! Nha sĩ đã có lịch hẹn vào khung giờ này. Vui lòng chọn thời gian khác.");
                model.addAttribute("listS", dao.getAllServices());
                return "booking";
            }

            // 4. Xử lý định danh bệnh nhân (Có tài khoản vs Khách vãng lai)
            Integer patientID = null;
            if (user != null) {
                patientID = user.getUserID();
            } else {
                String guestInfo = "【KHÁCH VÃNG LAI: " + fullName.toUpperCase() + " - SĐT: " + phone + "】";
                notes = (notes == null || notes.isBlank()) ? guestInfo : guestInfo + " | Ghi chú: " + notes;
            }

            // 5. Lưu vào Database thông qua DAO cũ
            dao.addAppointment(patientID, dentistID, serviceID, date, time, notes);
            
            // 6. Trả về thông báo thành công
            model.addAttribute("mess", "✅ Đặt lịch thành công! Nha khoa sẽ liên hệ xác nhận với bạn sớm nhất.");

        } catch (NumberFormatException e) {
            model.addAttribute("error", "Dữ liệu không hợp lệ. Vui lòng kiểm tra lại các mục đã chọn.");
        } catch (Exception e) {
            e.printStackTrace();
            model.addAttribute("error", "Đã xảy ra lỗi hệ thống: " + e.getMessage());
        }

        // Luôn nạp lại danh sách dịch vụ cho form ở cuối quy trình giống hàm reloadForm cũ của ní
        model.addAttribute("listS", dao.getAllServices());
        return "booking";
    }
}