package com.vtu.examsystem.model;

public class FacultyDuty {
    private int examId;
    private int facultyId;
    private Integer roomId;      // null for DCS and relieving supervisors
    private String role;         // INVIGILATOR / RELIEVING / DCS

    public FacultyDuty(int examId, int facultyId, Integer roomId, String role) {
        this.examId = examId; this.facultyId = facultyId;
        this.roomId = roomId; this.role = role;
    }
    public int getExamId() { return examId; }
    public int getFacultyId() { return facultyId; }
    public Integer getRoomId() { return roomId; }
    public String getRole() { return role; }
}