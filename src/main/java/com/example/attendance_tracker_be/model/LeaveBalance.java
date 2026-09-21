package com.example.attendance_tracker_be.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class LeaveBalance {

    @Builder.Default
    private int casualLeave = 12;

    @Builder.Default
    private int earnedLeave = 15;

    @Builder.Default
    private int sickLeave = 10;
}
