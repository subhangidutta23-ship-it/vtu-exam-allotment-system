package com.vtu.examsystem.dao;

import com.vtu.examsystem.model.Subject;
import com.vtu.examsystem.util.DBConnection;

import java.sql.*;

public class SubjectDAO {

    public boolean exists(String code) throws SQLException {
        String sql = "SELECT 1 FROM subject WHERE subject_code = ?";
        try (Connection con = DBConnection.get();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, code);
            try (ResultSet rs = ps.executeQuery()) { return rs.next(); }
        }
    }

    public void add(Subject s) throws SQLException {
        String sql = "INSERT INTO subject (subject_code, name, dept_id, semester) VALUES (?,?,?,?)";
        try (Connection con = DBConnection.get();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, s.getSubjectCode());
            ps.setString(2, s.getName());
            ps.setInt(3, s.getDeptId());
            ps.setInt(4, s.getSemester());
            ps.executeUpdate();
        }
    }
}