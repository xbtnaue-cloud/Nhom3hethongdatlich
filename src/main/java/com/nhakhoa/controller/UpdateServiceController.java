package com.nhakhoa.controller;

import com.nhakhoa.model.Service;
import com.nhakhoa.service.ServiceService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.UUID;

@Controller
public class UpdateServiceController {

    private static final String UPLOAD_DIR = System.getProperty("user.dir") + "/src/main/resources/static/uploads/services/";

    @Autowired
    private ServiceService serviceService; // Sử dụng Service chuẩn

    // ── 1. GET: Lấy thông tin dịch vụ đưa lên form sửa ──────────────────
    @GetMapping("/update-service")
    public String showEditServiceForm(@RequestParam(value = "id", required = false) Integer id, Model model) {
        if (id == null) return "redirect:/manage-services?error=invalidid";

        Service s = serviceService.getServiceByID(id);
        if (s == null) return "redirect:/manage-services?error=notfound";

        model.addAttribute("s", s);
        return "edit_service";
    }

    // ── 2. POST: Xử lý cập nhật ──────────────────────────────────────────
    @PostMapping("/update-service")
    public String updateService(
            @RequestParam("id") int id,
            @RequestParam("name") String name,
            @RequestParam("desc") String desc,
            @RequestParam("price") double price,
            @RequestParam(value = "serviceImage", required = false) MultipartFile filePart,
            @RequestParam(value = "doctorIds", required = false) String[] doctorIds) {

        try {
            String imagePath = null;

            if (filePart != null && !filePart.isEmpty()) {
                File dir = new File(UPLOAD_DIR);
                if (!dir.exists()) dir.mkdirs();

                String savedName = UUID.randomUUID().toString() + "_" + filePart.getOriginalFilename();
                Path path = Paths.get(UPLOAD_DIR + savedName);
                Files.write(path, filePart.getBytes());
                imagePath = "uploads/services/" + savedName;
            }

            // Gọi Service để cập nhật (Bạn cần đảm bảo ServiceService có hàm này)
            serviceService.updateService(id, name, desc, price, imagePath, doctorIds);

            return "redirect:/manage-services";

        } catch (Exception e) {
            e.printStackTrace();
            return "redirect:/manage-services?error=updatefail";
        }
    }
}