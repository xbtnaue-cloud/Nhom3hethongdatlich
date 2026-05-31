package com.nhakhoa.controller;

import com.nhakhoa.dao.ServiceDAO;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.nio.file.Paths;

@Controller // Đổi sang Annotation của Spring
public class AddServiceController {

    // Thay thế hoàn toàn cho doPost và @WebServlet(urlPatterns = {"/add-service"})
    @PostMapping("/add-service")
    public String addService(
            @RequestParam("name") String name,
            @RequestParam("desc") String desc,
            @RequestParam("price") double price,
            @RequestParam(value = "doctorIds", required = false) String[] doctorIds,
            @RequestParam("serviceImage") MultipartFile filePart) { // Dùng MultipartFile thay cho Part

        try {
            // ── 1. Xử lý upload ảnh ─────────────────────────────────────
            String imagePath = null;

            if (filePart != null && !filePart.isEmpty()) {
                // Lấy tên file gốc
                String originalName = Paths.get(filePart.getOriginalFilename())
                                           .getFileName()
                                           .toString();

                // Thêm timestamp tránh trùng tên file
                String savedName = System.currentTimeMillis() + "_" + originalName;

                // TRONG SPRING BOOT: Lưu vào thư mục tĩnh static để hiển thị được ngay
                // Bạn tạo sẵn thư mục: src/main/resources/static/uploads/services/ nhé
                String uploadDir = new File("src/main/resources/static/uploads/services/").getAbsolutePath();
                File dir = new File(uploadDir);
                if (!dir.exists()) dir.mkdirs();

                // Ghi file vào ổ đĩa bằng hàm transferTo của Spring
                File serverFile = new File(dir + File.separator + savedName);
                filePart.transferTo(serverFile);

                // Đường dẫn tương đối lưu vào DB (bỏ chữ static đi vì Spring Boot tự map static ra ngoài)
                imagePath = "uploads/services/" + savedName;
            }

            // ── 2. Lưu dịch vụ vào DB ────────────────────────────────────
            ServiceDAO dao = new ServiceDAO();
            int newServiceId = dao.addService(name, desc, price, imagePath);

            // ── 3. Lưu danh sách bác sĩ được chọn ────────────────────────
            if (newServiceId > 0 && doctorIds != null) {
                dao.updateDentistServices(newServiceId, doctorIds);
            }

            // Thành công thì redirect về trang danh sách dịch vụ
            return "redirect:/manage-services";

        } catch (Exception e) {
            e.printStackTrace();
            // Thất bại đá về kèm param báo lỗi
            return "redirect:/manage-services?error=addfail";
        }
    }
}