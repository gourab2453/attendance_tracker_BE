package com.example.attendance_tracker_be.repository;

import com.example.attendance_tracker_be.model.AttendanceRecord;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface AttendanceRecordRepository extends MongoRepository<AttendanceRecord, String> {

    Optional<AttendanceRecord> findByUserIdAndDate(String userId, LocalDate date);

    List<AttendanceRecord> findByUserIdAndDateBetween(String userId, LocalDate from, LocalDate to);

    List<AttendanceRecord> findByDateBetween(LocalDate from, LocalDate to);
}