package com.example.attendance_tracker_be.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class LiveEarningsResponse {
    private boolean clockedIn;
    private LocalDateTime clockIn;
    private double elapsedHours;
    private BigDecimal ratePerHour;
    private BigDecimal estimatedEarnings;
}