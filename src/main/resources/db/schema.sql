-- =========================================================
-- Student Registration System - Database Script
-- Database: student_registration_db
-- =========================================================

DROP DATABASE IF EXISTS student_registration_db;
CREATE DATABASE student_registration_db CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
USE student_registration_db;

-- =========================================================
-- Table: students
-- =========================================================
CREATE TABLE students (
    id              BIGINT AUTO_INCREMENT PRIMARY KEY,
    student_id      VARCHAR(20)  NOT NULL UNIQUE,
    first_name      VARCHAR(50)  NOT NULL,
    last_name       VARCHAR(50)  NOT NULL,
    gender          VARCHAR(10)  NOT NULL,
    date_of_birth   DATE         NOT NULL,
    mobile_number   VARCHAR(15)  NOT NULL,
    email           VARCHAR(100) NOT NULL UNIQUE,
    address         VARCHAR(255),
    city            VARCHAR(50),
    state           VARCHAR(50),
    pin_code        VARCHAR(10),
    course          VARCHAR(100) NOT NULL,
    branch          VARCHAR(100) NOT NULL,
    semester        INT          NOT NULL,
    admission_date  DATE         NOT NULL,
    photo_path      VARCHAR(255),
    status          VARCHAR(10)  NOT NULL DEFAULT 'ACTIVE',
    created_at      DATETIME,
    updated_at      DATETIME
) ENGINE=InnoDB;

-- =========================================================
-- Table: users  (login accounts - ADMIN or STUDENT)
-- =========================================================
CREATE TABLE users (
    id                  BIGINT AUTO_INCREMENT PRIMARY KEY,
    username            VARCHAR(50)  NOT NULL UNIQUE,
    password            VARCHAR(255) NOT NULL,
    email               VARCHAR(100) NOT NULL,
    role                VARCHAR(10)  NOT NULL,
    enabled             BOOLEAN      NOT NULL DEFAULT TRUE,
    student_id          BIGINT,
    reset_token         VARCHAR(100),
    reset_token_expiry  DATETIME,
    CONSTRAINT fk_users_student FOREIGN KEY (student_id) REFERENCES students(id)
) ENGINE=InnoDB;

-- =========================================================
-- Sample Students
-- =========================================================
INSERT INTO students
(student_id, first_name, last_name, gender, date_of_birth, mobile_number, email, address, city, state, pin_code,
 course, branch, semester, admission_date, photo_path, status, created_at, updated_at)
VALUES
('STU2026001', 'John', 'Smith', 'MALE', '2004-05-12', '9876543210', 'john.smith@example.com',
 '12 MG Road', 'Pune', 'Maharashtra', '411001', 'B.Tech', 'Computer Science', 3, '2024-07-15', NULL, 'ACTIVE', NOW(), NOW()),

('STU2026002', 'Emily', 'Davis', 'FEMALE', '2003-11-02', '9876543211', 'emily.davis@example.com',
 '45 Park Street', 'Mumbai', 'Maharashtra', '400001', 'B.Tech', 'Electronics', 5, '2023-07-10', NULL, 'ACTIVE', NOW(), NOW()),

('STU2026003', 'Michael', 'Brown', 'MALE', '2004-01-20', '9876543212', 'michael.brown@example.com',
 '78 Lake View', 'Bengaluru', 'Karnataka', '560001', 'B.Sc', 'Physics', 2, '2025-07-01', NULL, 'ACTIVE', NOW(), NOW()),

('STU2026004', 'Priya', 'Sharma', 'FEMALE', '2004-03-18', '9876543213', 'priya.sharma@example.com',
 '9 Green Avenue', 'Delhi', 'Delhi', '110001', 'B.Tech', 'Computer Science', 3, '2024-07-15', NULL, 'ACTIVE', NOW(), NOW()),

('STU2026005', 'Rahul', 'Verma', 'MALE', '2003-09-09', '9876543214', 'rahul.verma@example.com',
 '22 Nehru Nagar', 'Hyderabad', 'Telangana', '500001', 'MCA', 'Software Engineering', 1, '2026-01-05', NULL, 'INACTIVE', NOW(), NOW()),

('STU2026006', 'Sneha', 'Iyer', 'FEMALE', '2004-06-25', '9876543215', 'sneha.iyer@example.com',
 '5 Anna Salai', 'Chennai', 'Tamil Nadu', '600001', 'B.Tech', 'Mechanical', 7, '2022-07-20', NULL, 'ACTIVE', NOW(), NOW()),

('STU2026007', 'Arjun', 'Nair', 'MALE', '2004-08-14', '9876543216', 'arjun.nair@example.com',
 '31 MG Road', 'Kochi', 'Kerala', '682001', 'B.Com', 'Finance', 4, '2023-07-18', NULL, 'ACTIVE', NOW(), NOW()),

('STU2026008', 'Ananya', 'Gupta', 'FEMALE', '2005-02-11', '9876543217', 'ananya.gupta@example.com',
 '14 Civil Lines', 'Jaipur', 'Rajasthan', '302001', 'B.Tech', 'Electronics', 1, '2026-01-05', NULL, 'ACTIVE', NOW(), NOW());

-- =========================================================
-- Sample Users (Login Accounts)
-- Password for all demo accounts (BCrypt-encoded below):
--   admin    -> Admin@123
--   jsmith   -> Student@123
-- =========================================================

-- BCrypt hash of "Admin@123"
INSERT INTO users (username, password, email, role, enabled, student_id)
VALUES ('admin', '$2b$10$gzBK9UyIpOxxxpnJz4YbzustyeD9FIkvqNMpHypNwkFBTQ.eeRKWi', 'admin@srs.local', 'ADMIN', TRUE, NULL);

-- BCrypt hash of "Student@123" - linked to John Smith (STU2026001)
INSERT INTO users (username, password, email, role, enabled, student_id)
VALUES ('jsmith', '$2b$10$WNBrXwAyTAThDqnI8einceg0I4/GAUE6RzG9gvRtDb8nOVvAXm0Yi', 'john.smith@example.com', 'STUDENT', TRUE,
        (SELECT id FROM students WHERE student_id = 'STU2026001'));

-- =========================================================
-- Helpful indexes for search/filter/sort performance
-- =========================================================
CREATE INDEX idx_students_course ON students(course);
CREATE INDEX idx_students_branch ON students(branch);
CREATE INDEX idx_students_semester ON students(semester);
CREATE INDEX idx_students_status ON students(status);
CREATE INDEX idx_students_created_at ON students(created_at);
