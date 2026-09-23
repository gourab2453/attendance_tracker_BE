package com.example.attendance_tracker_be.repository;

import com.example.attendance_tracker_be.model.LeaveRequest;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;

public interface LeaveRequestRepository extends MongoRepository<LeaveRequest, String> {
    List<LeaveRequest> findByUserId(String userId);
}