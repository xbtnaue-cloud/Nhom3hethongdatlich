package com.nhakhoa.controller;

import com.nhakhoa.service.ServiceService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.nio.file.Paths;

@Controller
public class AddServiceController {

    @Autowired
    private ServiceService serviceService; // Inject Service thay vì new DAO

    @PostMapping("/add-service")
    public String addService(
            @RequestParam("name") String name,
            @RequestParam("desc") String desc,
            @RequestParam("price") double price,
            @RequestParam(value = "doctorIds", required = false) String[] doctorIds,
            @RequestParam("serviceImage") MultipartFile filePart) {

        try {
            String imagePath = null;

            // 1. Xử lý upload ảnh
            if (filePart != null && !filePart.isEmpty()) {
                String originalName = Paths.get(filePart.getOriginalFilename()).getFileName().toString();
                String savedName = System.currentTimeMillis() + "_" + originalName;
                
                String uploadDir = new File("src/main/resources/static/uploads/services/").getAbsolutePath();
                File dir = new File(uploadDir);
                if (!dir.exists()) dir.mkdirs();

                File serverFile = new File(dir + File.separator + savedName);
                filePart.transferTo(serverFile);
                imagePath = "uploads/services/" + savedName;
            }

            // 2. Gọi Service để thêm dịch vụ
            // LƯU Ý: Bạn cần tạo hàm addService và updateDentistServices trong ServiceService nhé!
            int newServiceId = serviceService.addService(name, desc, price, imagePath);

            // 3. Cập nhật bác sĩ
            if (newServiceId > 0 && doctorIds != null) {
                serviceService.updateDentistServices(newServiceId, doctorIds);
            }

            return "redirect:/manage-services";

        } catch (Exception e) {
            e.printStackTrace();
            return "redirect:/manage-services?error=addfail";
        }
    }
}