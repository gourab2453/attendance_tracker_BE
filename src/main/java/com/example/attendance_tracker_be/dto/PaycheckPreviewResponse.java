package com.example.attendance_tracker_be.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PaycheckPreviewResponse {
    private LocalDate periodStart;
    private LocalDate periodEnd;
    private double regularHours;
    private double overtimeHours;
    private BigDecimal regularPay;
    private BigDecimal overtimePay;
    private BigDecimal estimatedGrossPay;
}
