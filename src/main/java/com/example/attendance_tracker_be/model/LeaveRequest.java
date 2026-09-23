package com.example.attendance_tracker_be.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Document(collection = "leave_requests")
public class LeaveRequest {

    @Id
    private String id;

    private String userId;
    private LeaveType type;
    private LocalDate startDate;
    private LocalDate endDate;
    private String reason;

    @Builder.Default
    private LeaveRequestStatus status = LeaveRequestStatus.PENDING;

    @Builder.Default
    private LocalDateTime appliedAt = LocalDateTime.now();

    private String reviewedBy;
    private LocalDateTime reviewedAt;
}