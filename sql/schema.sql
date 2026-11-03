CREATE DATABASE IF NOT EXISTS vtu_exam;
USE vtu_exam;

CREATE TABLE department (
    dept_id INT AUTO_INCREMENT PRIMARY KEY,
    code VARCHAR(10) NOT NULL UNIQUE,
    name VARCHAR(100) NOT NULL,
    intake INT DEFAULT 0,
    diploma_seats INT DEFAULT 0,
    snq_seats INT DEFAULT 0
);

CREATE TABLE student (
    usn VARCHAR(15) PRIMARY KEY,
    name VARCHAR(100) NOT NULL,
    dept_id INT NOT NULL,
    semester INT NOT NULL,
    email VARCHAR(100),
    quota VARCHAR(10) DEFAULT 'REGULAR',
    FOREIGN KEY (dept_id) REFERENCES department(dept_id)
);

CREATE TABLE faculty (
    faculty_id INT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(100) NOT NULL,
    dept_id INT NOT NULL,
    designation VARCHAR(30) NOT NULL,
    email VARCHAR(100),
    FOREIGN KEY (dept_id) REFERENCES department(dept_id)
);

CREATE TABLE subject (
    subject_code VARCHAR(15) PRIMARY KEY,
    name VARCHAR(100) NOT NULL,
    dept_id INT NOT NULL,
    semester INT NOT NULL,
    FOREIGN KEY (dept_id) REFERENCES department(dept_id)
);

CREATE TABLE faculty_subject (
    faculty_id INT,
    subject_code VARCHAR(15),
    PRIMARY KEY (faculty_id, subject_code),
    FOREIGN KEY (faculty_id) REFERENCES faculty(faculty_id),
    FOREIGN KEY (subject_code) REFERENCES subject(subject_code)
);

CREATE TABLE room (
    room_id INT AUTO_INCREMENT PRIMARY KEY,
    room_name VARCHAR(30) NOT NULL UNIQUE,
    capacity INT NOT NULL,
    dept_id INT,
    available BOOLEAN DEFAULT TRUE,
    FOREIGN KEY (dept_id) REFERENCES department(dept_id)
);

CREATE TABLE exam (
    exam_id INT AUTO_INCREMENT PRIMARY KEY,
    subject_code VARCHAR(15) NOT NULL,
    exam_date DATE NOT NULL,
    session VARCHAR(10) NOT NULL,
    FOREIGN KEY (subject_code) REFERENCES subject(subject_code)
);

CREATE TABLE room_allotment (
    id INT AUTO_INCREMENT PRIMARY KEY,
    exam_id INT NOT NULL,
    usn VARCHAR(15) NOT NULL,
    room_id INT NOT NULL,
    seat_no INT NOT NULL,
    FOREIGN KEY (exam_id) REFERENCES exam(exam_id),
    FOREIGN KEY (usn) REFERENCES student(usn),
    FOREIGN KEY (room_id) REFERENCES room(room_id)
);

CREATE TABLE faculty_duty (
    id INT AUTO_INCREMENT PRIMARY KEY,
    exam_id INT NOT NULL,
    faculty_id INT NOT NULL,
    room_id INT,
    role VARCHAR(20) NOT NULL,
    FOREIGN KEY (exam_id) REFERENCES exam(exam_id),
    FOREIGN KEY (faculty_id) REFERENCES faculty(faculty_id),
    FOREIGN KEY (room_id) REFERENCES room(room_id)
);

INSERT INTO department (code, name, intake, diploma_seats, snq_seats) VALUES
('CSE',  'Computer Science',            120, 7, 3),
('ISE',  'Information Science',          60, 7, 3),
('AIML', 'CSE (AI & ML)',                60, 7, 3),
('EC',   'Electronics & Communication', 180, 7, 3),
('AIDS', 'AI & Data Science',            60, 7, 3);