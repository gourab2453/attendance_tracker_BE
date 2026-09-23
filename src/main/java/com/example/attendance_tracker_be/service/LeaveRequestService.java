package com.example.attendance_tracker_be.service;

import com.example.attendance_tracker_be.dto.LeaveRequestCreateRequest;
import com.example.attendance_tracker_be.dto.LeaveRequestResponse;
import com.example.attendance_tracker_be.exception.InsufficientLeaveBalanceException;
import com.example.attendance_tracker_be.exception.ResourceNotFoundException;
import com.example.attendance_tracker_be.model.*;
import com.example.attendance_tracker_be.repository.LeaveRequestRepository;
import com.example.attendance_tracker_be.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.List;

@Service
@RequiredArgsConstructor
public class LeaveRequestService {

    private final LeaveRequestRepository leaveRequestRepository;
    private final UserRepository userRepository;

    public LeaveRequestResponse applyForLeave(String userId, LeaveRequestCreateRequest request) {
        if (request.getEndDate().isBefore(request.getStartDate())) {
            throw new IllegalArgumentException("endDate cannot be before startDate");
        }

        LeaveRequest leaveRequest = LeaveRequest.builder()
                .userId(userId)
                .type(request.getType())
                .startDate(request.getStartDate())
                .endDate(request.getEndDate())
                .reason(request.getReason())
                .build();

        return toResponse(leaveRequestRepository.save(leaveRequest));
    }

    public List<LeaveRequestResponse> getMyLeaveRequests(String userId) {
        return leaveRequestRepository.findByUserId(userId).stream()
                .map(this::toResponse)
                .toList();
    }

    public List<LeaveRequestResponse> getAllLeaveRequests() {
        return leaveRequestRepository.findAll().stream()
                .map(this::toResponse)
                .toList();
    }

    public LeaveRequestResponse approve(String requestId, String reviewerId) {
        LeaveRequest leaveRequest = findRequestOrThrow(requestId);

        if (leaveRequest.getStatus() != LeaveRequestStatus.PENDING) {
            throw new IllegalStateException("Only pending requests can be approved");
        }

        User user = userRepository.findById(leaveRequest.getUserId())
                .orElseThrow(() -> new ResourceNotFoundException("User not found: " + leaveRequest.getUserId()));

        int days = daysRequested(leaveRequest);
        deductBalance(user, leaveRequest.getType(), days);
        userRepository.save(user);

        leaveRequest.setStatus(LeaveRequestStatus.APPROVED);
        leaveRequest.setReviewedBy(reviewerId);
        leaveRequest.setReviewedAt(LocalDateTime.now());

        return toResponse(leaveRequestRepository.save(leaveRequest));
    }

    public LeaveRequestResponse reject(String requestId, String reviewerId) {
        LeaveRequest leaveRequest = findRequestOrThrow(requestId);

        if (leaveRequest.getStatus() != LeaveRequestStatus.PENDING) {
            throw new IllegalStateException("Only pending requests can be rejected");
        }

        leaveRequest.setStatus(LeaveRequestStatus.REJECTED);
        leaveRequest.setReviewedBy(reviewerId);
        leaveRequest.setReviewedAt(LocalDateTime.now());

        return toResponse(leaveRequestRepository.save(leaveRequest));
    }

    // --- helpers ---

    private LeaveRequest findRequestOrThrow(String requestId) {
        return leaveRequestRepository.findById(requestId)
                .orElseThrow(() -> new ResourceNotFoundException("Leave request not found: " + requestId));
    }

    private int daysRequested(LeaveRequest leaveRequest) {
        return (int) ChronoUnit.DAYS.between(leaveRequest.getStartDate(), leaveRequest.getEndDate()) + 1;
    }

    private void deductBalance(User user, LeaveType type, int days) {
        LeaveBalance balance = user.getLeaveBalance();

        switch (type) {
            case CASUAL -> {
                if (balance.getCasualLeave() < days) {
                    throw new InsufficientLeaveBalanceException("Insufficient casual leave balance");
                }
                balance.setCasualLeave(balance.getCasualLeave() - days);
            }
            case EARNED -> {
                if (balance.getEarnedLeave() < days) {
                    throw new InsufficientLeaveBalanceException("Insufficient earned leave balance");
                }
                balance.setEarnedLeave(balance.getEarnedLeave() - days);
            }
            case SICK -> {
                if (balance.getSickLeave() < days) {
                    throw new InsufficientLeaveBalanceException("Insufficient sick leave balance");
                }
                balance.setSickLeave(balance.getSickLeave() - days);
            }
        }
    }

    private LeaveRequestResponse toResponse(LeaveRequest leaveRequest) {
        return LeaveRequestResponse.builder()
                .id(leaveRequest.getId())
                .userId(leaveRequest.getUserId())
                .type(leaveRequest.getType())
                .startDate(leaveRequest.getStartDate())
                .endDate(leaveRequest.getEndDate())
                .reason(leaveRequest.getReason())
                .days(daysRequested(leaveRequest))
                .status(leaveRequest.getStatus())
                .appliedAt(leaveRequest.getAppliedAt())
                .reviewedBy(leaveRequest.getReviewedBy())
                .reviewedAt(leaveRequest.getReviewedAt())
                .build();
    }
}