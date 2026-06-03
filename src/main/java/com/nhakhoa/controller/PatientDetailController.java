package com.nhakhoa.controller;

import com.nhakhoa.dao.AppointmentDAO;
import com.nhakhoa.dao.UserDAO;
import com.nhakhoa.model.Appointment;
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
	@ResponseBody // 🌟 Đặt thẳng ở đây để Controller này luôn trả về dữ liệu (JSON/Object)
	public Object handlePatientDetail(
	        @RequestParam(value = "id", required = false) Integer patientId,
	        @RequestParam(value = "phone", required = false) String phone,
	        @RequestParam(value = "ajax", required = false) String isAjax,
	        Model model) {

	    try {
	        AppointmentDAO appDao = new AppointmentDAO();
	        UserDAO uDao = new UserDAO();
	        List<Appointment> medicalHistory = (patientId != null && patientId > 0) 
	            ? uDao.getPatientMedicalHistory(patientId) 
	            : (phone != null ? appDao.getAppointmentsByPhone(phone) : new ArrayList<>());

	        // --- NẾU LÀ AJAX (Hiển thị Modal) ---
	        if ("true".equals(isAjax)) {
	            List<Map<String, Object>> jsonResponse = new ArrayList<>();
	            for (Appointment app : medicalHistory) {
	                Map<String, Object> map = new HashMap<>();
	                map.put("appointmentDate", app.getAppointmentDate() != null ? app.getAppointmentDate().toString() : "N/A");
	                map.put("serviceName", app.getServiceName() != null ? app.getServiceName() : "Khám tổng quát");
	                map.put("dentistName", app.getDentistName() != null ? app.getDentistName() : "Chưa phân công");
	                map.put("price", app.getPrice());
	                jsonResponse.add(map);
	            }
	            return jsonResponse; // Trả về JSON cho fetch
	        }

	        // --- NẾU LÀ XEM TRANG CHI TIẾT (HTML) ---
	        if (patientId != null && patientId > 0) {
	            model.addAttribute("p", uDao.getUserByID(patientId));
	        }
	        model.addAttribute("history", medicalHistory);
	        return "patient_detail"; // Trả về file HTML

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