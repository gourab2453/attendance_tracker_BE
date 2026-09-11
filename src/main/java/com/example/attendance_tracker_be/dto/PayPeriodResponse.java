package com.example.attendance_tracker_be.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PayPeriodResponse {
    private String type;
    private LocalDate periodStart;
    private LocalDate periodEnd;
}