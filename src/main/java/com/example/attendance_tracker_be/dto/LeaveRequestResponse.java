package com.example.attendance_tracker_be.dto;

import com.example.attendance_tracker_be.model.LeaveRequestStatus;
import com.example.attendance_tracker_be.model.LeaveType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class LeaveRequestResponse {
    private String id;
    private String userId;
    private String userName;
    private String userEmail;
    private LeaveType type;
    private LocalDate startDate;
    private LocalDate endDate;
    private String reason;
    private int days;
    private LeaveRequestStatus status;
    private LocalDateTime appliedAt;
    private String reviewedBy;
    private LocalDateTime reviewedAt;
}
