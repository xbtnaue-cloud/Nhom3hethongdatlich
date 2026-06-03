CREATE DATABASE Hethongnhakhoa CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
USE Hethongnhakhoa;

-- 1. Bảng Vai trò (Phân quyền rõ ràng)
CREATE TABLE Roles (
    RoleID INT PRIMARY KEY,
    RoleName VARCHAR(20) -- Admin, Dentist, Patient
);

-- 2. Bảng Người dùng (Dùng chung cho đăng nhập)
CREATE TABLE Users (
    UserID INT PRIMARY KEY AUTO_INCREMENT,
    Username VARCHAR(50) UNIQUE NOT NULL,
    Password VARCHAR(255) NOT NULL,
    FullName NVARCHAR(100),
    Email VARCHAR(100),
    Phone VARCHAR(15),
    RoleID INT,
    FOREIGN KEY (RoleID) REFERENCES Roles(RoleID)
);
ALTER TABLE Users ADD Specialty NVARCHAR(200) NULL;
ALTER TABLE Users ADD StatusID  INT NOT NULL DEFAULT 1;
UPDATE Users SET Specialty = NULL WHERE RoleID = 2;
-- 3. Bảng Dịch vụ (Lưu danh mục điều trị)
CREATE TABLE Services (
    ServiceID INT PRIMARY KEY AUTO_INCREMENT,
    ServiceName NVARCHAR(100) NOT NULL,
    Description NVARCHAR(255),
    Price DECIMAL(18, 2) NOT NULL,
    Duration INT -- Thời gian dự kiến thực hiện (phút)
);
ALTER TABLE Services ADD COLUMN ServiceImage VARCHAR(255) NULL;

-- 4. Bảng Lịch hẹn (Trung tâm của hệ thống)
CREATE TABLE Appointments (
    AppointmentID INT PRIMARY KEY AUTO_INCREMENT,
    PatientID INT,  -- Liên kết tới Users có Role là Patient
    DentistID INT,  -- Liên kết tới Users có Role là Dentist
    AppointmentDate DATE NOT NULL,
    AppointmentTime TIME NOT NULL,
    Status NVARCHAR(20) DEFAULT 'Pending', -- Pending, Confirmed, Cancelled, Completed
	Notes TEXT,
    CreatedAt TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (PatientID) REFERENCES Users(UserID),
    FOREIGN KEY (DentistID) REFERENCES Users(UserID)
);
ALTER TABLE Appointments MODIFY PatientID INT NULL;
ALTER TABLE Appointments ADD COLUMN ServiceID INT;
ALTER TABLE Appointments ADD FOREIGN KEY (ServiceID) REFERENCES Services(ServiceID);



-- 5. Bảng Chi tiết lịch hẹn (Nếu 1 lần đi khám làm nhiều dịch vụ)
CREATE TABLE AppointmentDetails (
    AppointmentID INT,
    ServiceID INT,
    PRIMARY KEY (AppointmentID, ServiceID),
    FOREIGN KEY (AppointmentID) REFERENCES Appointments(AppointmentID),
    FOREIGN KEY (ServiceID) REFERENCES Services(ServiceID)
);

-- Bảng liên kết Bác sĩ và Dịch vụ
CREATE TABLE DentistServices (
    DentistID INT,
    ServiceID INT,
    PRIMARY KEY (DentistID, ServiceID),
    FOREIGN KEY (DentistID) REFERENCES Users(UserID),
    FOREIGN KEY (ServiceID) REFERENCES Services(ServiceID)
);


CREATE TABLE Contacts (
    ContactID INT PRIMARY KEY AUTO_INCREMENT,
    FullName NVARCHAR(100),
    Email VARCHAR(100),
    Message TEXT,
    Status NVARCHAR(20) DEFAULT 'Pending', -- Để Admin biết cái nào đã đọc
    CreatedAt TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);
ALTER TABLE Contacts ADD ReplyMessage TEXT;
ALTER TABLE Contacts ADD Status VARCHAR(50) DEFAULT 'Pending';
-- Thêm cột UserID vào bảng Contacts
ALTER TABLE Contacts ADD COLUMN UserID INT NULL;

-- Tạo khóa ngoại liên kết sang bảng Users
ALTER TABLE Contacts ADD FOREIGN KEY (UserID) REFERENCES Users(UserID);

CREATE TABLE ChatMessages (
    MessageID INT PRIMARY KEY AUTO_INCREMENT,
    ContactID INT,          -- Liên kết với cuộc hội thoại trong bảng Contacts
    SenderRole VARCHAR(20), -- 'Patient' hoặc 'Doctor'
    Content TEXT,           -- Nội dung tin nhắn
    CreatedAt TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (ContactID) REFERENCES Contacts(ContactID)
);

CREATE TABLE Specialties (
    SpecialtyID INT PRIMARY KEY AUTO_INCREMENT,
    SpecialtyName NVARCHAR(100) NOT NULL
);
INSERT INTO Specialties (SpecialtyName) VALUES
('Nha khoa tổng quát'),
('Chỉnh nha (Niềng răng)'),
('Phẫu thuật hàm mặt'),
('Nha chu (Răng - Lợi)'),
('Implant (Trồng răng)'),
('Nha khoa thẩm mỹ'),
('Nha khoa trẻ em'),
('Nội nha (Điều trị tủy)');

