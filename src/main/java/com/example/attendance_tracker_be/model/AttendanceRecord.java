package com.example.attendance_tracker_be.model;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.CompoundIndex;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Document(collection = "attendance_records")
@CompoundIndex(name = "user_date_idx", def = "{'userId': 1, 'date': 1}", unique = true)
public class AttendanceRecord {

    @Id
    private String id;

    private String userId;
    private LocalDate date;

    private LocalDateTime clockIn;
    private LocalDateTime clockOut;

    private List<BreakEntry> breaks = new ArrayList<>();

    private double totalHours;
    private double overtimeHours;

    private AttendanceStatus status;

    public AttendanceRecord() {}

    // getters and setters

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public String getUserId() { return userId; }
    public void setUserId(String userId) { this.userId = userId; }

    public LocalDate getDate() { return date; }
    public void setDate(LocalDate date) { this.date = date; }

    public LocalDateTime getClockIn() { return clockIn; }
    public void setClockIn(LocalDateTime clockIn) { this.clockIn = clockIn; }

    public LocalDateTime getClockOut() { return clockOut; }
    public void setClockOut(LocalDateTime clockOut) { this.clockOut = clockOut; }

    public List<BreakEntry> getBreaks() { return breaks; }
    public void setBreaks(List<BreakEntry> breaks) { this.breaks = breaks; }

    public double getTotalHours() { return totalHours; }
    public void setTotalHours(double totalHours) { this.totalHours = totalHours; }

    public double getOvertimeHours() { return overtimeHours; }
    public void setOvertimeHours(double overtimeHours) { this.overtimeHours = overtimeHours; }

    public AttendanceStatus getStatus() { return status; }
    public void setStatus(AttendanceStatus status) { this.status = status; }
}