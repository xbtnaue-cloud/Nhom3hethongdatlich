package com.nhakhoa.controller;

import com.nhakhoa.dao.ServiceDAO;
import com.nhakhoa.model.Service;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;

import java.util.List;

@Controller // Đổi sang Annotation của Spring
public class ServiceClientController {

    // Thay thế hoàn toàn cho doGet, doPost và urlPatterns = {"/service-list"}
	@GetMapping("/service-list")
	public String getServiceListClient(Model model) {
	    try {
	        // 🌟 BƯỚC QUAN TRỌNG: Truyền biến activePage với giá trị "services"
	        // Nó phải khớp đúng với chữ 'services' trong header.html của ní
	        model.addAttribute("activePage", "services");

	        ServiceDAO dao = new ServiceDAO();
	        List<Service> listS = dao.getAllServices();

	        if (listS != null && !listS.isEmpty()) {
	            model.addAttribute("listS", listS);
	        } else {
	            model.addAttribute("message", "Hiện tại phòng khám đang cập nhật danh mục dịch vụ mới.");
	        }

	        return "service-list";

	    } catch (Exception e) {
	        e.printStackTrace();
	        model.addAttribute("error", "Không thể tải danh sách dịch vụ lúc này.");
	        return "error";
	    }
	}

    // Đề phòng trường hợp luồng cũ có các liên kết form bắn POST qua url này
    @PostMapping("/service-list")
    public String postServiceListClient(Model model) {
        return getServiceListClient(model); // Gọi lại hàm GET để đồng bộ xử lý
    }
}