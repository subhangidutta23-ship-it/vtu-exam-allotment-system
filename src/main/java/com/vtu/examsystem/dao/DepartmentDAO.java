package com.vtu.examsystem.dao;

import com.vtu.examsystem.model.Department;
import com.vtu.examsystem.util.DBConnection;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class DepartmentDAO {

    public List<Department> getAll() throws SQLException {
        List<Department> list = new ArrayList<>();
        String sql = "SELECT dept_id, code, name, intake, diploma_seats, snq_seats FROM department";
        try (Connection con = DBConnection.get();
             Statement st = con.createStatement();
             ResultSet rs = st.executeQuery(sql)) {
            while (rs.next()) {
                list.add(new Department(
                        rs.getInt("dept_id"), rs.getString("code"), rs.getString("name"),
                        rs.getInt("intake"), rs.getInt("diploma_seats"), rs.getInt("snq_seats")));
            }
        }
        return list;
    }

    public int getIdByCode(String code) throws SQLException {
        String sql = "SELECT dept_id FROM department WHERE code = ?";
        try (Connection con = DBConnection.get();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, code);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next() ? rs.getInt("dept_id") : -1;
            }
        }
    }
}