package com.vtu.examsystem.model;

public class Faculty {
    private int facultyId;
    private String name;
    private int deptId;
    private String designation;   // ASSISTANT_PROF / ASSOCIATE_PROF / PROFESSOR
    private String email;

    public Faculty(int facultyId, String name, int deptId, String designation, String email) {
        this.facultyId = facultyId; this.name = name; this.deptId = deptId;
        this.designation = designation; this.email = email;
    }
    public int getFacultyId() { return facultyId; }
    public String getName() { return name; }
    public int getDeptId() { return deptId; }
    public String getDesignation() { return designation; }
    public String getEmail() { return email; }
}