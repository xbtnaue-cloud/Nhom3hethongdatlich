package com.nhakhoa.controller;

import com.nhakhoa.dao.ServiceDAO;
import com.nhakhoa.model.Service;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.UUID;

@Controller // Đổi sang Annotation của Spring
public class UpdateServiceController {

    // Đường dẫn tuyệt đối lưu ảnh (Thay đổi cho phù hợp với máy ní, ví dụ lưu vào thư mục static của dự án)
    private static final String UPLOAD_DIR = System.getProperty("user.dir") + "/src/main/resources/static/uploads/services/";

    // ── 1. GET: Lấy thông tin dịch vụ đưa lên form sửa riêng biệt ──────────────────
    @GetMapping("/update-service")
    public String showEditServiceForm(@RequestParam(value = "id", required = false) Integer id, Model model) {
        if (id == null) {
            return "redirect:/manage-services?error=invalidid";
        }

        try {
            ServiceDAO dao = new ServiceDAO();
            Service s = dao.getServiceByID(id);

            if (s == null) {
                return "redirect:/manage-services?error=notfound";
            }

            model.addAttribute("s", s); // Khớp biến s cũ của ní
            return "edit_service"; // Mở file edit_service.html trong templates

        } catch (Exception e) {
            e.printStackTrace();
            return "redirect:/manage-services?error=invalidid";
        }
    }

    // ── 2. POST: Xử lý cập nhật dịch vụ kèm theo Up Ảnh (Hút từ Modal hoặc Form) ──
    @PostMapping("/update-service")
    public String updateService(
            @RequestParam("id") int id,
            @RequestParam("name") String name,
            @RequestParam("desc") String desc,
            @RequestParam("price") double price,
            @RequestParam(value = "doctorIds", required = false) String[] doctorIds,
            @RequestParam(value = "serviceImage", required = false) MultipartFile filePart) {

        try {
            String imagePath = null;

            // Xử lý upload ảnh mới bằng MultipartFile của Spring Boot nếu người dùng có chọn file
            if (filePart != null && !filePart.isEmpty()) {
                // Đảm bảo thư mục lưu trữ tồn tại
                File dir = new File(UPLOAD_DIR);
                if (!dir.exists()) {
                    dir.mkdirs();
                }

                // Tạo tên file ngẫu nhiên để tránh trùng lặp bằng UUID thay cho System.currentTimeMillis()
                String originalName = filePart.getOriginalFilename();
                String savedName = UUID.randomUUID().toString() + "_" + originalName;

                // Thực thi ghi file xuống đĩa cứng
                Path path = Paths.get(UPLOAD_DIR + savedName);
                Files.write(path, filePart.getBytes());

                // Đường dẫn tương đối lưu xuống Database để giao diện hiển thị được
                imagePath = "uploads/services/" + savedName;
            }

            // Gọi ServiceDAO cũ của ní để cập nhật thông tin cốt lõi
            ServiceDAO dao = new ServiceDAO();
            dao.updateService(id, name, desc, price, imagePath); // imagePath = null nếu không chọn ảnh mới

            // Cập nhật danh sách bác sĩ phụ trách dịch vụ này
            dao.updateDentistServices(id, doctorIds);

            return "redirect:/manage-services";

        } catch (Exception e) {
            e.printStackTrace();
            return "redirect:/manage-services?error=updatefail";
        }
    }
}