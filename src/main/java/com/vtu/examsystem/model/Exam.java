package com.vtu.examsystem.model;

import java.time.LocalDate;

public class Exam {
    private int examId;
    private String subjectCode;
    private LocalDate examDate;
    private String session;

    public Exam(int examId, String subjectCode, LocalDate examDate, String session) {
        this.examId = examId; this.subjectCode = subjectCode;
        this.examDate = examDate; this.session = session;
    }
    public int getExamId() { return examId; }
    public String getSubjectCode() { return subjectCode; }
    public LocalDate getExamDate() { return examDate; }
    public String getSession() { return session; }
}