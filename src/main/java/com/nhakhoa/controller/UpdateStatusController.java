package com.nhakhoa.controller;

import com.nhakhoa.dao.AppointmentDAO;
import com.nhakhoa.model.Appointment;
import com.nhakhoa.model.User;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.SessionAttribute;

@Controller // Đổi sang Annotation của Spring
public class UpdateStatusController {

    // Thay thế hoàn toàn cho processRequest, doGet, doPost và urlPatterns = {"/update-status"}
    @RequestMapping(value = "/update-status", method = {RequestMethod.GET, RequestMethod.POST})
    public String updateStatus(
            @SessionAttribute(value = "acc", required = false) User acc,
            @RequestParam(value = "id", required = false) Integer id,
            @RequestParam(value = "status", required = false) String status,
            @RequestParam(value = "reason", required = false) String reason) {

        // 1. Kiểm tra quyền truy cập hệ thống (Chỉ cho phép Admin hoặc Bác sĩ)
        if (acc == null || (acc.getRoleID() != 1 && acc.getRoleID() != 2)) {
            return "redirect:/login";
        }

        // Xác định trang đích để chuyển hướng dựa theo phân vai người dùng
        String targetPage = (acc.getRoleID() == 1) ? "redirect:/admin-dashboard" : "redirect:/dentist-dashboard";

        if (id != null && status != null) {
            try {
                AppointmentDAO dao = new AppointmentDAO();

                // ── BƯỚC 1: KIỂM TRA TRÙNG LỊCH (Khi bấm Duyệt lịch hẹn) ─────────────────
                if ("Confirmed".equals(status)) {
                    Appointment currentApp = dao.getAppointmentByID(id);
                    if (currentApp != null && dao.isDoctorBusy(currentApp.getDentistID(), 
                            currentApp.getAppointmentDate(), currentApp.getAppointmentTime())) {
                        return targetPage + "?error=busy"; // Trả về trang dashboard kèm mã lỗi bận việc
                    }
                }

                // ── BƯỚC 2: CẬP NHẬT TRẠNG THÁI XUỐNG DATABASE ─────────────────────────
                boolean success;
                if ("Cancelled".equals(status) && reason != null && !reason.isBlank()) {
                    // Nếu là hành động hủy lịch khám và có kèm lý do cụ thể
                    success = dao.updateStatusWithNotes(id, status, reason.trim());
                } else {
                    // Các trạng thái khám khác (Ví dụ: Pending, Done) hoặc hủy không lý do
                    success = dao.updateStatus(id, status);
                }

                // ── BƯỚC 3: ĐIỀU HƯỚNG KẾT QUẢ ─────────────────────────────────────────
                return targetPage + (success ? "?msg=success" : "?msg=fail");

            } catch (Exception e) {
                e.printStackTrace();
                return targetPage;
            }
        }

        return targetPage;
    }
}