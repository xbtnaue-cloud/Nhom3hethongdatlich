package com.nhakhoa.model;

public class User {

    private int    userID;
    private String username;
    private String password;
    private String fullName;
    private String email;
    private String phone;
    private int    roleID;
    private String specialty;
    private int    statusID;

    // 1. Constructor không đối số
    public User() {}

    // 2. Constructor 7 tham số (dùng khi bảng không có Specialty / StatusID)
    public User(int userID, String username, String password,
                String fullName, String email, String phone, int roleID) {
        this.userID   = userID;
        this.username = username;
        this.password = password;
        this.fullName = fullName;
        this.email    = email;
        this.phone    = phone;
        this.roleID   = roleID;
    }

    // 3. Constructor 9 tham số (đầy đủ, dùng khi cần specialty & statusID)
    public User(int userID, String username, String password,
                String fullName, String email, String phone,
                int roleID, String specialty, int statusID) {
        this(userID, username, password, fullName, email, phone, roleID); // gọi constructor 7 params
        this.specialty = specialty;
        this.statusID  = statusID;
    }

    // --- Getters & Setters ---

    public int getUserID()                  { return userID; }
    public void setUserID(int userID)       { this.userID = userID; }

    public String getUsername()             { return username; }
    public void setUsername(String u)       { this.username = u; }

    public String getPassword()             { return password; }
    public void setPassword(String p)       { this.password = p; }

    public String getFullName()             { return fullName; }
    public void setFullName(String n)       { this.fullName = n; }

    public String getEmail()                { return email; }
    public void setEmail(String e)          { this.email = e; }

    public String getPhone()                { return phone; }
    public void setPhone(String p)          { this.phone = p; }

    public int getRoleID()                  { return roleID; }
    public void setRoleID(int r)            { this.roleID = r; }

    public String getSpecialty()            { return specialty; }
    public void setSpecialty(String s)      { this.specialty = s; }

    public int getStatusID()                { return statusID; }
    public void setStatusID(int s)          { this.statusID = s; }

    @Override
    public String toString() {
        return "User{fullName=" + fullName
             + ", specialty=" + specialty
             + ", statusID="  + statusID + "}";
    }
}