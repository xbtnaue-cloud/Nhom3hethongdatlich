package com.nhakhoa.dao;

import java.sql.Connection;
import java.sql.DriverManager;


public class DBContext {
    
    /* CẤU HÌNH THÔNG SỐ KẾT NỐI TẠI ĐÂY */
    private final String serverName = "localhost";
    private final String dbName = "Hethongnhakhoa"; // Tên database bạn đã tạo
    private final String portNumber = "3306";
    private final String userID = "root";     // Username mặc định của MySQL là root
    private final String password = "MySQL123"; // THAY BẰNG mật khẩu MySQL của bạn

    public Connection getConnection() throws Exception {
        // Chuỗi kết nối có hỗ trợ tiếng Việt (useUnicode=true)
        String url = "jdbc:mysql://" + serverName + ":" + portNumber + "/" + dbName 
                + "?useUnicode=true&characterEncoding=UTF-8";
        
        // Load Driver (Dành cho bản mysql-connector-java 8.0+)
        Class.forName("com.mysql.cj.jdbc.Driver");
        
        return DriverManager.getConnection(url, userID, password);
    }

    /* Test kết nối trực tiếp tại đây */
    public static void main(String[] args) {
        try {
            DBContext db = new DBContext();
            if (db.getConnection() != null) {
                System.out.println("Kết nối Database thành công!");
            }
        } catch (Exception e) {
            System.out.println("Lỗi kết nối: " + e.getMessage());
        }
    }
}