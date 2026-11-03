package com.vtu.examsystem.service;

import com.vtu.examsystem.dao.AllotmentDAO;
import com.vtu.examsystem.dao.RoomDAO;
import com.vtu.examsystem.model.ExamCandidate;
import com.vtu.examsystem.model.Room;
import com.vtu.examsystem.model.RoomAllotment;

import java.sql.SQLException;
import java.time.LocalDate;
import java.util.*;

public class RoomAllotmentService {

    private final AllotmentDAO allotmentDao = new AllotmentDAO();
    private final RoomDAO roomDao = new RoomDAO();

    /** Allots rooms and seats for one exam slot. Returns the number of students seated. */
    public int allot(LocalDate date, String session) throws SQLException {
        List<ExamCandidate> candidates = allotmentDao.findCandidates(date, session);
        List<Room> rooms = roomDao.getAvailable();

        if (candidates.isEmpty())
            throw new IllegalStateException("No students have an exam in this slot.");
        int totalCapacity = rooms.stream().mapToInt(Room::getCapacity).sum();
        if (candidates.size() > totalCapacity)
            throw new IllegalStateException("Not enough seats: " + candidates.size()
                    + " students but capacity is only " + totalCapacity);

        // 1. One queue per branch
        Map<Integer, Deque<ExamCandidate>> byBranch = new LinkedHashMap<>();
        for (ExamCandidate c : candidates) {
            byBranch.computeIfAbsent(c.student().getDeptId(), k -> new ArrayDeque<>()).add(c);
        }

        // 2. Interleave: one from each branch, repeat (branch interchange)
        List<ExamCandidate> mixed = new ArrayList<>();
        boolean any = true;
        while (any) {
            any = false;
            for (Deque<ExamCandidate> queue : byBranch.values()) {
                if (!queue.isEmpty()) {
                    mixed.add(queue.poll());
                    any = true;
                }
            }
        }

        // 3. Fill rooms seat by seat
        List<RoomAllotment> result = new ArrayList<>();
        int next = 0;
        for (Room room : rooms) {
            for (int seat = 1; seat <= room.getCapacity() && next < mixed.size(); seat++) {
                ExamCandidate c = mixed.get(next++);
                result.add(new RoomAllotment(c.examId(), c.student().getUsn(), room.getRoomId(), seat));
            }
        }

        // 4. Save (replace any older allotment for this slot)
        allotmentDao.deleteForSlot(date, session);
        allotmentDao.saveAll(result);
        return result.size();
    }
}
