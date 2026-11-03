package com.vtu.examsystem.model;

public class Subject {
    private String subjectCode;
    private String name;
    private int deptId;
    private int semester;

    public Subject(String subjectCode, String name, int deptId, int semester) {
        this.subjectCode = subjectCode; this.name = name;
        this.deptId = deptId; this.semester = semester;
    }
    public String getSubjectCode() { return subjectCode; }
    public String getName() { return name; }
    public int getDeptId() { return deptId; }
    public int getSemester() { return semester; }
}