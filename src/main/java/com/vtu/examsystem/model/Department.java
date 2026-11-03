package com.vtu.examsystem.model;

public class Department {
    private int deptId;
    private String code;
    private String name;
    private int intake;
    private int diplomaSeats;
    private int snqSeats;

    public Department(int deptId, String code, String name, int intake, int diplomaSeats, int snqSeats) {
        this.deptId = deptId;
        this.code = code;
        this.name = name;
        this.intake = intake;
        this.diplomaSeats = diplomaSeats;
        this.snqSeats = snqSeats;
    }

    public int getDeptId() { return deptId; }
    public String getCode() { return code; }
    public String getName() { return name; }
    public int getIntake() { return intake; }
    public int getDiplomaSeats() { return diplomaSeats; }
    public int getSnqSeats() { return snqSeats; }
    public int getTotalStrength() { return intake + diplomaSeats + snqSeats; }

    @Override
    public String toString() { return code; }
}