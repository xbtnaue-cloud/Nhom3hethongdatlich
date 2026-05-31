package com.nhakhoa.controller;

import com.nhakhoa.dao.AppointmentDAO;
import com.nhakhoa.dao.UserDAO;
import com.nhakhoa.model.Appointment;
import com.nhakhoa.model.User;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Controller // Dùng Annotation của Spring
public class PatientDetailController {

    // Gom cả GET và POST về chung một lộ trình xử lý tương thích logic cũ
    @RequestMapping(value = "/patient-detail", method = {RequestMethod.GET, RequestMethod.POST})
    public String getPatientDetail(
            @RequestParam(value = "id", required = false) Integer patientId,
            @RequestParam(value = "phone", required = false) String phone,
            @RequestParam(value = "ajax", required = false) String isAjax,
            Model model) {

        // Kiểm tra phòng hờ nếu URL không truyền ID lên
        if (patientId == null) {
            return "redirect:/patients";
        }

        try {
            AppointmentDAO appDao = new AppointmentDAO();
            UserDAO uDao = new UserDAO();
            List<Appointment> medicalHistory = new ArrayList<>();

            // ── 1. PHÂN LOẠI BỆNH NHÂN ĐỂ LẤY LỊCH SỬ KHÁM ──────────────────────
            if (patientId > 0) {
                // Trường hợp 1: Bệnh nhân có tài khoản hệ thống (ID > 0)
                medicalHistory = uDao.getPatientMedicalHistory(patientId);
            } else if (phone != null && !phone.isBlank()) {
                // Trường hợp 2: Khách vãng lai (ID = 0) - Tìm kiếm bằng Số điện thoại
                medicalHistory = appDao.getAppointmentsByPhone(phone);
            }

            // ── 2. XỬ LÝ TRẢ VỀ JSON CHO AJAX (Hiển thị Popup Modal) ───────────
            if ("true".equals(isAjax)) {
                // Ta lưu danh sách Map này trực tiếp vào model tạm thời và rẽ nhánh sang một API Controller khác, 
                // hoặc cách nhanh nhất là ní trả về một chuỗi xử lý chuyển hướng nội bộ.
                // Để tối ưu và giữ nguyên luồng húp chuỗi JSON chuẩn bài, ní dùng "forward" sang đường dẫn API bên dưới nhé!
                model.addAttribute("sharedHistory", medicalHistory);
                return "forward:/api/patient-detail-json";
            }

            // ── 3. XỬ LÝ HIỂN THỊ CHUYỂN TRANG TRUYỀN THỐNG (Admin/Bác sĩ xem chi tiết) ──
            if (patientId > 0) {
                User patient = uDao.getUserByID(patientId);
                model.addAttribute("p", patient); // Gửi thông tin cá nhân
            }
            
            model.addAttribute("history", medicalHistory); // Gửi danh sách lịch sử khám
            return "patient_detail"; // Mở file patient_detail.html trong thư mục templates

        } catch (Exception e) {
            e.printStackTrace();
            return "error";
        }
    }

    // ── 4. API BỔ TRỢ: Tự động xuất chuỗi mảng JSON an toàn, sạch sẽ khớp Frontend cũ ──
    @RequestMapping(value = "/api/patient-detail-json", method = {RequestMethod.GET, RequestMethod.POST})
    @ResponseBody // Ép xuất JSON trực tiếp thay vì tìm file giao diện
    public List<Map<String, Object>> getPatientDetailJson(Model model) {
        List<Map<String, Object>> jsonResponse = new ArrayList<>();
        
        // Nhận lại danh sách lịch sử đã bốc từ luồng forward bên trên
        @SuppressWarnings("unchecked")
        List<Appointment> medicalHistory = (List<Appointment>) model.getAttribute("sharedHistory");

        if (medicalHistory != null) {
            for (Appointment app : medicalHistory) {
                Map<String, Object> map = new HashMap<>();
                
                String date = (app.getAppointmentDate() != null) ? app.getAppointmentDate().toString() : "N/A";
                String service = (app.getServiceName() != null) ? app.getServiceName() : "Khám tổng quát";
                String dentist = (app.getDentistName() != null) ? app.getDentistName() : "Chưa phân công";

                map.put("appointmentDate", date);
                map.put("serviceName", service);
                map.put("dentistName", dentist);
                map.put("price", app.getPrice()); // Giữ nguyên kiểu dữ liệu số thực, Jackson tự chuyển đổi

                jsonResponse.add(map);
            }
        }
        return jsonResponse; // Trả về dạng mảng JSON [{}, {}] sạch sẽ không sợ dính lỗi ký tự đặc biệt
    }
}