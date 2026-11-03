package com.vtu.examsystem.dao;

import com.vtu.examsystem.model.Room;
import com.vtu.examsystem.util.DBConnection;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class RoomDAO {

    public void add(Room r) throws SQLException {
        String sql = "INSERT INTO room (room_name, capacity, dept_id, available) VALUES (?,?,?,?)";
        try (Connection con = DBConnection.get();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, r.getRoomName());
            ps.setInt(2, r.getCapacity());
            ps.setInt(3, r.getDeptId());
            ps.setBoolean(4, r.isAvailable());
            ps.executeUpdate();
        }
    }

    public List<Room> getAll() throws SQLException {
        return query("SELECT * FROM room ORDER BY room_name");
    }

    public List<Room> getAvailable() throws SQLException {
        return query("SELECT * FROM room WHERE available = TRUE ORDER BY room_name");
    }

    private List<Room> query(String sql) throws SQLException {
        List<Room> list = new ArrayList<>();
        try (Connection con = DBConnection.get();
             Statement st = con.createStatement();
             ResultSet rs = st.executeQuery(sql)) {
            while (rs.next()) {
                list.add(new Room(rs.getInt("room_id"), rs.getString("room_name"),
                        rs.getInt("capacity"), rs.getInt("dept_id"), rs.getBoolean("available")));
            }
        }
        return list;
    }
}