package com.nhakhoa.dao;

import com.nhakhoa.model.Appointment;
import com.nhakhoa.model.User;
import com.nhakhoa.model.Service;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class AppointmentDAO {
    Connection conn = null;
    PreparedStatement ps = null;
    ResultSet rs = null;

    // --- 1. HÀM ĐẶT LỊCH HẸN MỚI (Đầy đủ tham số ServiceID) ---
    public void addAppointment(Integer patientID, int dentistID, int serviceID, String date, String time, String notes) {
        String query = "INSERT INTO Appointments (PatientID, DentistID, ServiceID, AppointmentDate, AppointmentTime, Status, Notes) "
                     + "VALUES (?, ?, ?, ?, ?, 'Pending', ?)";
        try {
            conn = new DBContext().getConnection();
            ps = conn.prepareStatement(query);
            
            // Dấu ? số 1: PatientID (Cho phép null nếu khách không đăng nhập)
            if (patientID != null && patientID != 0) {
                ps.setInt(1, patientID);
            } else {
                ps.setNull(1, java.sql.Types.INTEGER);
            }
            
            ps.setInt(2, dentistID);   // Dấu ? số 2: DentistID
            ps.setInt(3, serviceID);   // Dấu ? số 3: ServiceID
            ps.setString(4, date);     // Dấu ? số 4: Date
            ps.setString(5, time);     // Dấu ? số 5: Time
            ps.setString(6, notes);    // Dấu ? số 6: Notes (Chứa thông tin khách vãng lai nếu có)
            
            ps.executeUpdate();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            closeConnections();
        }
    }

    // --- 2. LẤY DANH SÁCH LỊCH HẸN CỦA MỘT BỆNH NHÂN (Dùng cho trang Lịch sử cá nhân) ---
    public List<Appointment> getAppointmentsByPatient(int patientID) {
        List<Appointment> list = new ArrayList<>();
        String query = "SELECT * FROM Appointments WHERE PatientID = ? ORDER BY AppointmentDate DESC";
        try {
            conn = new DBContext().getConnection();
            ps = conn.prepareStatement(query);
            ps.setInt(1, patientID);
            rs = ps.executeQuery();
            while (rs.next()) {
                list.add(new Appointment(
                    rs.getInt("AppointmentID"),
                    rs.getInt("PatientID"),
                    rs.getInt("DentistID"),
                    rs.getDate("AppointmentDate"),
                    rs.getTime("AppointmentTime"),
                    rs.getString("Status"),
                    rs.getString("Notes")
                ));
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            closeConnections();
        }
        return list;
    }

    // --- 3. LỌC NHA SĨ THEO DỊCH VỤ (Chuẩn logic chuyên khoa) ---
    public List<User> getDentistsByService(int serviceID) {
        List<User> list = new ArrayList<>();
        String query = "SELECT u.UserID, u.FullName FROM Users u " +
                       "JOIN DentistServices ds ON u.UserID = ds.DentistID " +
                       "WHERE ds.ServiceID = ? AND u.RoleID = 2 " +
                       "ORDER BY u.FullName";
        try {
            conn = new DBContext().getConnection();
            ps = conn.prepareStatement(query);
            ps.setInt(1, serviceID);
            rs = ps.executeQuery();
            while (rs.next()) {
                User user = new User();
                user.setUserID(rs.getInt("UserID"));
                user.setFullName(rs.getString("FullName"));
                list.add(user);
            }
            System.out.println("[DEBUG] getDentistsByService(" + serviceID + ") → " + list.size() + " bác sĩ");
        } catch (Exception e) {
            System.err.println("[ERROR] getDentistsByService: " + e.getMessage());
            e.printStackTrace();
        } finally {
            closeConnections();
        }
        return list;
    }

    // --- 4. LẤY TẤT CẢ LỊCH HẸN (Dành cho Admin - TRIPLE JOIN lấy tên BS và Dịch vụ) ---
    public List<Appointment> getAllAppointments() {
        List<Appointment> list = new ArrayList<>();
        // u1: Bệnh nhân | u2: Nha sĩ | s: Dịch vụ
        String query = "SELECT a.*, " +
                       "u1.FullName AS pName, u1.Phone AS pPhone, " +
                       "u2.FullName AS dName, " +
                       "s.ServiceName " +
                       "FROM Appointments a " +
                       "LEFT JOIN Users u1 ON a.PatientID = u1.UserID " + 
                       "LEFT JOIN Users u2 ON a.DentistID = u2.UserID " + 
                       "LEFT JOIN Services s ON a.ServiceID = s.ServiceID " + 
                       "ORDER BY CASE WHEN a.Status = 'Pending' THEN 1 ELSE 2 END, " +
                       "a.AppointmentDate DESC, a.AppointmentTime DESC";
        try {
            conn = new DBContext().getConnection();
            ps = conn.prepareStatement(query);
            rs = ps.executeQuery();
            while (rs.next()) {
                Appointment app = new Appointment();
                app.setAppointmentID(rs.getInt("AppointmentID"));
                app.setPatientID(rs.getInt("PatientID"));
                app.setDentistID(rs.getInt("DentistID"));
                app.setAppointmentDate(rs.getDate("AppointmentDate"));
                app.setAppointmentTime(rs.getTime("AppointmentTime"));
                app.setStatus(rs.getString("Status"));
                
                // Gán tên Bác sĩ và Dịch vụ từ kết quả JOIN
                app.setDentistName(rs.getString("dName"));
                app.setServiceName(rs.getString("ServiceName"));
                
                String fullNotes = rs.getString("Notes");
                app.setNotes(fullNotes);
                
                String dbName = rs.getString("pName");
                String dbPhone = rs.getString("pPhone");
                
                if (dbName != null && !dbName.isEmpty()) {
                    app.setPatientName(dbName);
                    app.setPhoneNumber(dbPhone);
                } else if (fullNotes != null && fullNotes.contains("【KHÁCH VÃNG LAI:")) {
                    try {
                        int nameStart = fullNotes.indexOf("KHÁCH VÃNG LAI:") + 15;
                        int nameEnd = fullNotes.indexOf(" - SĐT:");
                        int phoneStart = fullNotes.indexOf("SĐT:") + 4;
                        int phoneEnd = fullNotes.indexOf("】");
                        
                        app.setPatientName(fullNotes.substring(nameStart, nameEnd).trim());
                        app.setPhoneNumber(fullNotes.substring(phoneStart, phoneEnd).trim());
                    } catch (Exception e) {
                        app.setPatientName("Khách vãng lai");
                        app.setPhoneNumber("N/A");
                    }
                } else {
                    app.setPatientName("Khách vãng lai");
                    app.setPhoneNumber("N/A");
                }
                list.add(app);
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            closeConnections();
        }
        return list;
    }

    // --- 5. LẤY LỊCH HẸN THEO ID (Để trang chi tiết/update) ---
    public Appointment getAppointmentByID(int id) {
        String query = "SELECT a.*, u1.FullName as pName, u1.Phone as pPhone, u2.FullName as dName, s.ServiceName " +
                       "FROM Appointments a " +
                       "LEFT JOIN Users u1 ON a.PatientID = u1.UserID " +
                       "LEFT JOIN Users u2 ON a.DentistID = u2.UserID " +
                       "LEFT JOIN Services s ON a.ServiceID = s.ServiceID " +
                       "WHERE a.AppointmentID = ?";
        try {
            conn = new DBContext().getConnection();
            ps = conn.prepareStatement(query);
            ps.setInt(1, id);
            rs = ps.executeQuery();
            if (rs.next()) {
                Appointment app = new Appointment();
                app.setAppointmentID(rs.getInt("AppointmentID"));
                app.setPatientID(rs.getInt("PatientID"));
                app.setDentistID(rs.getInt("DentistID"));
                app.setAppointmentDate(rs.getDate("AppointmentDate"));
                app.setAppointmentTime(rs.getTime("AppointmentTime"));
                app.setStatus(rs.getString("Status"));
                app.setNotes(rs.getString("Notes"));
                app.setDentistName(rs.getString("dName"));
                app.setServiceName(rs.getString("ServiceName"));

                String name = rs.getString("pName");
                if (name != null) {
                    app.setPatientName(name);
                    app.setPhoneNumber(rs.getString("pPhone"));
                } else {
                    app.setPatientName("Khách vãng lai");
                    app.setPhoneNumber("N/A");
                }
                return app;
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            closeConnections();
        }
        return null;
    }

    // --- 6. CẬP NHẬT TRẠNG THÁI ---
    public boolean updateStatus(int appointmentID, String status) {
        String query = "UPDATE Appointments SET Status = ? WHERE AppointmentID = ?";
        try {
            conn = new DBContext().getConnection();
            ps = conn.prepareStatement(query);
            ps.setString(1, status);
            ps.setInt(2, appointmentID);
            return ps.executeUpdate() > 0;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        } finally {
            closeConnections();
        }
    }

    // --- 7. KIỂM TRA BÁC SĨ CÓ BẬN KHÔNG ---
    public boolean isDoctorBusy(int dentistID, java.sql.Date date, java.sql.Time time) {
        String query = "SELECT * FROM Appointments WHERE DentistID = ? AND AppointmentDate = ? AND AppointmentTime = ? AND Status = 'Confirmed'";
        try {
            conn = new DBContext().getConnection();
            ps = conn.prepareStatement(query);
            ps.setInt(1, dentistID);
            ps.setDate(2, date);
            ps.setTime(3, time);
            rs = ps.executeQuery();
            return rs.next(); 
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        } finally {
            closeConnections();
        }
    }

    // --- 8. LẤY TOÀN BỘ DANH MỤC DỊCH VỤ ---
    public List<Service> getAllServices() {
        List<Service> list = new ArrayList<>();
        String query = "SELECT ServiceID, ServiceName, Price FROM Services ORDER BY ServiceName";
        try {
            conn = new DBContext().getConnection();
            ps = conn.prepareStatement(query);
            rs = ps.executeQuery();
            while (rs.next()) {
                Service s = new Service();
                s.setServiceID(rs.getInt("ServiceID"));
                s.setServiceName(rs.getString("ServiceName"));
                s.setPrice(rs.getDouble("Price"));
                list.add(s);
            }
            System.out.println("[DEBUG] getAllServices() → " + list.size() + " dịch vụ");
        } catch (Exception e) {
            System.err.println("[ERROR] getAllServices: " + e.getMessage());
            e.printStackTrace();
        } finally {
            closeConnections();
        }
        return list;
    }
    public List<Appointment> getAppointmentsByUserId(int userId) {
        List<Appointment> list = new ArrayList<>();
        // Chú ý: Sử dụng AppointmentDate và DentistID để khớp với DB của bạn
        String sql = "SELECT a.*, s.ServiceName, u.FullName as dName " +
                     "FROM Appointments a " +
                     "JOIN Services s ON a.ServiceID = s.ServiceID " +
                     "JOIN Users u ON a.DentistID = u.UserID " +
                     "WHERE a.PatientID = ? " +
                     "ORDER BY a.AppointmentDate DESC, a.AppointmentTime DESC";
        try {
            conn = new DBContext().getConnection();
            ps = conn.prepareStatement(sql);
            ps.setInt(1, userId);
            rs = ps.executeQuery();
            while (rs.next()) {
                Appointment app = new Appointment();
                app.setAppointmentID(rs.getInt("AppointmentID"));
                app.setPatientID(rs.getInt("PatientID"));
                app.setDentistID(rs.getInt("DentistID"));
                app.setAppointmentDate(rs.getDate("AppointmentDate"));
                app.setAppointmentTime(rs.getTime("AppointmentTime"));
                app.setStatus(rs.getString("Status"));
                app.setNotes(rs.getString("Notes"));
                
                // Gán tên dịch vụ và tên bác sĩ từ kết quả JOIN
                app.setServiceName(rs.getString("ServiceName"));
                app.setDentistName(rs.getString("dName")); // Đảm bảo model có setDentistName
                
                list.add(app);
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            closeConnections();
        }
        return list;
    }
    
    public List<Appointment> getAppointmentsByPhone(String phone) {
        List<Appointment> list = new ArrayList<>();
        // SỬA TẠI ĐÂY: Thêm s.Price vào danh sách SELECT
        String sql = "SELECT a.*, " +
                     "uP.FullName AS pName, uP.Phone AS pPhone, " +
                     "uD.FullName AS dName, " +
                     "s.ServiceName, s.Price " + // <--- PHẢI CÓ s.Price Ở ĐÂY
                     "FROM Appointments a " +
                     "LEFT JOIN Users uP ON a.PatientID = uP.UserID " +
                     "LEFT JOIN Users uD ON a.DentistID = uD.UserID " +
                     "LEFT JOIN Services s ON a.ServiceID = s.ServiceID " +
                     "WHERE uP.Phone = ? OR a.Notes LIKE ? " +
                     "ORDER BY a.AppointmentDate DESC, a.AppointmentTime DESC";
        try {
            conn = new DBContext().getConnection();
            ps = conn.prepareStatement(sql);
            ps.setString(1, phone);
            ps.setString(2, "%" + phone + "%");
            rs = ps.executeQuery();
            
            while (rs.next()) {
                Appointment app = new Appointment();
                app.setAppointmentID(rs.getInt("AppointmentID"));
                app.setAppointmentDate(rs.getDate("AppointmentDate"));
                app.setAppointmentTime(rs.getTime("AppointmentTime"));
                app.setStatus(rs.getString("Status"));
                app.setServiceName(rs.getString("ServiceName") != null ? rs.getString("ServiceName") : "Khám tổng quát");
                app.setDentistName(rs.getString("dName"));
                
                // --- CỰC KỲ QUAN TRỌNG: Gán giá tiền từ Database vào Object ---
                // Nếu không có dòng này, app.getPrice() sẽ luôn trả về 0.0
                app.setPrice(rs.getDouble("Price")); 
                
                String fullNotes = rs.getString("Notes");
                app.setNotes(fullNotes);

                // Logic xử lý tên bệnh nhân (Giữ nguyên của bác sĩ - rất tốt)
                String dbName = rs.getString("pName");
                if (dbName != null && !dbName.isEmpty()) {
                    app.setPatientName(dbName);
                } else if (fullNotes != null && fullNotes.contains("【KHÁCH VÃNG LAI:")) {
                    try {
                        int nameStart = fullNotes.indexOf("KHÁCH VÃNG LAI:") + 15;
                        int nameEnd = fullNotes.indexOf(" - SĐT:");
                        if (nameStart > 14 && nameEnd > nameStart) {
                            app.setPatientName(fullNotes.substring(nameStart, nameEnd).trim());
                        } else {
                            app.setPatientName("Khách vãng lai");
                        }
                    } catch (Exception e) {
                        app.setPatientName("Khách vãng lai");
                    }
                } else {
                    app.setPatientName("Khách hàng");
                }
                
                list.add(app);
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            closeConnections();
        }
        return list;
    }
    
    public List<User> getAllDoctors() {
        List<User> list = new ArrayList<>();
        // Thêm Email và Phone vào câu SELECT
        String query = "SELECT UserID, FullName, Email, Phone FROM Users WHERE RoleID = 2 ORDER BY FullName";
        try {
            conn = new DBContext().getConnection();
            ps = conn.prepareStatement(query);
            rs = ps.executeQuery();
            while (rs.next()) {
                User user = new User();
                user.setUserID(rs.getInt("UserID"));
                user.setFullName(rs.getString("FullName"));
                user.setEmail(rs.getString("Email")); // Thêm dòng này
                user.setPhone(rs.getString("Phone")); // Thêm dòng này
                list.add(user);
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            closeConnections();
        }
        return list;
    }
    
 // 1. Tổng doanh thu (Chỉ tính các lịch hẹn đã hoàn thành)
 // Tính tổng doanh thu từ các lịch hẹn đã Hoàn thành (Completed)
    public double getTotalRevenue() {
        String query = "SELECT SUM(s.Price) FROM Appointments a "
                     + "JOIN Services s ON a.ServiceID = s.ServiceID "
                     + "WHERE a.Status = 'Completed'";
        try {
            conn = new DBContext().getConnection();
            ps = conn.prepareStatement(query);
            rs = ps.executeQuery();
            if (rs.next()) return rs.getDouble(1);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            closeConnections();
        }
        return 0;
    }
    
    public List<Appointment> getAppointmentsByDentist(int dentistID) {
        List<Appointment> list = new ArrayList<>();
        String query = "SELECT a.*, uP.FullName AS pName, uP.Phone AS pPhone, " +
                       "s.ServiceName, uD.FullName AS dName " +
                       "FROM Appointments a " +
                       "LEFT JOIN Users uP ON a.PatientID = uP.UserID " +
                       "LEFT JOIN Services s ON a.ServiceID = s.ServiceID " +
                       "LEFT JOIN Users uD ON a.DentistID = uD.UserID " +
                       "WHERE a.DentistID = ? " +
                       "ORDER BY a.AppointmentDate DESC, a.AppointmentTime DESC";
        try {
            conn = new DBContext().getConnection();
            ps = conn.prepareStatement(query);
            ps.setInt(1, dentistID);
            rs = ps.executeQuery();

            while (rs.next()) {
                Appointment app = new Appointment();
                app.setAppointmentID(rs.getInt("AppointmentID"));
                app.setAppointmentDate(rs.getDate("AppointmentDate"));
                app.setAppointmentTime(rs.getTime("AppointmentTime"));
                app.setStatus(rs.getString("Status"));
                app.setServiceName(rs.getString("ServiceName") != null ? rs.getString("ServiceName") : "Khám tổng quát");

                String dbName = rs.getString("pName");
                String notes = rs.getString("Notes");
                app.setNotes(notes); // Vẫn lưu đầy đủ Notes để làm Tooltip ở cột Trạng thái

                // --- LOGIC TÁCH CHUỖI THÔNG MINH ---
                if (dbName != null && !dbName.trim().isEmpty()) {
                    // Trường hợp bệnh nhân có tài khoản
                    app.setPatientName(dbName);
                    app.setPhoneNumber(rs.getString("pPhone"));
                } else if (notes != null && notes.contains("KHÁCH VÃNG LAI")) {
                    try {
                        // BƯỚC 1: Tách bỏ phần lý do hủy sau dấu | (nếu có)
                        // Ví dụ: "【KHÁCH VÃNG LAI: BT - SĐT: 0922...】 | [KHÁCH HỦY] Lý do: t bận"
                        // Sau khi split sẽ chỉ còn: "【KHÁCH VÃNG LAI: BT - SĐT: 0922...】"
                        String cleanInfo = notes.split("\\|")[0].trim();
                        
                        // BƯỚC 2: Tách lấy Tên và SĐT như cũ
                        String temp = cleanInfo.replace("【", "").replace("】", "");
                        String name = temp.split(":")[1].split("-")[0].trim();
                        String phone = temp.split("SĐT:")[1].trim();
                        
                        app.setPatientName(name);
                        app.setPhoneNumber(phone);
                    } catch (Exception e) {
                        app.setPatientName("Khách vãng lai");
                        app.setPhoneNumber("N/A");
                    }
                } else {
                    app.setPatientName("Chưa rõ tên");
                    app.setPhoneNumber("N/A");
                }
                list.add(app);
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            closeConnections();
        }
        return list;
    }
    
    public List<User> getPatientsByDentist(int dentistID) {
        List<User> list = new ArrayList<>();
        // SQL này sẽ lấy cả bệnh nhân có tài khoản và khách vãng lai từ bảng Appointments
        String query = 
            "SELECT DISTINCT u.UserID, u.FullName, u.Email, u.Phone, 'Member' as Type " +
            "FROM Users u JOIN Appointments a ON u.UserID = a.PatientID " +
            "WHERE a.DentistID = ? " +
            "UNION " +
            "SELECT DISTINCT 0 as UserID, " +
            "SUBSTRING_INDEX(SUBSTRING_INDEX(Notes, 'KHÁCH VÃNG LAI: ', -1), ' - SĐT:', 1) as FullName, " +
            "'Guest' as Email, " +
            "SUBSTRING_INDEX(SUBSTRING_INDEX(Notes, 'SĐT: ', -1), '】', 1) as Phone, " +
            "'Guest' as Type " +
            "FROM Appointments " +
            "WHERE DentistID = ? AND PatientID IS NULL AND Notes LIKE '%KHÁCH VÃNG LAI%'";

        try {
            conn = new DBContext().getConnection();
            ps = conn.prepareStatement(query);
            ps.setInt(1, dentistID);
            ps.setInt(2, dentistID);
            rs = ps.executeQuery();
            while (rs.next()) {
                User u = new User();
                u.setUserID(rs.getInt("UserID"));
                u.setFullName(rs.getString("FullName"));
                u.setEmail(rs.getString("Email"));
                u.setPhone(rs.getString("Phone"));
                list.add(u);
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            closeConnections();
        }
        return list;
    }
    
 // Trong AppointmentDAO.java
    public int countByStatusAndDentist(String status, int dentistID) {
        int count = 0;
        String query = "SELECT COUNT(*) FROM Appointments WHERE Status = ? AND DentistID = ?";
        try {
            conn = new DBContext().getConnection();
            ps = conn.prepareStatement(query);
            ps.setString(1, status);
            ps.setInt(2, dentistID);
            rs = ps.executeQuery();
            if (rs.next()) count = rs.getInt(1);
        } catch (Exception e) { e.printStackTrace(); }
        return count;
    }

    public double getRevenueByDentist(int dentistID) {
        double total = 0;
        // Chúng em dùng COALESCE để nếu không có dữ liệu sẽ trả về 0 thay vì lỗi
        String query = "SELECT SUM(COALESCE(s.Price, 0)) " +
                       "FROM Appointments a " +
                       "JOIN Services s ON a.ServiceID = s.ServiceID " +
                       "WHERE a.DentistID = ? AND a.Status = 'Completed'"; 
        try {
            conn = new DBContext().getConnection();
            ps = conn.prepareStatement(query);
            ps.setInt(1, dentistID);
            rs = ps.executeQuery();
            if (rs.next()) {
                total = rs.getDouble(1);
            }
            System.out.println("[DEBUG] Doanh thu BS ID " + dentistID + " là: " + total);
        } catch (Exception e) { 
            e.printStackTrace(); 
        } finally {
            closeConnections();
        }
        return total;
    }

    // Tổng số lịch hẹn có trong hệ thống
    public int getTotalAppointments() {
        String query = "SELECT COUNT(*) FROM Appointments";
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
    
    public boolean isDuplicateAppointment(int dentistID, String date, String time) {
        // Chỉ kiểm tra trùng với các lịch chưa bị Hủy (Cancelled)
        String query = "SELECT COUNT(*) FROM Appointments " +
                       "WHERE DentistID = ? AND AppointmentDate = ? AND AppointmentTime = ? " +
                       "AND Status IN ('Pending', 'Confirmed', 'Completed')";
        try {
            conn = new DBContext().getConnection();
            ps = conn.prepareStatement(query);
            ps.setInt(1, dentistID);
            ps.setString(2, date);
            ps.setString(3, time);
            rs = ps.executeQuery();
            if (rs.next()) {
                return rs.getInt(1) > 0; // Trả về true nếu đã có người đặt rồi
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            closeConnections();
        }
        return false;
    }

    // 2. Thống kê số lịch hẹn theo trạng thái (Để vẽ biểu đồ tròn)
    public int countByStatus(String status) {
        String sql = "SELECT COUNT(*) FROM Appointments WHERE Status = ?";
        try {
            conn = new DBContext().getConnection();
            ps = conn.prepareStatement(sql);
            ps.setString(1, status);
            rs = ps.executeQuery();
            if (rs.next()) return rs.getInt(1);
        } catch (Exception e) {}
        return 0;
    }
    
 // --- 10. HÀM HỦY LỊCH VÀ GHI LÝ DO (Cho khách hàng) ---
    public boolean cancelAppointmentWithReason(int id, String reason) {
        // Câu lệnh lấy Notes cũ để cộng dồn
        String getOldNotesSql = "SELECT Notes FROM Appointments WHERE AppointmentID = ?";
        String updateSql = "UPDATE Appointments SET Status = 'Cancelled', Notes = ? WHERE AppointmentID = ?";
        
        try {
            conn = new DBContext().getConnection();
            // 1. Lấy nội dung ghi chú cũ
            String oldNotes = "";
            ps = conn.prepareStatement(getOldNotesSql);
            ps.setInt(1, id);
            rs = ps.executeQuery();
            if (rs.next()) {
                oldNotes = rs.getString("Notes");
            }

            // 2. Tạo nội dung ghi chú mới (Notes cũ + Lý do hủy)
            String newNotes = (oldNotes != null ? oldNotes : "") + " | [KHÁCH HỦY] Lý do: " + reason;

            // 3. Thực thi cập nhật
            ps = conn.prepareStatement(updateSql);
            ps.setString(1, newNotes);
            ps.setInt(2, id);
            
            return ps.executeUpdate() > 0;
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            closeConnections();
        }
        return false;
    }
    public void updateStatusAndNotes(int id, String status, String reason) {
        // Nối thêm lý do vào Notes cũ để không mất dữ liệu
        String query = "UPDATE Appointments SET Status = ?, Notes = CONCAT(COALESCE(Notes, ''), ' | ADMIN HỦY: ', ?) WHERE AppointmentID = ?";
        try {
            conn = new DBContext().getConnection();
            ps = conn.prepareStatement(query);
            ps.setString(1, status);
            ps.setString(2, reason);
            ps.setInt(3, id);
            ps.executeUpdate();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            closeConnections();
        }
    }
    public boolean updateStatusWithNotes(int id, String status, String reason) {
        // Nối thêm lý do vào sau nội dung cũ trong cột Notes
        String query = "UPDATE Appointments SET Status = ?, "
                     + "Notes = CONCAT(COALESCE(Notes, ''), ' | [ADMIN HỦY]: ', ?) "
                     + "WHERE AppointmentID = ?";
        try {
            conn = new DBContext().getConnection();
            ps = conn.prepareStatement(query);
            ps.setString(1, status);
            ps.setString(2, reason);
            ps.setInt(3, id);
            return ps.executeUpdate() > 0;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        } finally {
            closeConnections();
        }
    }

    // --- ĐÓNG KẾT NỐI ---
    private void closeConnections() {
        try {
            if (rs != null) rs.close();
            if (ps != null) ps.close();
            if (conn != null) conn.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}