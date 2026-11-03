package com.vtu.examsystem.service;

import com.vtu.examsystem.dao.DutyDAO;
import com.vtu.examsystem.dao.FacultyDAO;
import com.vtu.examsystem.model.Faculty;
import com.vtu.examsystem.model.FacultyDuty;
import com.vtu.examsystem.model.Room;

import java.sql.SQLException;
import java.time.LocalDate;
import java.util.*;

public class FacultyAllotmentService {

    public static final int ROOMS_PER_RELIEVING = 10;

    private final DutyDAO dutyDao = new DutyDAO();
    private final FacultyDAO facultyDao = new FacultyDAO();

    /** Assigns invigilators, relieving supervisors and a DCS. Returns the number of duties. */
    public int allot(LocalDate date, String session, int invigilatorsPerRoom) throws SQLException {
        List<Room> rooms = dutyDao.getRoomsInUse(date, session);
        if (rooms.isEmpty())
            throw new IllegalStateException("No rooms allotted for this slot. Run room allotment first.");
        int examId = dutyDao.getSlotExamId(date, session);

        Set<Integer> conflicted = dutyDao.getConflictedFaculty(date, session);
        Map<Integer, Integer> load = dutyDao.getDutyCounts(date, session);

        List<Faculty> assistants = available(facultyDao.getByDesignation("ASSISTANT_PROF"), conflicted, load);
        List<Faculty> seniors    = available(facultyDao.getByDesignation("ASSOCIATE_PROF"), conflicted, load);
        List<Faculty> professors = available(facultyDao.getByDesignation("PROFESSOR"), conflicted, load);

        int needInvigilators = rooms.size() * invigilatorsPerRoom;
        int needRelieving = (int) Math.ceil(rooms.size() / (double) ROOMS_PER_RELIEVING);

        if (assistants.size() < needInvigilators)
            throw new IllegalStateException("Need " + needInvigilators + " Assistant Professors but only "
                    + assistants.size() + " are available.");
        if (seniors.size() < needRelieving)
            throw new IllegalStateException("Need " + needRelieving + " relieving supervisor(s) but only "
                    + seniors.size() + " Associate Professor(s) are available.");
        if (professors.isEmpty())
            throw new IllegalStateException("No Professor available for DCS.");

        List<FacultyDuty> duties = new ArrayList<>();
        int idx = 0;
        for (Room room : rooms) {
            for (int i = 0; i < invigilatorsPerRoom; i++) {
                duties.add(new FacultyDuty(examId, assistants.get(idx++).getFacultyId(),
                        room.getRoomId(), "INVIGILATOR"));
            }
        }
        for (int i = 0; i < needRelieving; i++) {
            duties.add(new FacultyDuty(examId, seniors.get(i).getFacultyId(), null, "RELIEVING"));
        }
        duties.add(new FacultyDuty(examId, professors.get(0).getFacultyId(), null, "DCS"));

        dutyDao.deleteForSlot(date, session);
        dutyDao.saveAll(duties);
        return duties.size();
    }

    /** Removes conflicted faculty, then sorts by fewest duties so far. */
    private List<Faculty> available(List<Faculty> all, Set<Integer> conflicted, Map<Integer, Integer> load) {
        List<Faculty> list = new ArrayList<>();
        for (Faculty f : all) {
            if (!conflicted.contains(f.getFacultyId())) list.add(f);
        }
        list.sort(Comparator
                .comparingInt((Faculty f) -> load.getOrDefault(f.getFacultyId(), 0))
                .thenComparing(Faculty::getName));
        return list;
    }
}