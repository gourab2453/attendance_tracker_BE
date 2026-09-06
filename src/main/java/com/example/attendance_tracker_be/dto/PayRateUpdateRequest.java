package com.example.attendance_tracker_be.dto;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.math.BigDecimal;

@Data
public class PayRateUpdateRequest {

    @NotNull
    @DecimalMin(value = "0.0", message = "regular rate cannot be negative")
    private BigDecimal regular;

    @NotNull
    @DecimalMin(value = "0.0", message = "overtime rate cannot be negative")
    private BigDecimal overtime;

    @NotNull
    @DecimalMin(value = "0.0", message = "weekend rate cannot be negative")
    private BigDecimal weekend;

    @NotNull
    @DecimalMin(value = "0.0", message = "holiday rate cannot be negative")
    private BigDecimal holiday;
}