package com.vtu.examsystem.dao;

import com.vtu.examsystem.model.Student;
import com.vtu.examsystem.util.DBConnection;

import java.sql.*;
import java.util.*;

public class StudentDAO {

    public boolean exists(String usn) throws SQLException {
        String sql = "SELECT 1 FROM student WHERE usn = ?";
        try (Connection con = DBConnection.get();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, usn);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next();
            }
        }
    }

    public void add(Student s) throws SQLException {
        String sql = "INSERT INTO student (usn, name, dept_id, semester, email, quota) VALUES (?,?,?,?,?,?)";
        try (Connection con = DBConnection.get();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, s.getUsn());
            ps.setString(2, s.getName());
            ps.setInt(3, s.getDeptId());
            ps.setInt(4, s.getSemester());
            ps.setString(5, s.getEmail());
            ps.setString(6, s.getQuota());
            ps.executeUpdate();
        }
    }

    public List<Student> getAll() throws SQLException {
        List<Student> list = new ArrayList<>();
        String sql = "SELECT usn, name, dept_id, semester, email, quota FROM student ORDER BY dept_id, usn";
        try (Connection con = DBConnection.get();
             Statement st = con.createStatement();
             ResultSet rs = st.executeQuery(sql)) {
            while (rs.next()) {
                list.add(new Student(
                        rs.getString("usn"), rs.getString("name"), rs.getInt("dept_id"),
                        rs.getInt("semester"), rs.getString("email"), rs.getString("quota")));
            }
        }
        return list;
    }

    /** Department code -> number of students (for the dashboard). */
    public Map<String, Integer> countByDept() throws SQLException {
        Map<String, Integer> map = new LinkedHashMap<>();
        String sql = "SELECT d.code, COUNT(s.usn) AS total "
                   + "FROM department d LEFT JOIN student s ON s.dept_id = d.dept_id "
                   + "GROUP BY d.code";
        try (Connection con = DBConnection.get();
             Statement st = con.createStatement();
             ResultSet rs = st.executeQuery(sql)) {
            while (rs.next()) {
                map.put(rs.getString("code"), rs.getInt("total"));
            }
        }
        return map;
    }
}