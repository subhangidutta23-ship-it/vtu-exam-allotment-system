package com.vtu.examsystem.dao;

import com.vtu.examsystem.model.Exam;
import com.vtu.examsystem.util.DBConnection;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class ExamDAO {

    public void add(Exam e) throws SQLException {
        String sql = "INSERT INTO exam (subject_code, exam_date, session) VALUES (?,?,?)";
        try (Connection con = DBConnection.get();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, e.getSubjectCode());
            ps.setDate(2, Date.valueOf(e.getExamDate()));
            ps.setString(3, e.getSession());
            ps.executeUpdate();
        }
    }

    public List<Exam> getAll() throws SQLException {
        List<Exam> list = new ArrayList<>();
        String sql = "SELECT * FROM exam ORDER BY exam_date, session";
        try (Connection con = DBConnection.get();
             Statement st = con.createStatement();
             ResultSet rs = st.executeQuery(sql)) {
            while (rs.next()) {
                list.add(new Exam(rs.getInt("exam_id"), rs.getString("subject_code"),
                        rs.getDate("exam_date").toLocalDate(), rs.getString("session")));
            }
        }
        return list;
    }
}