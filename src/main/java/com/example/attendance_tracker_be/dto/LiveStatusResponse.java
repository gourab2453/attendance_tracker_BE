package com.example.attendance_tracker_be.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class LiveStatusResponse {
    private long online;
    private long working;
    private long onBreak;
    private long unavailable;
}