package com.nhakhoa.dao;

import com.nhakhoa.model.Service;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class ServiceDAO {

    Connection conn = null;
    PreparedStatement ps = null;
    ResultSet rs = null;

    // ─────────────────────────────────────────────────────────────
    // 1. LẤY TẤT CẢ DỊCH VỤ (kèm ảnh, tên bác sĩ, id bác sĩ)
    // ─────────────────────────────────────────────────────────────
    public List<Service> getAllServices() {
        List<Service> list = new ArrayList<>();
        String sql =
            "SELECT s.ServiceID, s.ServiceName, s.Description, s.Price, s.ServiceImage, " +
            "       GROUP_CONCAT(u.FullName SEPARATOR ',') AS doctorNames, " +
            "       CONCAT('[', IFNULL(GROUP_CONCAT(u.UserID SEPARATOR ','),''), ']') AS doctorIdsJson " +
            "FROM Services s " +
            "LEFT JOIN DentistServices ds ON s.ServiceID = ds.ServiceID " +
            "LEFT JOIN Users u ON ds.DentistID = u.UserID " +
            "GROUP BY s.ServiceID, s.ServiceName, s.Description, s.Price, s.ServiceImage";
        try {
            conn = new DBContext().getConnection();
            ps   = conn.prepareStatement(sql);
            rs   = ps.executeQuery();
            while (rs.next()) {
                Service s = new Service();
                s.setServiceID(rs.getInt("ServiceID"));
                s.setServiceName(rs.getString("ServiceName"));
                s.setDescription(rs.getString("Description"));
                s.setPrice(rs.getDouble("Price"));
                s.setServiceImage(rs.getString("ServiceImage"));   // <-- MỚI

                String json = rs.getString("doctorIdsJson");
                if (json == null || json.equals("[null]") || json.equals("[]") || json.equals("[,]"))
                    json = "[]";
                // Đảm bảo dạng JSON hợp lệ: [1,2,3]
                if (!json.startsWith("[")) json = "[" + json + "]";
                s.setDoctorIdsJson(json);

                s.setDoctorNames(rs.getString("doctorNames"));
                list.add(s);
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            closeConnections();
        }
        return list;
    }

    // ─────────────────────────────────────────────────────────────
    // 2. THÊM DỊCH VỤ — trả về ID vừa tạo, nhận thêm imagePath
    // ─────────────────────────────────────────────────────────────
    public int addService(String name, String desc, double price, String imagePath) {
        String sql = "INSERT INTO Services (ServiceName, Description, Price, ServiceImage) VALUES (?, ?, ?, ?)";
        try {
            conn = new DBContext().getConnection();
            ps   = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
            ps.setString(1, name);
            ps.setString(2, desc);
            ps.setDouble(3, price);
            ps.setString(4, imagePath);   // NULL nếu không upload
            ps.executeUpdate();

            rs = ps.getGeneratedKeys();
            if (rs.next()) return rs.getInt(1);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            closeConnections();
        }
        return -1;
    }

    // ─────────────────────────────────────────────────────────────
    // 3. CẬP NHẬT DỊCH VỤ — có ảnh mới hoặc giữ ảnh cũ
    //    imagePath == null  →  giữ nguyên ảnh cũ trong DB
    // ─────────────────────────────────────────────────────────────
    public void updateService(int id, String name, String desc, double price, String imagePath) {
        String sql;
        if (imagePath != null) {
            sql = "UPDATE Services SET ServiceName=?, Description=?, Price=?, ServiceImage=? WHERE ServiceID=?";
        } else {
            sql = "UPDATE Services SET ServiceName=?, Description=?, Price=? WHERE ServiceID=?";
        }
        try {
            conn = new DBContext().getConnection();
            ps   = conn.prepareStatement(sql);
            ps.setString(1, name);
            ps.setString(2, desc);
            ps.setDouble(3, price);
            if (imagePath != null) {
                ps.setString(4, imagePath);
                ps.setInt(5, id);
            } else {
                ps.setInt(4, id);
            }
            ps.executeUpdate();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            closeConnections();
        }
    }

    // ─────────────────────────────────────────────────────────────
    // 4. XÓA DỊCH VỤ
    // ─────────────────────────────────────────────────────────────
    public void deleteService(int id) {
        try {
            conn = new DBContext().getConnection();

            ps = conn.prepareStatement("DELETE FROM DentistServices WHERE ServiceID=?");
            ps.setInt(1, id); ps.executeUpdate();

            ps = conn.prepareStatement("UPDATE Appointments SET ServiceID=NULL WHERE ServiceID=?");
            ps.setInt(1, id); ps.executeUpdate();

            ps = conn.prepareStatement("DELETE FROM Services WHERE ServiceID=?");
            ps.setInt(1, id); ps.executeUpdate();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            closeConnections();
        }
    }

    // ─────────────────────────────────────────────────────────────
    // 5. LẤY 1 DỊCH VỤ THEO ID
    // ─────────────────────────────────────────────────────────────
    public Service getServiceByID(int id) {
        String sql = "SELECT * FROM Services WHERE ServiceID=?";
        try {
            conn = new DBContext().getConnection();
            ps   = conn.prepareStatement(sql);
            ps.setInt(1, id);
            rs = ps.executeQuery();
            if (rs.next()) {
                Service s = new Service();
                s.setServiceID(rs.getInt("ServiceID"));
                s.setServiceName(rs.getString("ServiceName"));
                s.setDescription(rs.getString("Description"));
                s.setPrice(rs.getDouble("Price"));
                s.setServiceImage(rs.getString("ServiceImage"));
                return s;
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            closeConnections();
        }
        return null;
    }

    // ─────────────────────────────────────────────────────────────
    // 6. CẬP NHẬT BẢNG LIÊN KẾT BÁC SĨ – DỊCH VỤ
    // ─────────────────────────────────────────────────────────────
    public void updateDentistServices(int serviceId, String[] doctorIds) {
        try {
            conn = new DBContext().getConnection();

            ps = conn.prepareStatement("DELETE FROM DentistServices WHERE ServiceID=?");
            ps.setInt(1, serviceId);
            ps.executeUpdate();

            if (doctorIds != null && doctorIds.length > 0) {
                ps = conn.prepareStatement(
                        "INSERT INTO DentistServices (ServiceID, DentistID) VALUES (?, ?)");
                for (String docId : doctorIds) {
                    if (docId == null || docId.trim().isEmpty()) continue;
                    ps.setInt(1, serviceId);
                    ps.setInt(2, Integer.parseInt(docId.trim()));
                    ps.addBatch();
                }
                ps.executeBatch();
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            closeConnections();
        }
    }

    // ─────────────────────────────────────────────────────────────
    // ĐÓNG KẾT NỐI
    // ─────────────────────────────────────────────────────────────
    private void closeConnections() {
        try {
            if (rs   != null) rs.close();
            if (ps   != null) ps.close();
            if (conn != null) conn.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}