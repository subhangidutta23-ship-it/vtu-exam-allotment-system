package com.vtu.examsystem.dao;

import com.vtu.examsystem.model.Faculty;
import com.vtu.examsystem.util.DBConnection;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class FacultyDAO {

    public void add(Faculty f) throws SQLException {
        String sql = "INSERT INTO faculty (name, dept_id, designation, email) VALUES (?,?,?,?)";
        try (Connection con = DBConnection.get();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, f.getName());
            ps.setInt(2, f.getDeptId());
            ps.setString(3, f.getDesignation());
            ps.setString(4, f.getEmail());
            ps.executeUpdate();
        }
    }

    public List<Faculty> getAll() throws SQLException {
        return query("SELECT * FROM faculty ORDER BY dept_id, name", null);
    }

    public List<Faculty> getByDesignation(String designation) throws SQLException {
        return query("SELECT * FROM faculty WHERE designation = ? ORDER BY name", designation);
    }

    private List<Faculty> query(String sql, String param) throws SQLException {
        List<Faculty> list = new ArrayList<>();
        try (Connection con = DBConnection.get();
             PreparedStatement ps = con.prepareStatement(sql)) {
            if (param != null) ps.setString(1, param);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    list.add(new Faculty(rs.getInt("faculty_id"), rs.getString("name"),
                            rs.getInt("dept_id"), rs.getString("designation"), rs.getString("email")));
                }
            }
        }
        return list;
    }
}