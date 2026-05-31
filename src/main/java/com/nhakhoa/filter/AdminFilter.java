package com.nhakhoa.filter;

import com.nhakhoa.model.User;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

@Component // Đăng ký Interceptor này như một Bean của Spring
public class AdminFilter implements HandlerInterceptor {

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler)
            throws Exception {

        HttpSession session = request.getSession(false);
        User user = (session != null) ? (User) session.getAttribute("acc") : null;

        if (user != null) {
            int role = user.getRoleID();
            String uri = request.getRequestURI();

            // 1. Nếu là ADMIN (Role 1): Cho phép đi qua tất cả các URL
            if (role == 1) {
                return true; // true = cho đi tiếp
            }

            // 2. Nếu là BÁC SĨ (Role 2):
            if (role == 2) {
                // CHỈ CHẶN Dashboard của Admin
                if (uri.contains("/admin-dashboard")) {
                    response.sendRedirect(request.getContextPath() + "/dentist-dashboard");
                    return false; // false = chặn lại
                }
                // CHO PHÉP đi qua các trang dùng chung
                return true;
            }
        }

        // 3. Chưa đăng nhập hoặc sai quyền -> Đá về trang đăng nhập kèm thông báo
        if (session != null) {
            session.setAttribute("error", "Vui lòng đăng nhập quyền Bác sĩ/Admin!");
        }
        
        response.sendRedirect(request.getContextPath() + "/login");
        return false; // Chặn đứng request lại
    }
}