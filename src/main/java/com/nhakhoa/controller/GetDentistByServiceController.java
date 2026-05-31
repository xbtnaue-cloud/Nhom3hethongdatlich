package com.nhakhoa.controller;

import com.nhakhoa.dao.AppointmentDAO;
import com.nhakhoa.model.User;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.List;

@Controller // Đổi sang Annotation của Spring
public class GetDentistByServiceController {

    // Nhận cả GET và POST tới endpoint /getDentists giống urlPatterns cũ
    @RequestMapping(value = "/getDentists", method = {RequestMethod.GET, RequestMethod.POST})
    @ResponseBody // CHỐT CHẶN: Ép Spring Boot trả về chuỗi text/html thuần thay vì tìm file giao diện
    public String getDentistsByService(@RequestParam(value = "serviceID", required = false) String serviceIDRaw) {
        
        System.out.println("[DEBUG] /getDentists called → serviceID = " + serviceIDRaw);

        // Sử dụng StringBuilder để nối chuỗi HTML cho tối ưu hiệu năng
        StringBuilder htmlOptions = new StringBuilder();

        if (serviceIDRaw == null || serviceIDRaw.isBlank()) {
            return "<option value=''>-- Vui lòng chọn dịch vụ trước --</option>";
        }

        try {
            int serviceID = Integer.parseInt(serviceIDRaw);
            AppointmentDAO dao = new AppointmentDAO();
            List<User> list = dao.getDentistsByService(serviceID);

            System.out.println("[DEBUG] getDentists → trả về " + list.size() + " BS cho serviceID=" + serviceID);

            if (list.isEmpty()) {
                htmlOptions.append("<option value=''>-- Chưa có bác sĩ cho dịch vụ này --</option>");
            } else {
                htmlOptions.append("<option value=''>-- Chọn bác sĩ --</option>");
                for (User d : list) {
                    htmlOptions.append("<option value='").append(d.getUserID()).append("'>")
                               .append(d.getFullName()).append("</option>");
                }
            }
        } catch (Exception e) {
            System.err.println("[ERROR] /getDentists: " + e.getMessage());
            e.printStackTrace();
            return "<option value=''>Lỗi tải danh sách bác sĩ</option>";
        }

        return htmlOptions.toString(); // Trả thẳng chuỗi các thẻ <option> về cho Ajax húp
    }
}