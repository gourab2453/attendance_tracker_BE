package com.example.attendance_tracker_be.controller;

import com.example.attendance_tracker_be.dto.EmployeeStatsResponse;
import com.example.attendance_tracker_be.dto.LiveStatusResponse;
import com.example.attendance_tracker_be.service.AdminService;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;

@RestController
@RequestMapping("/api/admin")
@RequiredArgsConstructor
@PreAuthorize("hasRole('ADMIN')")
public class AdminController {

    private final AdminService adminService;

    @GetMapping("/live-status")
    public ResponseEntity<LiveStatusResponse> getLiveStatus() {
        return ResponseEntity.ok(adminService.getLiveStatus());
    }

    @GetMapping("/employees/{id}/stats")
    public ResponseEntity<EmployeeStatsResponse> getEmployeeStats(
            @PathVariable String id,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate from,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate to) {
        return ResponseEntity.ok(adminService.getEmployeeStats(id, from, to));
    }
}