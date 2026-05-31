package com.nhakhoa.filter;

import org.springframework.stereotype.Component;
import jakarta.servlet.*; // Đổi hoàn toàn từ javax sang jakarta
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;

@Component // Đăng ký Filter này để Spring Boot tự động nhận diện và kích hoạt cho toàn hệ thống (thay cho @WebFilter)
public class EncodingFilter implements Filter {

    @Override
    public void init(FilterConfig filterConfig) throws ServletException {
        // Khởi tạo cấu hình nếu cần
    }

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException {
        
        HttpServletRequest req = (HttpServletRequest) request;
        HttpServletResponse res = (HttpServletResponse) response;

        // Thiết lập cấu hình tiếng Việt UTF-8 chuẩn Jakarta Servlet
        req.setCharacterEncoding("UTF-8");
        res.setCharacterEncoding("UTF-8");
        
        // Cho phép request tiếp tục đi tiếp đến các chốt chặn khác hoặc vào Controller
        chain.doFilter(request, response);
    }

    @Override
    public void destroy() {
        // Hủy cấu hình khi dừng server
    }
}