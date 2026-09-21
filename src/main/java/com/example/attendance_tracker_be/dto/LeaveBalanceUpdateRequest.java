package com.example.attendance_tracker_be.dto;

import lombok.Data;

@Data
public class LeaveBalanceUpdateRequest {
    private int casualLeave;
    private int earnedLeave;
    private int sickLeave;
}