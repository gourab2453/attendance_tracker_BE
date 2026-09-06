package com.example.attendance_tracker_be.service;

import com.example.attendance_tracker_be.dto.AttendanceRecordResponse;
import com.example.attendance_tracker_be.exception.ResourceNotFoundException;
import com.example.attendance_tracker_be.model.*;
import com.example.attendance_tracker_be.repository.AttendanceRecordRepository;
import com.example.attendance_tracker_be.repository.UserRepository;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class AttendanceService {

    private static final LocalTime LATE_CUTOFF = LocalTime.of(9, 15); // adjust as needed
    private static final double STANDARD_DAY_HOURS = 8.0;

    private final AttendanceRecordRepository attendanceRepository;
    private final UserRepository userRepository;

    public AttendanceService(AttendanceRecordRepository attendanceRepository,
                             UserRepository userRepository) {
        this.attendanceRepository = attendanceRepository;
        this.userRepository = userRepository;
    }

    public AttendanceRecordResponse clockIn(String userId) {
        LocalDate today = LocalDate.now();

        attendanceRepository.findByUserIdAndDate(userId, today).ifPresent(r -> {
            throw new IllegalStateException("Already clocked in today");
        });

        AttendanceRecord record = new AttendanceRecord();
        record.setUserId(userId);
        record.setDate(today);
        LocalDateTime now = LocalDateTime.now();
        record.setClockIn(now);
        record.setStatus(now.toLocalTime().isAfter(LATE_CUTOFF)
                ? AttendanceStatus.LATE
                : AttendanceStatus.PRESENT);

        AttendanceRecord saved = attendanceRepository.save(record);
        updateUserStatus(userId, UserStatus.WORKING);

        return toResponse(saved);
    }

    public AttendanceRecordResponse clockOut(String userId) {
        AttendanceRecord record = getTodayRecordOrThrow(userId);

        if (record.getClockOut() != null) {
            throw new IllegalStateException("Already clocked out today");
        }

        record.setClockOut(LocalDateTime.now());
        recalculateHours(record);

        AttendanceRecord saved = attendanceRepository.save(record);
        updateUserStatus(userId, UserStatus.UNAVAILABLE);

        return toResponse(saved);
    }

    public AttendanceRecordResponse startBreak(String userId) {
        AttendanceRecord record = getTodayRecordOrThrow(userId);

        boolean alreadyOnBreak = record.getBreaks().stream()
                .anyMatch(b -> b.getEnd() == null);
        if (alreadyOnBreak) {
            throw new IllegalStateException("Already on break");
        }
        if (record.getClockOut() != null) {
            throw new IllegalStateException("Cannot start break after clocking out");
        }

        record.getBreaks().add(new BreakEntry(LocalDateTime.now(), null));
        AttendanceRecord saved = attendanceRepository.save(record);
        updateUserStatus(userId, UserStatus.ON_BREAK);

        return toResponse(saved);
    }

    public AttendanceRecordResponse endBreak(String userId) {
        AttendanceRecord record = getTodayRecordOrThrow(userId);

        BreakEntry openBreak = record.getBreaks().stream()
                .filter(b -> b.getEnd() == null)
                .findFirst()
                .orElseThrow(() -> new IllegalStateException("No active break to end"));

        openBreak.setEnd(LocalDateTime.now());
        AttendanceRecord saved = attendanceRepository.save(record);
        updateUserStatus(userId, UserStatus.WORKING);

        return toResponse(saved);
    }

    public AttendanceRecordResponse getToday(String userId) {
        return attendanceRepository.findByUserIdAndDate(userId, LocalDate.now())
                .map(this::toResponse)
                .orElse(null); // no record yet today — controller returns 204/empty
    }

    public List<AttendanceRecordResponse> getRange(String userId, LocalDate from, LocalDate to) {
        return attendanceRepository.findByUserIdAndDateBetween(userId, from, to)
                .stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
    }

    // --- helpers ---

    private AttendanceRecord getTodayRecordOrThrow(String userId) {
        return attendanceRepository.findByUserIdAndDate(userId, LocalDate.now())
                .orElseThrow(() -> new ResourceNotFoundException("No clock-in found for today"));
    }

    private void recalculateHours(AttendanceRecord record) {
        if (record.getClockIn() == null || record.getClockOut() == null) return;

        long totalMinutes = Duration.between(record.getClockIn(), record.getClockOut()).toMinutes();

        long breakMinutes = record.getBreaks().stream()
                .filter(b -> b.getStart() != null && b.getEnd() != null)
                .mapToLong(b -> Duration.between(b.getStart(), b.getEnd()).toMinutes())
                .sum();

        double workedHours = (totalMinutes - breakMinutes) / 60.0;
        record.setTotalHours(Math.round(workedHours * 100.0) / 100.0);

        double overtime = Math.max(0, workedHours - STANDARD_DAY_HOURS);
        record.setOvertimeHours(Math.round(overtime * 100.0) / 100.0);
    }

    private void updateUserStatus(String userId, UserStatus status) {
        userRepository.findById(userId).ifPresent(user -> {
            user.setStatus(status);
            userRepository.save(user);
        });
    }

    private AttendanceRecordResponse toResponse(AttendanceRecord record) {
        AttendanceRecordResponse dto = new AttendanceRecordResponse();
        dto.setId(record.getId());
        dto.setUserId(record.getUserId());
        dto.setDate(record.getDate());
        dto.setClockIn(record.getClockIn());
        dto.setClockOut(record.getClockOut());
        dto.setBreaks(record.getBreaks());
        dto.setTotalHours(record.getTotalHours());
        dto.setOvertimeHours(record.getOvertimeHours());
        dto.setStatus(record.getStatus());
        return dto;
    }
}