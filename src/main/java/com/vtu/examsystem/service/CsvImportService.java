package com.vtu.examsystem.service;

import com.vtu.examsystem.dao.DepartmentDAO;
import com.vtu.examsystem.dao.StudentDAO;
import com.vtu.examsystem.model.Student;

import java.io.BufferedReader;
import java.io.FileReader;
import java.util.*;

public class CsvImportService {

    public static class Result {
        public int added = 0;
        public List<String> errors = new ArrayList<>();
    }

    private final DepartmentDAO deptDao = new DepartmentDAO();
    private final StudentDAO studentDao = new StudentDAO();

    /** CSV format: usn,name,dept,semester,email,quota */
    public Result importStudents(String path) {
        Result result = new Result();
        Map<String, Integer> deptCache = new HashMap<>();
        Set<String> validQuota = Set.of("REGULAR", "DIPLOMA", "SNQ");

        try (BufferedReader br = new BufferedReader(new FileReader(path))) {
            String line = br.readLine();           // skip header
            int lineNo = 1;
            while ((line = br.readLine()) != null) {
                lineNo++;
                if (line.trim().isEmpty()) continue;
                String[] p = line.split(",", -1);
                if (p.length < 5) { result.errors.add("Line " + lineNo + ": missing columns"); continue; }

                String usn = p[0].trim().toUpperCase();
                String name = p[1].trim();
                String dept = p[2].trim().toUpperCase();
                String sem = p[3].trim();
                String email = p[4].trim();
                String quota = (p.length > 5 && !p[5].trim().isEmpty()) ? p[5].trim().toUpperCase() : "REGULAR";

                if (usn.isEmpty() || name.isEmpty() || dept.isEmpty() || sem.isEmpty()) {
                    result.errors.add("Line " + lineNo + ": empty required field"); continue;
                }
                int semester;
                try {
                    semester = Integer.parseInt(sem);
                    if (semester < 1 || semester > 8) throw new NumberFormatException();
                } catch (NumberFormatException e) {
                    result.errors.add("Line " + lineNo + ": invalid semester '" + sem + "'"); continue;
                }
                if (!validQuota.contains(quota)) {
                    result.errors.add("Line " + lineNo + ": invalid quota '" + quota + "'"); continue;
                }
                if (!deptCache.containsKey(dept)) deptCache.put(dept, deptDao.getIdByCode(dept));
                int deptId = deptCache.get(dept);
                if (deptId == -1) {
                    result.errors.add("Line " + lineNo + ": unknown department '" + dept + "'"); continue;
                }
                if (studentDao.exists(usn)) {
                    result.errors.add("Line " + lineNo + ": duplicate USN " + usn); continue;
                }
                studentDao.add(new Student(usn, name, deptId, semester, email, quota));
                result.added++;
            }
        } catch (Exception e) {
            result.errors.add("File error: " + e.getMessage());
        }
        return result;
    }
}