package com.vtu.examsystem.model;

public class RoomAllotment {
    private int examId;
    private String usn;
    private int roomId;
    private int seatNo;

    public RoomAllotment(int examId, String usn, int roomId, int seatNo) {
        this.examId = examId; this.usn = usn; this.roomId = roomId; this.seatNo = seatNo;
    }
    public int getExamId() { return examId; }
    public String getUsn() { return usn; }
    public int getRoomId() { return roomId; }
    public int getSeatNo() { return seatNo; }
}