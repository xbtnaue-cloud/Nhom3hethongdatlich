package com.nhakhoa.controller;

import com.nhakhoa.model.User;
import com.nhakhoa.service.AppointmentService;
import com.nhakhoa.service.UserService;
import com.nhakhoa.dto.PatientDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.SessionAttribute;
import com.nhakhoa.dto.AppointmentDTO;
import java.util.Collections;
import java.util.List;

@Controller
public class PatientController {

    @Autowired
    private UserService userService;

    @Autowired
    private AppointmentService appointmentService;

    // 1. DÀNH CHO NÚT "HỒ SƠ" (API AJAX)
    @GetMapping("/patient-detail")
    @ResponseBody
    public List<AppointmentDTO> getPatientDetail(
            @RequestParam(value = "id", required = false) Integer id,
            @RequestParam(value = "phone", required = false) String phone) {

        // Nếu là khách vãng lai (id = 0 hoặc null), dùng SĐT để tìm
        if ((id == null || id == 0) && phone != null && !phone.equals("N/A")) {
            return appointmentService.getHistoryByPhone(phone); // Hàm này phải trả về danh sách có giá tiền
        }
        // Nếu là khách có tài khoản
        return appointmentService.getHistoryByPatientID(id);
    }

    // 2. DÀNH CHO TRANG QUẢN LÝ BỆNH NHÂN
    @GetMapping("/patients")
    public String managePatients(
            @SessionAttribute(value = "acc", required = false) User acc,
            @RequestParam(value = "message", required = false) String msg,
            Model model) {

        if (acc == null || (acc.getRoleID() != 1 && acc.getRoleID() != 2)) {
            return "redirect:/login";
        }

        try {
            List<PatientDTO> listP;
            if (acc.getRoleID() == 1) {
                listP = userService.getAllPatientsDTO();
            } else {
                int dentistID = acc.getUserID();
                listP = appointmentService.getPatientsByDentist(dentistID);
            }

            model.addAttribute("listP", listP);
            model.addAttribute("activePage", "patients");
            return "patients";
        } catch (Exception e) {
            e.printStackTrace(); 
            return "error"; 
        }
    }

    @PostMapping("/patients")
    public String managePatientsPost(@SessionAttribute(value = "acc", required = false) User acc, 
                                     @RequestParam(value = "message", required = false) String msg, 
                                     Model model) {
        return managePatients(acc, msg, model);
    }
}
   