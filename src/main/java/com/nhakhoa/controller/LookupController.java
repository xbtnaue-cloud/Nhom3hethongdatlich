package com.nhakhoa.controller;

import com.nhakhoa.dto.AppointmentDTO;
import com.nhakhoa.model.Appointment;
import com.nhakhoa.service.AppointmentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

@Controller
public class LookupController {

    @Autowired
    private AppointmentService appointmentService;

    @GetMapping("/lookup-appointment")
    public String lookupAppointment(
            @RequestParam(value = "phone", required = false) String phone,
            @RequestParam(value = "msg", required = false) String msg,
            Model model) {

    	if (phone != null && !phone.isBlank()) {
    	    String cleanPhone = phone.trim();
    	    
    	    // GỌI HÀM MỚI VỪA TẠO Ở SERVICE
    	    List<AppointmentDTO> listA = appointmentService.getHistoryByPhone(cleanPhone);
    	    
    	    if (!listA.isEmpty()) {
    	        model.addAttribute("listA", listA);
    	    } else {
    	        model.addAttribute("message", "Không tìm thấy lịch hẹn cho số điện thoại này.");
    	    }
    	    model.addAttribute("searchPhone", cleanPhone);
    	}

        if ("cancel_ok".equals(msg)) model.addAttribute("success", "✅ Hủy lịch thành công!");
        else if ("cancel_fail".equals(msg)) model.addAttribute("error", "❌ Hủy lịch thất bại.");
        else if ("too_late".equals(msg)) model.addAttribute("error", "⚠️ Không thể hủy vì đã quá hạn 24h.");

        return "lookup-result"; // Đã sửa từ lookup-result thành look-res
    }

    @PostMapping("/lookup-appointment")
    public String processAction(
            @RequestParam(value = "action", required = false) String action,
            @RequestParam("searchPhone") String phone,
            @RequestParam(value = "appointmentID", required = false) Integer appointmentID,
            @RequestParam(value = "cancelReason", required = false) String reason) {

        if ("cancel".equals(action)) {
            boolean success = appointmentService.cancelAppointment(appointmentID, reason);
            if (success) {
                return "redirect:/lookup-appointment?phone=" + phone + "&msg=cancel_ok";
            } else {
                return "redirect:/lookup-appointment?phone=" + phone + "&msg=cancel_fail";
            }
        }
        return "redirect:/lookup-appointment?phone=" + phone;
    }
}
