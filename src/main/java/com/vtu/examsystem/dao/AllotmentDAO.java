package com.vtu.examsystem.dao;

import com.vtu.examsystem.model.ExamCandidate;
import com.vtu.examsystem.model.RoomAllotment;
import com.vtu.examsystem.model.Student;
import com.vtu.examsystem.util.DBConnection;

import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class AllotmentDAO {

    /** Students who have an exam on this date and session. */
    public List<ExamCandidate> findCandidates(LocalDate date, String session) throws SQLException {
        String sql = "SELECT s.usn, s.name, s.dept_id, s.semester, s.email, s.quota, e.exam_id "
                   + "FROM student s "
                   + "JOIN subject sub ON sub.dept_id = s.dept_id AND sub.semester = s.semester "
                   + "JOIN exam e ON e.subject_code = sub.subject_code "
                   + "WHERE e.exam_date = ? AND e.session = ? "
                   + "ORDER BY s.dept_id, s.usn";
        List<ExamCandidate> list = new ArrayList<>();
        try (Connection con = DBConnection.get();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setDate(1, Date.valueOf(date));
            ps.setString(2, session);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    Student s = new Student(rs.getString("usn"), rs.getString("name"),
                            rs.getInt("dept_id"), rs.getInt("semester"),
                            rs.getString("email"), rs.getString("quota"));
                    list.add(new ExamCandidate(s, rs.getInt("exam_id")));
                }
            }
        }
        return list;
    }

    /** Remove an old allotment so the slot can be allotted again. */
    public void deleteForSlot(LocalDate date, String session) throws SQLException {
        String sql = "DELETE ra FROM room_allotment ra "
                   + "JOIN exam e ON ra.exam_id = e.exam_id "
                   + "WHERE e.exam_date = ? AND e.session = ?";
        try (Connection con = DBConnection.get();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setDate(1, Date.valueOf(date));
            ps.setString(2, session);
            ps.executeUpdate();
        }
    }

    public void saveAll(List<RoomAllotment> list) throws SQLException {
        String sql = "INSERT INTO room_allotment (exam_id, usn, room_id, seat_no) VALUES (?,?,?,?)";
        try (Connection con = DBConnection.get();
             PreparedStatement ps = con.prepareStatement(sql)) {
            con.setAutoCommit(false);
            for (RoomAllotment a : list) {
                ps.setInt(1, a.getExamId());
                ps.setString(2, a.getUsn());
                ps.setInt(3, a.getRoomId());
                ps.setInt(4, a.getSeatNo());
                ps.addBatch();
            }
            ps.executeBatch();
            con.commit();
        }
    }

    /** Room-wise list, one text line per student. */
    public List<String> roomWiseReport(LocalDate date, String session) throws SQLException {
        String sql = "SELECT r.room_name, ra.seat_no, s.usn, s.name, d.code "
                   + "FROM room_allotment ra "
                   + "JOIN exam e ON ra.exam_id = e.exam_id "
                   + "JOIN room r ON ra.room_id = r.room_id "
                   + "JOIN student s ON ra.usn = s.usn "
                   + "JOIN department d ON s.dept_id = d.dept_id "
                   + "WHERE e.exam_date = ? AND e.session = ? "
                   + "ORDER BY r.room_name, ra.seat_no";
        List<String> lines = new ArrayList<>();
        try (Connection con = DBConnection.get();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setDate(1, Date.valueOf(date));
            ps.setString(2, session);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    lines.add(rs.getString("room_name") + " | Seat " + rs.getInt("seat_no")
                            + " | " + rs.getString("usn") + " | " + rs.getString("name")
                            + " | " + rs.getString("code"));
                }
            }
        }
        return lines;
    }
}