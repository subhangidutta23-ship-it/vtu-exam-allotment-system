package com.vtu.examsystem.dao;

import com.vtu.examsystem.model.FacultyDuty;
import com.vtu.examsystem.model.Room;
import com.vtu.examsystem.util.DBConnection;

import java.sql.*;
import java.sql.Date;
import java.time.LocalDate;
import java.util.*;

public class DutyDAO {

    /** Rooms that have students allotted in this slot. */
    public List<Room> getRoomsInUse(LocalDate date, String session) throws SQLException {
        String sql = "SELECT DISTINCT r.room_id, r.room_name, r.capacity, r.dept_id, r.available "
                   + "FROM room_allotment ra "
                   + "JOIN exam e ON ra.exam_id = e.exam_id "
                   + "JOIN room r ON ra.room_id = r.room_id "
                   + "WHERE e.exam_date = ? AND e.session = ? ORDER BY r.room_name";
        List<Room> list = new ArrayList<>();
        try (Connection con = DBConnection.get();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setDate(1, Date.valueOf(date));
            ps.setString(2, session);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    list.add(new Room(rs.getInt("room_id"), rs.getString("room_name"),
                            rs.getInt("capacity"), rs.getInt("dept_id"), rs.getBoolean("available")));
                }
            }
        }
        return list;
    }

    /** First exam id of the slot. Duties are stored against it. */
    public int getSlotExamId(LocalDate date, String session) throws SQLException {
        String sql = "SELECT MIN(exam_id) FROM exam WHERE exam_date = ? AND session = ?";
        try (Connection con = DBConnection.get();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setDate(1, Date.valueOf(date));
            ps.setString(2, session);
            try (ResultSet rs = ps.executeQuery()) {
                rs.next();
                int id = rs.getInt(1);
                return rs.wasNull() ? -1 : id;
            }
        }
    }

    /** Faculty who teach a subject examined in this slot (they must not invigilate). */
    public Set<Integer> getConflictedFaculty(LocalDate date, String session) throws SQLException {
        String sql = "SELECT DISTINCT fs.faculty_id FROM faculty_subject fs "
                   + "JOIN exam e ON e.subject_code = fs.subject_code "
                   + "WHERE e.exam_date = ? AND e.session = ?";
        Set<Integer> set = new HashSet<>();
        try (Connection con = DBConnection.get();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setDate(1, Date.valueOf(date));
            ps.setString(2, session);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) set.add(rs.getInt(1));
            }
        }
        return set;
    }

    /** Duties each faculty already has in OTHER slots (for fair load). */
    public Map<Integer, Integer> getDutyCounts(LocalDate date, String session) throws SQLException {
        String sql = "SELECT fd.faculty_id, COUNT(*) AS total FROM faculty_duty fd "
                   + "JOIN exam e ON fd.exam_id = e.exam_id "
                   + "WHERE NOT (e.exam_date = ? AND e.session = ?) "
                   + "GROUP BY fd.faculty_id";
        Map<Integer, Integer> map = new HashMap<>();
        try (Connection con = DBConnection.get();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setDate(1, Date.valueOf(date));
            ps.setString(2, session);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) map.put(rs.getInt("faculty_id"), rs.getInt("total"));
            }
        }
        return map;
    }

    public void deleteForSlot(LocalDate date, String session) throws SQLException {
        String sql = "DELETE fd FROM faculty_duty fd JOIN exam e ON fd.exam_id = e.exam_id "
                   + "WHERE e.exam_date = ? AND e.session = ?";
        try (Connection con = DBConnection.get();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setDate(1, Date.valueOf(date));
            ps.setString(2, session);
            ps.executeUpdate();
        }
    }

    public void saveAll(List<FacultyDuty> list) throws SQLException {
        String sql = "INSERT INTO faculty_duty (exam_id, faculty_id, room_id, role) VALUES (?,?,?,?)";
        try (Connection con = DBConnection.get();
             PreparedStatement ps = con.prepareStatement(sql)) {
            con.setAutoCommit(false);
            for (FacultyDuty d : list) {
                ps.setInt(1, d.getExamId());
                ps.setInt(2, d.getFacultyId());
                if (d.getRoomId() == null) ps.setNull(3, Types.INTEGER);
                else ps.setInt(3, d.getRoomId());
                ps.setString(4, d.getRole());
                ps.addBatch();
            }
            ps.executeBatch();
            con.commit();
        }
    }

    /** Duty list, one text line per duty. */
    public List<String> report(LocalDate date, String session) throws SQLException {
        String sql = "SELECT fd.role, f.name, f.designation, d.code, r.room_name "
                   + "FROM faculty_duty fd "
                   + "JOIN exam e ON fd.exam_id = e.exam_id "
                   + "JOIN faculty f ON fd.faculty_id = f.faculty_id "
                   + "JOIN department d ON f.dept_id = d.dept_id "
                   + "LEFT JOIN room r ON fd.room_id = r.room_id "
                   + "WHERE e.exam_date = ? AND e.session = ? "
                   + "ORDER BY FIELD(fd.role, 'DCS', 'RELIEVING', 'INVIGILATOR'), r.room_name, f.name";
        List<String> lines = new ArrayList<>();
        try (Connection con = DBConnection.get();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setDate(1, Date.valueOf(date));
            ps.setString(2, session);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    String room = rs.getString("room_name");
                    lines.add(rs.getString("role") + " | " + rs.getString("name")
                            + " | " + rs.getString("designation") + " | " + rs.getString("code")
                            + " | " + (room == null ? "All rooms" : room));
                }
            }
        }
        return lines;
    }

    public int findIdByName(String name) throws SQLException {
        String sql = "SELECT faculty_id FROM faculty WHERE name = ?";
        try (Connection con = DBConnection.get();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, name);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next() ? rs.getInt(1) : -1;
            }
        }
    }

    /** Records that a faculty member teaches a subject. */
    public void assignSubject(int facultyId, String subjectCode) throws SQLException {
        String sql = "INSERT IGNORE INTO faculty_subject (faculty_id, subject_code) VALUES (?,?)";
        try (Connection con = DBConnection.get();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, facultyId);
            ps.setString(2, subjectCode);
            ps.executeUpdate();
        }
    }
}