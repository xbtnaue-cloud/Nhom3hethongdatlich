package com.nhakhoa.dao;

import com.nhakhoa.model.Contact;
import com.nhakhoa.model.ChatMessage;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

/**
 * DAO hoàn chỉnh xử lý liên hệ và lịch sử Chat
 * Project: Dental Pro
 */
public class ContactDAO {

    Connection conn = null;
    PreparedStatement ps = null;
    ResultSet rs = null;

    // ==========================================
    // 1. NHÓM HÀM THÊM MỚI (INSERT)
    // ==========================================

    // Hàm cũ: Thêm liên hệ từ form (không trả về ID)
    public void addContact(String name, String email, String message) {
        String queryContact = "INSERT INTO Contacts (FullName, Email, Message, Status) VALUES (?, ?, ?, 'Pending')";
        try {
            conn = new DBContext().getConnection();
            // 1. Lưu vào bảng Contacts để Admin thấy trong danh sách hộp thư
            ps = conn.prepareStatement(queryContact, Statement.RETURN_GENERATED_KEYS);
            ps.setString(1, name);
            ps.setString(2, email);
            ps.setString(3, message);
            ps.executeUpdate();

            // 2. Lấy ID vừa tạo để tạo "sợi dây" liên kết với Chatbot
            rs = ps.getGeneratedKeys();
            if (rs.next()) {
                int generatedId = rs.getInt(1);
                
                // 3. QUAN TRỌNG: Lưu tin nhắn này vào lịch sử Chat
                // Điều này giúp khách mở chatbot lên sẽ thấy ngay tin mình vừa gửi qua form
                insertChatMessage(generatedId, "Patient", message);
                
                // 4. (Tùy chọn) Lưu ID này vào Session để chatbot nhận diện được khách này
                // Lưu ý: Nếu hàm này chạy trong Controller thì dùng session.setAttribute
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            closeConnections();
        }
    }

    // Hàm mới: Thêm liên hệ và lấy ID để tạo Session Chat
    public int insertContactAndReturnID(Integer userId, String name, String email, String msg) {
        // Thêm UserID vào câu lệnh SQL
        String queryContact = "INSERT INTO Contacts (UserID, FullName, Email, Message, Status) VALUES (?, ?, ?, ?, 'Pending')";
        int generatedId = -1;
        try {
            conn = new DBContext().getConnection();
            ps = conn.prepareStatement(queryContact, Statement.RETURN_GENERATED_KEYS);
            
            // Gán UserID (Có thể là null nếu dùng setObject hoặc ktr null)
            if (userId != null) {
                ps.setInt(1, userId);
            } else {
                ps.setNull(1, java.sql.Types.INTEGER);
            }
            
            ps.setString(2, name);
            ps.setString(3, email);
            ps.setString(4, msg);
            ps.executeUpdate();
            
            rs = ps.getGeneratedKeys();
            if (rs.next()) {
                generatedId = rs.getInt(1);
                insertChatMessage(generatedId, "Patient", msg);
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            closeConnections();
        }
        return generatedId;
    }

    // Hàm chèn tin nhắn vào bảng lịch sử (Dùng cho cả Khách và Admin)
    public void insertChatMessage(int contactID, String role, String content) {
        String query = "INSERT INTO ChatMessages (ContactID, SenderRole, Content) VALUES (?, ?, ?)";
        try {
            conn = new DBContext().getConnection();
            ps = conn.prepareStatement(query);
            ps.setInt(1, contactID);
            ps.setString(2, role); // 'Patient' hoặc 'Doctor'
            ps.setString(3, content);
            ps.executeUpdate();
            
            if("Patient".equals(role)) {
                updateStatus(contactID, "Pending");
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            closeConnections();
        }
    }

    // ==========================================
    // 2. NHÓM HÀM TRUY VẤN (SELECT)
    // ==========================================

    // Lấy tất cả liên hệ cho Admin
    public List<Contact> getAllContacts() {
        List<Contact> list = new ArrayList<>();
        // Sử dụng LEFT JOIN để lấy cả người có tài khoản và khách vãng lai
        String query = "SELECT c.*, u.Username " +
                       "FROM Contacts c " +
                       "LEFT JOIN Users u ON c.UserID = u.UserID " +
                       "ORDER BY c.CreatedAt DESC";
        try {
            conn = new DBContext().getConnection();
            ps = conn.prepareStatement(query);
            rs = ps.executeQuery();
            while (rs.next()) {
                Contact c = new Contact();
                c.setContactID(rs.getInt("ContactID"));
                c.setUserID(rs.getInt("UserID")); // Lưu ID tài khoản
                c.setUsername(rs.getString("Username")); // Lưu Username từ bảng Users
                c.setFullName(rs.getString("FullName"));
                c.setEmail(rs.getString("Email"));
                c.setMessage(rs.getString("Message"));
                c.setStatus(rs.getString("Status"));
                c.setCreatedAt(rs.getTimestamp("CreatedAt"));
                list.add(c);
            }
        } catch (Exception e) { e.printStackTrace(); }
        finally { closeConnections(); }
        return list;
    }

    // Lấy chi tiết 1 liên hệ theo ID
    public Contact getContactByID(int id) {
        String query = "SELECT * FROM Contacts WHERE ContactID = ?";
        try {
            conn = new DBContext().getConnection();
            ps = conn.prepareStatement(query);
            ps.setInt(1, id);
            rs = ps.executeQuery();
            if (rs.next()) {
                Contact c = new Contact();
                c.setContactID(rs.getInt("ContactID"));
                c.setFullName(rs.getString("FullName"));
                c.setEmail(rs.getString("Email"));
                c.setMessage(rs.getString("Message"));
                c.setReplyMessage(rs.getString("ReplyMessage"));
                c.setCreatedAt(rs.getTimestamp("CreatedAt"));
                return c;
            }
        } catch (Exception e) { 
            e.printStackTrace(); 
        } finally {
            closeConnections();
        }
        return null;
    }

    // Lấy toàn bộ lịch sử chat của 1 người
    public List<ChatMessage> getListMessagesByContactID(int contactID) {
        List<ChatMessage> list = new ArrayList<>();
        String query = "SELECT * FROM ChatMessages WHERE ContactID = ? ORDER BY CreatedAt ASC";
        try {
            conn = new DBContext().getConnection();
            ps = conn.prepareStatement(query);
            ps.setInt(1, contactID);
            rs = ps.executeQuery();
            while (rs.next()) {
                ChatMessage m = new ChatMessage();
                m.setMessageID(rs.getInt("MessageID"));
                m.setContactID(rs.getInt("ContactID"));
                m.setSenderRole(rs.getString("SenderRole"));
                m.setContent(rs.getString("Content"));
                m.setCreatedAt(rs.getTimestamp("CreatedAt"));
                list.add(m);
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            closeConnections();
        }
        return list;
    }

    // Hàm cũ: Lấy phản hồi mới nhất (hỗ trợ logic cũ nếu cần)
    public Contact getLatestReplyForCustomer(String name) {
        String query = "SELECT * FROM Contacts WHERE FullName = ? AND Status = 'Replied' ORDER BY CreatedAt DESC LIMIT 1";
        try {
            conn = new DBContext().getConnection();
            ps = conn.prepareStatement(query);
            ps.setString(1, name);
            rs = ps.executeQuery();
            if (rs.next()) {
                Contact c = new Contact();
                c.setContactID(rs.getInt("ContactID"));
                c.setReplyMessage(rs.getString("ReplyMessage"));
                return c;
            }
        } catch (Exception e) { e.printStackTrace(); }
        return null;
    }

    public int countNewMessages(int contactID) {
        // Đếm các tin nhắn của Doctor trong cuộc hội thoại này 
        // (Bạn có thể thêm cột IsRead vào DB nếu muốn chính xác tuyệt đối, 
        // còn đây là cách đơn giản dựa trên session)
        String query = "SELECT COUNT(*) FROM ChatMessages WHERE ContactID = ? AND SenderRole = 'Doctor'";
        try {
            conn = new DBContext().getConnection();
            ps = conn.prepareStatement(query);
            ps.setInt(1, contactID);
            rs = ps.executeQuery();
            if (rs.next()) return rs.getInt(1);
        } catch (Exception e) { e.printStackTrace(); }
        finally { closeConnections(); }
        return 0;
    }
    
    // ==========================================
    // 3. NHÓM HÀM CẬP NHẬT & XÓA (UPDATE/DELETE)
    // ==========================================

    // Admin trả lời (Dùng Transaction để đảm bảo an toàn)
    public void replyContact(int id, String reply) {
        String queryInsert = "INSERT INTO ChatMessages (ContactID, SenderRole, Content) VALUES (?, 'Doctor', ?)";
        String queryUpdateStatus = "UPDATE Contacts SET Status = 'Replied' WHERE ContactID = ?";
        try {
            conn = new DBContext().getConnection();
            conn.setAutoCommit(false); 

            ps = conn.prepareStatement(queryInsert);
            ps.setInt(1, id);
            ps.setString(2, reply);
            ps.executeUpdate();

            ps = conn.prepareStatement(queryUpdateStatus);
            ps.setInt(1, id);
            ps.executeUpdate();

            conn.commit(); 
        } catch (Exception e) {
            if (conn != null) try { conn.rollback(); } catch (Exception ex) {}
            e.printStackTrace();
        } finally {
            closeConnections();
        }
    }

    public void updateStatus(int id, String status) {
        String query = "UPDATE Contacts SET Status = ? WHERE ContactID = ?";
        try {
            conn = new DBContext().getConnection();
            ps = conn.prepareStatement(query);
            ps.setString(1, status);
            ps.setInt(2, id);
            ps.executeUpdate();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            closeConnections();
        }
    }

    public void deleteContact(int id) {
        String query = "DELETE FROM Contacts WHERE ContactID = ?";
        try {
            conn = new DBContext().getConnection();
            ps = conn.prepareStatement(query);
            ps.setInt(1, id);
            ps.executeUpdate();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            closeConnections();
        }
    }

    // Đóng kết nối
    private void closeConnections() {
        try {
            if (rs != null) rs.close();
            if (ps != null) ps.close();
            if (conn != null) conn.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}