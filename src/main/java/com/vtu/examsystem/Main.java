package com.vtu.examsystem;

import com.vtu.examsystem.dao.*;
import com.vtu.examsystem.model.*;
import com.vtu.examsystem.service.*;

import java.time.LocalDate;

public class Main {
    public static void main(String[] args) {
        try {
            DepartmentDAO deptDao = new DepartmentDAO();
            SubjectDAO subjectDao = new SubjectDAO();
            ExamDAO examDao = new ExamDAO();
            FacultyDAO facultyDao = new FacultyDAO();
            AllotmentDAO allotmentDao = new AllotmentDAO();
            DutyDAO dutyDao = new DutyDAO();

            LocalDate date = LocalDate.of(2026, 12, 1);
            String session = "MORNING";

            // Subjects (semester 5, one per branch)
            String[][] subjects = {
                {"CSE",  "CS501", "Computer Networks"},
                {"ISE",  "IS501", "Software Engineering"},
                {"AIML", "AI501", "Machine Learning"},
                {"EC",   "EC501", "VLSI Design"},
                {"AIDS", "AD501", "Data Mining"}
            };
            for (String[] s : subjects) {
                if (!subjectDao.exists(s[1])) {
                    subjectDao.add(new Subject(s[1], s[2], deptDao.getIdByCode(s[0]), 5));
                }
            }

            // Exams
            if (examDao.getAll().isEmpty()) {
                for (String[] s : subjects) examDao.add(new Exam(0, s[1], date, session));
            }

            // Extra Assistant Professors so there are enough invigilators
            if (facultyDao.getAll().size() < 9) {
                facultyDao.add(new Faculty(0, "Gita N",   deptDao.getIdByCode("CSE"),  "ASSISTANT_PROF", "gita@example.com"));
                facultyDao.add(new Faculty(0, "Harish V", deptDao.getIdByCode("EC"),   "ASSISTANT_PROF", "harish@example.com"));
                facultyDao.add(new Faculty(0, "Indu R",   deptDao.getIdByCode("AIML"), "ASSISTANT_PROF", "indu@example.com"));
                facultyDao.add(new Faculty(0, "Jagan T",  deptDao.getIdByCode("AIDS"), "ASSISTANT_PROF", "jagan@example.com"));
            }

            // Subject restriction demo: Asha Rao teaches CS501, so she must not invigilate it
            int asha = dutyDao.findIdByName("Asha Rao");
            if (asha != -1) dutyDao.assignSubject(asha, "CS501");

            // Room allotment
            int seated = new RoomAllotmentService().allot(date, session);
            System.out.println("Students seated: " + seated);

            // Faculty allotment (2 invigilators per room)
            int duties = new FacultyAllotmentService().allot(date, session, 2);
            System.out.println("Faculty duties assigned: " + duties);

            System.out.println("--- Room-wise list ---");
            for (String line : allotmentDao.roomWiseReport(date, session)) System.out.println(line);

            System.out.println("--- Faculty duty list ---");
            for (String line : dutyDao.report(date, session)) System.out.println(line);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}