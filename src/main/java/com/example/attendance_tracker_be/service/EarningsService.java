package com.example.attendance_tracker_be.service;

import com.example.attendance_tracker_be.dto.LiveEarningsResponse;
import com.example.attendance_tracker_be.dto.PayPeriodResponse;
import com.example.attendance_tracker_be.dto.PaycheckPreviewResponse;
import com.example.attendance_tracker_be.exception.ResourceNotFoundException;
import com.example.attendance_tracker_be.model.AttendanceRecord;
import com.example.attendance_tracker_be.model.User;
import com.example.attendance_tracker_be.repository.AttendanceRecordRepository;
import com.example.attendance_tracker_be.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.DayOfWeek;
import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.temporal.TemporalAdjusters;
import java.util.List;

@Service
@RequiredArgsConstructor
public class EarningsService {

    private static final BigDecimal OVERTIME_MULTIPLIER = new BigDecimal("1.5");

    private final AttendanceRecordRepository attendanceRepository;
    private final UserRepository userRepository;

    public LiveEarningsResponse getLiveEarnings(String userId) {
        User user = getUserOrThrow(userId);
        BigDecimal rate = user.getPayRate().getRegular();

        return attendanceRepository.findByUserIdAndDate(userId, LocalDate.now())
                .filter(r -> r.getClockIn() != null && r.getClockOut() == null)
                .map(record -> {
                    double elapsedHours = Duration.between(record.getClockIn(), LocalDateTime.now())
                            .toMinutes() / 60.0;
                    double roundedHours = Math.round(elapsedHours * 100.0) / 100.0;

                    BigDecimal earnings = rate.multiply(BigDecimal.valueOf(roundedHours))
                            .setScale(2, RoundingMode.HALF_UP);

                    return LiveEarningsResponse.builder()
                            .clockedIn(true)
                            .clockIn(record.getClockIn())
                            .elapsedHours(roundedHours)
                            .ratePerHour(rate)
                            .estimatedEarnings(earnings)
                            .build();
                })
                .orElse(LiveEarningsResponse.builder()
                        .clockedIn(false)
                        .elapsedHours(0)
                        .ratePerHour(rate)
                        .estimatedEarnings(BigDecimal.ZERO.setScale(2))
                        .build());
    }

    public PaycheckPreviewResponse getPaycheckPreview(String userId, String period) {
        User user = getUserOrThrow(userId);
        LocalDate[] range = resolvePeriodRange(period);
        LocalDate start = range[0];
        LocalDate end = range[1];

        List<AttendanceRecord> records = attendanceRepository
                .findByUserIdAndDateBetween(userId, start, end);

        double regularHours = records.stream()
                .mapToDouble(r -> Math.max(0, r.getTotalHours() - r.getOvertimeHours()))
                .sum();
        double overtimeHours = records.stream()
                .mapToDouble(AttendanceRecord::getOvertimeHours)
                .sum();

        BigDecimal regularRate = user.getPayRate().getRegular();
        BigDecimal overtimeRate = user.getPayRate().getOvertime().compareTo(BigDecimal.ZERO) > 0
                ? user.getPayRate().getOvertime()
                : regularRate.multiply(OVERTIME_MULTIPLIER); // fallback if overtime rate not set

        BigDecimal regularPay = regularRate.multiply(BigDecimal.valueOf(regularHours))
                .setScale(2, RoundingMode.HALF_UP);
        BigDecimal overtimePay = overtimeRate.multiply(BigDecimal.valueOf(overtimeHours))
                .setScale(2, RoundingMode.HALF_UP);

        return PaycheckPreviewResponse.builder()
                .periodStart(start)
                .periodEnd(end)
                .regularHours(round2(regularHours))
                .overtimeHours(round2(overtimeHours))
                .regularPay(regularPay)
                .overtimePay(overtimePay)
                .estimatedGrossPay(regularPay.add(overtimePay))
                .build();
    }

    public PayPeriodResponse getPayPeriod(String type) {
        LocalDate[] range = resolvePeriodRange(type);
        return PayPeriodResponse.builder()
                .type(type)
                .periodStart(range[0])
                .periodEnd(range[1])
                .build();
    }

    // --- helpers ---

    private User getUserOrThrow(String userId) {
        return userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));
    }

    private LocalDate[] resolvePeriodRange(String type) {
        LocalDate today = LocalDate.now();

        return switch (type.toLowerCase()) {
            case "weekly" -> new LocalDate[]{
                    today.with(TemporalAdjusters.previousOrSame(DayOfWeek.MONDAY)),
                    today.with(TemporalAdjusters.nextOrSame(DayOfWeek.SUNDAY))
            };
            case "biweekly" -> {
                LocalDate monday = today.with(TemporalAdjusters.previousOrSame(DayOfWeek.MONDAY));
                yield new LocalDate[]{monday.minusWeeks(1), monday.plusDays(6)};
            }
            case "semimonthly" -> {
                if (today.getDayOfMonth() <= 15) {
                    yield new LocalDate[]{today.withDayOfMonth(1), today.withDayOfMonth(15)};
                } else {
                    yield new LocalDate[]{
                            today.withDayOfMonth(16),
                            today.with(TemporalAdjusters.lastDayOfMonth())
                    };
                }
            }
            default -> throw new IllegalArgumentException("Invalid period type: " + type);
        };
    }

    private double round2(double value) {
        return Math.round(value * 100.0) / 100.0;
    }
}