package com.example.attendance_tracker_be.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class EmployeeStatsResponse {
    private String userId;
    private String name;
    private double totalHours;
    private double overtimeHours;
    private long presentDays;
    private long lateDays;
    private long absentDays;
}
