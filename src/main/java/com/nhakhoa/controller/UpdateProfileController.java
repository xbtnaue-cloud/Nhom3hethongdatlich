package com.nhakhoa.controller;

import com.nhakhoa.dao.UserDAO;
import com.nhakhoa.model.User;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.SessionAttribute;
import jakarta.servlet.http.HttpSession;

@Controller // Đổi sang Annotation của Spring
public class UpdateProfileController {

    // 1. GET: Hiển thị trang Hồ sơ cá nhân (Phòng khi người dùng vào xem profile)
    @GetMapping("/profile")
    public String showProfile(@SessionAttribute(value = "acc", required = false) User acc) {
        if (acc == null) {
            return "redirect:/login"; // Chưa đăng nhập thì đá về trang login
        }
        return "profile"; // Mở file profile.html trong thư mục templates
    }

    // 2. POST: Xử lý dữ liệu khi người dùng bấm nút "Cập nhật thông tin"
    @PostMapping("/update-profile")
    public String updateProfile(
            @SessionAttribute(value = "acc", required = false) User acc,
            @RequestParam("fullName") String fullName,
            @RequestParam("phone") String phone,
            @RequestParam(value = "newPass", required = false) String newPass,
            HttpSession session, // Dùng để cập nhật lại session mới
            Model model) {

        // Kiểm tra session chốt chặn an toàn
        if (acc == null) {
            return "redirect:/login";
        }

        // Nếu mật khẩu mới trống, dùng lại mật khẩu cũ từ session giống logic cũ của ní
        String password = (newPass == null || newPass.isBlank()) ? acc.getPassword() : newPass.trim();

        try {
            UserDAO dao = new UserDAO();
            // 1. Cập nhật xuống Database thông qua DAO cũ
            dao.updateProfile(acc.getUserID(), fullName.trim(), phone.trim(), password);

            // 2. Đồng bộ lại đối tượng acc trong Session để Header ăn theo tên mới ngay lập tức
            acc.setFullName(fullName.trim());
            acc.setPhone(phone.trim());
            acc.setPassword(password);
            session.setAttribute("acc", acc);

            // 3. Bắn thông báo thành công ra giao diện
            model.addAttribute("success", "Cập nhật thông tin thành công!");
            return "profile"; // Ở lại trang profile.html để người dùng thấy thông báo

        } catch (Exception e) {
            e.printStackTrace();
            model.addAttribute("error", "Có lỗi xảy ra khi cập nhật!");
            return "profile";
        }
    }
}