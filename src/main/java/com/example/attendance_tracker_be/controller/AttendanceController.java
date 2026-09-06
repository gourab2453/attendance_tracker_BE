package com.example.attendance_tracker_be.controller;

import org.springframework.security.access.prepost.PreAuthorize;
import com.example.attendance_tracker_be.dto.AttendanceRecordResponse;
import com.example.attendance_tracker_be.service.AttendanceService;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/api/attendance")
public class AttendanceController {

    private final AttendanceService attendanceService;

    public AttendanceController(AttendanceService attendanceService) {
        this.attendanceService = attendanceService;
    }

    @PostMapping("/clock-in")
    public ResponseEntity<AttendanceRecordResponse> clockIn(Authentication auth) {
        String userId = auth.getName(); // adjust based on how you store principal (userId vs email)
        return ResponseEntity.ok(attendanceService.clockIn(userId));
    }

    @PostMapping("/clock-out")
    public ResponseEntity<AttendanceRecordResponse> clockOut(Authentication auth) {
        String userId = auth.getName();
        return ResponseEntity.ok(attendanceService.clockOut(userId));
    }

    @PostMapping("/break/start")
    public ResponseEntity<AttendanceRecordResponse> startBreak(Authentication auth) {
        String userId = auth.getName();
        return ResponseEntity.ok(attendanceService.startBreak(userId));
    }

    @PostMapping("/break/end")
    public ResponseEntity<AttendanceRecordResponse> endBreak(Authentication auth) {
        String userId = auth.getName();
        return ResponseEntity.ok(attendanceService.endBreak(userId));
    }

    @GetMapping("/me/today")
    public ResponseEntity<AttendanceRecordResponse> getToday(Authentication auth) {
        AttendanceRecordResponse response = attendanceService.getToday(auth.getName());
        return response == null ? ResponseEntity.noContent().build() : ResponseEntity.ok(response);
    }

    @GetMapping("/me")
    public ResponseEntity<List<AttendanceRecordResponse>> getMyRange(
            Authentication auth,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate from,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate to) {
        return ResponseEntity.ok(attendanceService.getRange(auth.getName(), from, to));
    }

    @GetMapping("/{userId}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<AttendanceRecordResponse>> getUserRange(
            @PathVariable String userId,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate from,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate to) {
        return ResponseEntity.ok(attendanceService.getRange(userId, from, to));
    }
}