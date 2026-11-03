package com.vtu.examsystem.model;

public class Room {
    private int roomId;
    private String roomName;
    private int capacity;
    private int deptId;
    private boolean available;

    public Room(int roomId, String roomName, int capacity, int deptId, boolean available) {
        this.roomId = roomId; this.roomName = roomName; this.capacity = capacity;
        this.deptId = deptId; this.available = available;
    }
    public int getRoomId() { return roomId; }
    public String getRoomName() { return roomName; }
    public int getCapacity() { return capacity; }
    public int getDeptId() { return deptId; }
    public boolean isAvailable() { return available; }
}