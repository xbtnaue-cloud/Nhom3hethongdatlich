package com.nhakhoa.dao;

import com.nhakhoa.model.User;
import com.nhakhoa.model.Appointment;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class UserDAO {

    Connection conn = null;
    PreparedStatement ps = null;
    ResultSet rs = null;

    // =========================================================
    // HELPER: map một hàng ResultSet -> User (đầy đủ 9 trường)
    // =========================================================
 // Thay thế hàm mapFullUser trong UserDAO.java bằng đoạn này:
    private User mapFullUser(ResultSet rs) throws SQLException {
        String specialty = null;
        int statusID = 1; // Mặc định là 1 (Hoạt động)
        try {
            specialty = rs.getString("Specialty");
            statusID  = rs.getInt("StatusID"); // Chốt chặn quan trọng nhất ở đây
        } catch (SQLException e) {
            // Nếu cột chưa có trong DB thì vẫn để mặc định là 1
        }

        return new User(
            rs.getInt("UserID"),
            rs.getString("Username"),
            rs.getString("Password"),
            rs.getString("FullName"),
            rs.getString("Email"),
            rs.getString("Phone"),
            rs.getInt("RoleID"),
            specialty,
            statusID
        );
    }
 

    // =========================================================
    // HELPER: map một hàng ResultSet -> User (7 trường cơ bản,
    //         dùng cho bảng không có cột Specialty / StatusID)
    // =========================================================
 // Đổi từ private sang public là hết vàng liền nha ní
    public User mapBasicUser(ResultSet rs) throws SQLException {
        return new User(
            rs.getInt("UserID"),
            rs.getString("Username"),
            rs.getString("Password"),
            rs.getString("FullName"),
            rs.getString("Email"),
            rs.getString("Phone"),
            rs.getInt("RoleID")
        );
    }

    // --- 1. ĐĂNG NHẬP ---
    public User login(String username, String password) {
        String query = "SELECT * FROM Users WHERE Username = ? AND Password = ?";
        try {
            conn = new DBContext().getConnection();
            ps = conn.prepareStatement(query);
            ps.setString(1, username);
            ps.setString(2, password);
            rs = ps.executeQuery();
            if (rs.next()) {
                return mapFullUser(rs);
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            closeConnections();
        }
        return null;
    }

    // --- 2. ĐĂNG KÝ TÀI KHOẢN ---
    public boolean register(String user, String pass, String name,
                            String email, String phone, int roleID) {
        String query = "INSERT INTO Users (Username, Password, FullName, Email, Phone, RoleID) "
                     + "VALUES (?, ?, ?, ?, ?, ?)";
        try {
            conn = new DBContext().getConnection();
            ps = conn.prepareStatement(query);
            ps.setString(1, user);
            ps.setString(2, pass);
            ps.setString(3, name);
            ps.setString(4, email);
            ps.setString(5, phone);
            ps.setInt(6, roleID);
            return ps.executeUpdate() > 0;
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            closeConnections();
        }
        return false;
    }

    // --- 3. LẤY TẤT CẢ BÁC SĨ (dùng cho trang doctors.jsp) ---
    public List<User> getAllDentists() {
        List<User> list = new ArrayList<>();
        String query = "SELECT * FROM Users WHERE RoleID = 2";
        try {
            conn = new DBContext().getConnection();
            ps = conn.prepareStatement(query);
            rs = ps.executeQuery();
            while (rs.next()) {
                list.add(mapFullUser(rs));
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            closeConnections();
        }
        return list;
    }

    // --- 4. LẤY TẤT CẢ BỆNH NHÂN ---
    public List<User> getAllPatients() {
        List<User> list = new ArrayList<>();
        String query = "SELECT * FROM Users WHERE RoleID = 3";
        try {
            conn = new DBContext().getConnection();
            ps = conn.prepareStatement(query);
            rs = ps.executeQuery();
            while (rs.next()) {
                list.add(mapFullUser(rs));
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            closeConnections();
        }
        return list;
    }

    // --- 5. LẤY TẤT CẢ BÁC SĨ (alias dùng cho trang quản trị) ---
    public List<User> getAllDoctors() {
        return getAllDentists();   // logic giống nhau, tránh trùng lặp code
    }

    // --- 6. CẬP NHẬT HỒ SƠ NGƯỜI DÙNG ---
    public void updateProfile(int id, String name, String phone, String pass) {
        String sql = "UPDATE Users SET FullName = ?, Phone = ?, Password = ? WHERE UserID = ?";
        try {
            conn = new DBContext().getConnection();
            ps = conn.prepareStatement(sql);
            ps.setString(1, name);
            ps.setString(2, phone);
            ps.setString(3, pass);
            ps.setInt(4, id);
            ps.executeUpdate();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            closeConnections();
        }
    }

    // --- 7. KIỂM TRA TỒN TẠI ---
    public boolean checkUserExist(String username) {
        String query = "SELECT UserID FROM Users WHERE Username = ?";
        try {
            conn = new DBContext().getConnection();
            ps = conn.prepareStatement(query);
            ps.setString(1, username);
            rs = ps.executeQuery();
            if (rs.next()) return true;
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            closeConnections();
        }
        return false;
    }

    public boolean checkEmailExist(String email) {
        String query = "SELECT UserID FROM Users WHERE Email = ?";
        try {
            conn = new DBContext().getConnection();
            ps = conn.prepareStatement(query);
            ps.setString(1, email);
            rs = ps.executeQuery();
            if (rs.next()) return true;
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            closeConnections();
        }
        return false;
    }

    // --- 8. CẬP NHẬT MẬT KHẨU ---
    public boolean updatePassword(String email, String newPass) {
        String query = "UPDATE Users SET Password = ? WHERE Email = ?";
        try {
            conn = new DBContext().getConnection();
            ps = conn.prepareStatement(query);
            ps.setString(1, newPass);
            ps.setString(2, email);
            return ps.executeUpdate() > 0;
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            closeConnections();
        }
        return false;
    }

    // --- 9. LẤY USER THEO ID ---
    public User getUserByID(int id) {
        String query = "SELECT * FROM Users WHERE UserID = ?";
        try {
            conn = new DBContext().getConnection();
            ps = conn.prepareStatement(query);
            ps.setInt(1, id);
            rs = ps.executeQuery();
            if (rs.next()) {
                return mapFullUser(rs);
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            closeConnections();
        }
        return null;
    }

    // --- 10. THÊM BÁC SĨ MỚI ---
    public void insertDoctor(String user, String pass, String name,
                             String email, String phone, String specialty) {
        String query = "INSERT INTO Users "
                     + "(Username, Password, FullName, Email, Phone, RoleID, Specialty, StatusID) "
                     + "VALUES (?, ?, ?, ?, ?, 2, ?, 1)";
        try {
            conn = new DBContext().getConnection();
            ps = conn.prepareStatement(query);
            ps.setString(1, user);
            ps.setString(2, pass);
            ps.setString(3, name);
            ps.setString(4, email);
            ps.setString(5, phone);
            ps.setString(6, specialty);
            ps.executeUpdate();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            closeConnections();
        }
    }

    // --- 11. CẬP NHẬT THÔNG TIN BÁC SĨ ---
    public void updateDoctor(int id, String name, String email, String phone,
            String user, String specialty, int statusID, String password) {
// Thêm Password = ? vào câu lệnh UPDATE
String query = "UPDATE Users "
    + "SET FullName = ?, Email = ?, Phone = ?, Username = ?, "
    + "    Specialty = ?, StatusID = ?, Password = ? "
    + "WHERE UserID = ?";
try {
conn = new DBContext().getConnection();
ps = conn.prepareStatement(query);
ps.setString(1, name);
ps.setString(2, email);
ps.setString(3, phone);
ps.setString(4, user);
ps.setString(5, specialty);
ps.setInt(6, statusID);
ps.setString(7, password); // Gán giá trị mật khẩu
ps.setInt(8, id);
ps.executeUpdate();
} catch (Exception e) {
e.printStackTrace();
} finally {
closeConnections();
}
}

    // --- 12. XÓA NGƯỜI DÙNG ---
    public boolean deleteUser(int userID) {
        try {
            conn = new DBContext().getConnection();

            // Xóa liên kết dịch vụ trước
            ps = conn.prepareStatement("DELETE FROM DentistServices WHERE DentistID = ?");
            ps.setInt(1, userID);
            ps.executeUpdate();

            // Null hóa lịch hẹn liên quan
            ps = conn.prepareStatement("UPDATE Appointments SET DentistID = NULL WHERE DentistID = ?");
            ps.setInt(1, userID);
            ps.executeUpdate();

            // Xóa user
            ps = conn.prepareStatement("DELETE FROM Users WHERE UserID = ?");
            ps.setInt(1, userID);
            return ps.executeUpdate() > 0;

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            closeConnections();
        }
        return false;
    }

    // --- 13. CẬP NHẬT THÔNG TIN BỆNH NHÂN ---
    public void updatePatient(int id, String name, String email, String phone) {
        String sql = "UPDATE Users SET FullName = ?, Email = ?, Phone = ? WHERE UserID = ?";
        try {
            conn = new DBContext().getConnection();
            ps = conn.prepareStatement(sql);
            ps.setString(1, name);
            ps.setString(2, email);
            ps.setString(3, phone);
            ps.setInt(4, id);
            ps.executeUpdate();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            closeConnections();
        }
    }

 // --- 16. LẤY DANH SÁCH CHUYÊN KHOA CHO DROPDOWN ---
    public List<String> getAllSpecialties() {
        List<String> list = new ArrayList<>();
        String query = "SELECT SpecialtyName FROM Specialties";
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
            closeConnections();
        }
        return list;
    }
    // --- 14. LỊCH SỬ KHÁM BỆNH ---
    public List<Appointment> getPatientMedicalHistory(int patientId) {
        List<Appointment> list = new ArrayList<>();
        String sql = "SELECT a.AppointmentDate, "
                   + "COALESCE(s.ServiceName, N'Dịch vụ tổng quát') AS ServiceName, "
                   + "COALESCE(s.Price, 0)                          AS Price, "
                   + "COALESCE(u.FullName, N'Y tá trực')            AS dName "
                   + "FROM Appointments a "
                   + "LEFT JOIN Services     s ON a.ServiceID  = s.ServiceID "
                   + "LEFT JOIN Users        u ON a.DentistID  = u.UserID "
                   + "WHERE a.PatientID = ? AND a.Status = 'Completed' "
                   + "ORDER BY a.AppointmentDate DESC";
        try {
            conn = new DBContext().getConnection();
            ps = conn.prepareStatement(sql);
            ps.setInt(1, patientId);
            rs = ps.executeQuery();
            while (rs.next()) {
                Appointment app = new Appointment();
                app.setAppointmentDate(rs.getDate("AppointmentDate"));
                app.setServiceName(rs.getString("ServiceName"));
                app.setPrice(rs.getDouble("Price"));
                app.setDentistName(rs.getString("dName"));
                list.add(app);
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            closeConnections();
        }
        return list;
    }
 // --- 17. LẤY TOÀN BỘ NGƯỜI DÙNG (Cho trang quản lý phân quyền) ---
    public List<User> getAllUsers() {
        List<User> list = new ArrayList<>();
        String query = "SELECT * FROM Users ORDER BY RoleID ASC, UserID DESC";
        try {
            conn = new DBContext().getConnection();
            ps = conn.prepareStatement(query);
            rs = ps.executeQuery();
            while (rs.next()) {
                list.add(mapFullUser(rs));
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            closeConnections();
        }
        return list;
    }
    
 // Đếm tổng số Bệnh nhân (RoleID = 3)
    public int getTotalPatients() {
        String query = "SELECT COUNT(*) FROM Users WHERE RoleID = 3";
        try {
            conn = new DBContext().getConnection();
            ps = conn.prepareStatement(query);
            rs = ps.executeQuery();
            if (rs.next()) return rs.getInt(1);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            closeConnections();
        }
        return 0;
    }

    // Đếm tổng số Bác sĩ (RoleID = 2)
    public int getTotalDoctors() {
        String query = "SELECT COUNT(*) FROM Users WHERE RoleID = 2";
        try {
            conn = new DBContext().getConnection();
            ps = conn.prepareStatement(query);
            rs = ps.executeQuery();
            if (rs.next()) return rs.getInt(1);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            closeConnections();
        }
        return 0;
    }

    // --- 18. CẬP NHẬT TRẠNG THÁI KHÓA/MỞ KHÓA ---
    public void updateUserStatus(int id, int status) {
        String sql = "UPDATE Users SET StatusID = ? WHERE UserID = ?";
        try {
            conn = new DBContext().getConnection();
            ps = conn.prepareStatement(sql);
            ps.setInt(1, status);
            ps.setInt(2, id);
            ps.executeUpdate();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            closeConnections();
        }
    }
    
    public int getActiveDoctorsCount() {
        int count = 0;
        // Lọc theo RoleID = 2 (Bác sĩ) và StatusID = 1 (Đang trực)
        String query = "SELECT COUNT(*) FROM Users WHERE RoleID = 2 AND StatusID = 1";
        try {
            conn = new DBContext().getConnection();
            ps = conn.prepareStatement(query);
            rs = ps.executeQuery();
            if (rs.next()) {
                count = rs.getInt(1);
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            // Đảm bảo bác sĩ đã có hàm closeConnections() trong UserDAO
            closeConnections(); 
        }
        return count;
    }

    // --- 19. CẬP NHẬT VAI TRÒ (PHÂN QUYỀN) ---
    public void updateUserRole(int id, int roleID) {
        String sql = "UPDATE Users SET RoleID = ? WHERE UserID = ?";
        try {
            conn = new DBContext().getConnection();
            ps = conn.prepareStatement(sql);
            ps.setInt(1, roleID);
            ps.setInt(2, id);
            ps.executeUpdate();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            closeConnections();
        }
    }
    
    public void updatePasswordById(int id, String newPass) {
        String sql = "UPDATE Users SET Password = ? WHERE UserID = ?";
        try {
            conn = new DBContext().getConnection();
            ps = conn.prepareStatement(sql);
            ps.setString(1, newPass);
            ps.setInt(2, id);
            ps.executeUpdate();
        } catch (Exception e) {}
    }

    public void updateUserByAdmin(int id, String name, String email, String phone, int roleID) {
        String sql = "UPDATE Users SET FullName = ?, Email = ?, Phone = ?, RoleID = ? WHERE UserID = ?";
        try {
            conn = new DBContext().getConnection();
            ps = conn.prepareStatement(sql);
            ps.setString(1, name);
            ps.setString(2, email);
            ps.setString(3, phone);
            ps.setInt(4, roleID);
            ps.setInt(5, id);
            ps.executeUpdate();
        } catch (Exception e) {}
    }
    // --- 15. ĐÓNG KẾT NỐI ---
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