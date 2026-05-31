package com.nhakhoa.controller;

import com.nhakhoa.dao.AppointmentDAO;
import com.nhakhoa.model.Appointment;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

@Controller // Đổi sang Annotation của Spring
public class LookupController {

    // ── 1. GET: Xử lý tra cứu lịch hẹn và hiển thị thông báo ─────────────────────────
    @GetMapping("/lookup-appointment")
    public String lookupAppointment(
            @RequestParam(value = "phone", required = false) String phone,
            @RequestParam(value = "msg", required = false) String msg,
            Model model) {

        try {
            AppointmentDAO dao = new AppointmentDAO();

            if (phone != null && !phone.isBlank()) {
                phone = phone.trim();
                List<Appointment> listA = dao.getAppointmentsByPhone(phone);

                if (listA != null && !listA.isEmpty()) {
                    model.addAttribute("listA", listA);
                } else {
                    model.addAttribute("message", "Không tìm thấy lịch hẹn nào cho số điện thoại này.");
                }
                model.addAttribute("searchPhone", phone);
            }

            // Xử lý hiển thị thông báo thân thiện bằng Spring Model thay vì JSTL
            if ("cancel_ok".equals(msg)) model.addAttribute("success", "✅ Hủy lịch thành công!");
            if ("cancel_fail".equals(msg)) model.addAttribute("error", "❌ Hủy lịch thất bại, vui lòng thử lại.");
            if ("too_late".equals(msg)) model.addAttribute("error", "⚠️ Không thể hủy trực tuyến vì đã quá hạn (24h).");

            return "lookup-result"; // Mở file lookup-result.html trong thư mục templates

        } catch (Exception e) {
            e.printStackTrace();
            return "error"; // Mở file error.html nếu sập hệ thống
        }
    }

    // ── 2. POST: Xử lý yêu cầu Hủy lịch (Từ Form Modal gửi lên) ─────────────────────
    @PostMapping("/lookup-appointment")
    public String processAction(
            @RequestParam(value = "action", required = false) String action,
            @RequestParam("searchPhone") String phone,
            @RequestParam(value = "appointmentID", required = false) Integer appointmentID,
            @RequestParam(value = "cancelReason", required = false) String reason) {

        // Nếu bấm tra cứu lại bằng phương thức POST (giữ nguyên logic cũ của ní)
        if (!"cancel".equals(action)) {
            return "redirect:/lookup-appointment?phone=" + phone;
        }

        try {
            AppointmentDAO dao = new AppointmentDAO();

            // --- BƯỚC 2.1: LẤY THÔNG TIN LỊCH HẸN ĐỂ KIỂM TRA THỜI GIAN ---
            Appointment app = dao.getAppointmentByID(appointmentID);

            if (app != null) {
                // Ghép ngày và giờ từ Model cũ của ní thành chuỗi hoàn chỉnh để so sánh
                String dateTimeStr = app.getAppointmentDate().toString() + " " + app.getAppointmentTime().toString();
                DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
                LocalDateTime appointmentDateTime = LocalDateTime.parse(dateTimeStr, formatter);

                // Kiểm tra nếu thời gian hiện tại cách giờ hẹn ít hơn 24 tiếng
                if (LocalDateTime.now().isAfter(appointmentDateTime.minusHours(24))) {
                    return "redirect:/lookup-appointment?phone=" + phone + "&msg=too_late";
                }

                // --- BƯỚC 2.2: THỰC HIỆN HỦY ---
                boolean success = dao.cancelAppointmentWithReason(appointmentID, reason);
                if (success) {
                    return "redirect:/lookup-appointment?phone=" + phone + "&msg=cancel_ok";
                } else {
                    return "redirect:/lookup-appointment?phone=" + phone + "&msg=cancel_fail";
                }
            }
            
            return "redirect:/lookup-appointment?phone=" + phone + "&msg=cancel_fail";

        } catch (Exception e) {
            e.printStackTrace();
            return "redirect:/lookup-appointment?phone=" + phone + "&msg=cancel_fail";
        }
    }
}