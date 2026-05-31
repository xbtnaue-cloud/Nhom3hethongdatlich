package com.nhakhoa.filter; // Giữ nguyên đúng package .filter cũ theo cây thư mục của ní

import com.nhakhoa.model.User;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

@Component // Đăng ký Interceptor này thành một Bean để Spring Boot tự nhận diện
public class AuthFilter implements HandlerInterceptor {

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler)
            throws Exception {

        HttpSession session = request.getSession(false);
        User user = (session != null) ? (User) session.getAttribute("acc") : null;

        // In ra Console để debug xem ai đang vào trang đặt lịch (đồng thời để biến 'user' không bị báo cảnh báo vàng)
        System.out.println(">>> [AuthFilter Interceptor] Người dùng vào trang booking: " 
                + (user != null ? user.getFullName() : "Khách vãng lai"));

        // --- ĐÃ CHUYỂN SANG INTERCEPTOR: KHÔNG REDIRECT NỮA ---
        // Trả về true để cho phép tất cả các request đi tiếp vào /booking.
        // BookingController của Spring Boot sẽ tự xử lý: nếu có user thì tự điền, nếu null thì coi là khách vãng lai.
        return true; 
    }
}