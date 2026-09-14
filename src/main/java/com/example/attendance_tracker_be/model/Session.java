package com.example.attendance_tracker_be.model;

import java.time.LocalDateTime;

public class Session {
    private LocalDateTime clockIn;
    private LocalDateTime clockOut;

    public Session() {}

    public Session(LocalDateTime clockIn, LocalDateTime clockOut) {
        this.clockIn = clockIn;
        this.clockOut = clockOut;
    }

    public LocalDateTime getClockIn() { return clockIn; }
    public void setClockIn(LocalDateTime clockIn) { this.clockIn = clockIn; }
    public LocalDateTime getClockOut() { return clockOut; }
    public void setClockOut(LocalDateTime clockOut) { this.clockOut = clockOut; }
}