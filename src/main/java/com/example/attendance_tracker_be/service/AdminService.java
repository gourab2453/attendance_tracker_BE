package com.example.attendance_tracker_be.service;

import com.example.attendance_tracker_be.dto.EmployeeStatsResponse;
import com.example.attendance_tracker_be.dto.LiveStatusResponse;
import com.example.attendance_tracker_be.exception.ResourceNotFoundException;
import com.example.attendance_tracker_be.model.AttendanceRecord;
import com.example.attendance_tracker_be.model.AttendanceStatus;
import com.example.attendance_tracker_be.model.User;
import com.example.attendance_tracker_be.model.UserStatus;
import com.example.attendance_tracker_be.repository.AttendanceRecordRepository;
import com.example.attendance_tracker_be.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.util.List;

@Service
@RequiredArgsConstructor
public class AdminService {

    private final UserRepository userRepository;
    private final AttendanceRecordRepository attendanceRepository;

    public LiveStatusResponse getLiveStatus() {
        List<User> users = userRepository.findAll();

        return LiveStatusResponse.builder()
                .online(users.stream().filter(u -> u.getStatus() == UserStatus.ONLINE).count())
                .working(users.stream().filter(u -> u.getStatus() == UserStatus.WORKING).count())
                .onBreak(users.stream().filter(u -> u.getStatus() == UserStatus.ON_BREAK).count())
                .unavailable(users.stream().filter(u -> u.getStatus() == UserStatus.UNAVAILABLE).count())
                .build();
    }

    public EmployeeStatsResponse getEmployeeStats(String userId, LocalDate from, LocalDate to) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        LocalDate start = from != null ? from : LocalDate.now().withDayOfMonth(1);
        LocalDate end = to != null ? to : LocalDate.now();

        List<AttendanceRecord> records = attendanceRepository.findByUserIdAndDateBetween(userId, start, end);

        double totalHours = records.stream().mapToDouble(AttendanceRecord::getTotalHours).sum();
        double overtimeHours = records.stream().mapToDouble(AttendanceRecord::getOvertimeHours).sum();
        long presentDays = records.stream()
                .filter(r -> r.getStatus() == AttendanceStatus.PRESENT || r.getStatus() == AttendanceStatus.LATE)
                .count();
        long lateDays = records.stream().filter(r -> r.getStatus() == AttendanceStatus.LATE).count();

        long totalWeekdays = start.datesUntil(end.plusDays(1))
                .filter(d -> d.getDayOfWeek() != DayOfWeek.SATURDAY && d.getDayOfWeek() != DayOfWeek.SUNDAY)
                .count();
        long absentDays = Math.max(0, totalWeekdays - presentDays);

        return EmployeeStatsResponse.builder()
                .userId(user.getId())
                .name(user.getName())
                .totalHours(round2(totalHours))
                .overtimeHours(round2(overtimeHours))
                .presentDays(presentDays)
                .lateDays(lateDays)
                .absentDays(absentDays)
                .build();
    }

    private double round2(double value) {
        return Math.round(value * 100.0) / 100.0;
    }
}