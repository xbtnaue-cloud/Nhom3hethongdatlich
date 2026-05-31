package com.nhakhoa.controller;

import com.nhakhoa.dao.ContactDAO;
import com.nhakhoa.model.User;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.SessionAttribute;
import jakarta.servlet.http.HttpSession;

@Controller // Sử dụng Controller của Spring
public class SendContactController {

    // ── 1. POST: Xử lý gửi tin nhắn Chat qua Ajax ─────────────────────────────
    @PostMapping("/send-contact-ajax")
    @ResponseBody // CHỐT CHẶN: Trả về chuỗi text thuần ("success", "error",...) cho Ajax húp thay vì tìm file HTML
    public String sendContactAjax(
            @RequestParam(value = "message", required = false) String message,
            @SessionAttribute(value = "acc", required = false) User acc,
            HttpSession session) {

        // Kiểm tra dữ liệu rỗng (Dùng hàm .isBlank() cực gọn của Java 17)
        if (message == null || message.isBlank()) {
            return "empty";
        }

        // Lấy ID cuộc trò chuyện hiện tại từ Session giống logic cũ
        Integer contactID = (Integer) session.getAttribute("myContactID");
        ContactDAO dao = new ContactDAO();

        try {
            if (contactID == null) {
                // --- TRƯỜNG HỢP: KHÁCH BẮT ĐẦU CUỘC CHAT MỚI ---
                Integer userId = null;
                String name = "Khách hàng ẩn danh";
                String email = "guest@dentalpro.com";

                // Nếu đã login, bốc thông tin thật từ đối tượng tài khoản ra
                if (acc != null) {
                    userId = acc.getUserID();
                    name = acc.getFullName();
                    email = acc.getEmail();
                }

                // Gọi hàm DAO nhận 4 tham số để lưu và lấy về ID tự tăng vừa tạo
                contactID = dao.insertContactAndReturnID(userId, name, email, message.trim());
                
                // Cất ID cuộc trò chuyện vào Session để lần nhắn kế tiếp không bị tạo cuộc hội thoại mới
                if (contactID != null && contactID > 0) {
                    session.setAttribute("myContactID", contactID);
                }
            } else {
                // --- TRƯỜNG HỢP: CHAT TIẾP VÀO CUỘC HỘI THOẠI ĐANG MỞ ───
                dao.insertChatMessage(contactID, "Patient", message.trim());
            }
            
            return "success"; // Phản hồi chuỗi báo thành công về cho frontend

        } catch (Exception e) {
            e.printStackTrace();
            return "error"; // Phản hồi chuỗi báo lỗi khi vướng ngoại lệ
        }
    }

    // ── 2. GET: Phòng trường hợp người dùng gõ trực tiếp URL lên trình duyệt ──
    @GetMapping("/send-contact-ajax")
    public String handleGetRequest() {
        return "redirect:/index"; // Điều hướng an toàn người dùng về lại trang chủ hệ thống
    }
}