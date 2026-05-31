package com.nhakhoa.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class SpecialtyDAO {

    Connection conn = null;
    PreparedStatement ps = null;
    ResultSet rs = null;

    // Lấy tất cả chuyên khoa từ bảng Specialties
    public List<String> getAllSpecialties() {
        List<String> list = new ArrayList<>();
        String query = "SELECT SpecialtyName FROM Specialties ORDER BY SpecialtyID";
        try {
            conn = new DBContext().getConnection();
            ps = conn.prepareStatement(query);
            rs = ps.executeQuery();
            while (rs.next()) {
                list.add(rs.getString("SpecialtyName"));
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                if (rs   != null) rs.close();
                if (ps   != null) ps.close();
                if (conn != null) conn.close();
            } catch (SQLException e) { e.printStackTrace(); }
        }
        return list;
    }
}