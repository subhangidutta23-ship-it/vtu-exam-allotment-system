package com.vtu.examsystem.model;

public class Student {
    private String usn;
    private String name;
    private int deptId;
    private int semester;
    private String email;
    private String quota;   // REGULAR / DIPLOMA / SNQ

    public Student(String usn, String name, int deptId, int semester, String email, String quota) {
        this.usn = usn;
        this.name = name;
        this.deptId = deptId;
        this.semester = semester;
        this.email = email;
        this.quota = quota;
    }

    public String getUsn() { return usn; }
    public String getName() { return name; }
    public int getDeptId() { return deptId; }
    public int getSemester() { return semester; }
    public String getEmail() { return email; }
    public String getQuota() { return quota; }
}