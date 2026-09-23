package com.example.attendance_tracker_be.controller;

import com.example.attendance_tracker_be.dto.LeaveRequestCreateRequest;
import com.example.attendance_tracker_be.dto.LeaveRequestResponse;
import com.example.attendance_tracker_be.service.LeaveRequestService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/leave-requests")
@RequiredArgsConstructor
public class LeaveRequestController {

    private final LeaveRequestService leaveRequestService;

    @PostMapping
    public ResponseEntity<LeaveRequestResponse> applyForLeave(
            Authentication auth,
            @Valid @RequestBody LeaveRequestCreateRequest request) {
        return ResponseEntity.ok(leaveRequestService.applyForLeave(auth.getName(), request));
    }

    @GetMapping("/me")
    public ResponseEntity<List<LeaveRequestResponse>> getMyLeaveRequests(Authentication auth) {
        return ResponseEntity.ok(leaveRequestService.getMyLeaveRequests(auth.getName()));
    }

    @GetMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<LeaveRequestResponse>> getAllLeaveRequests() {
        return ResponseEntity.ok(leaveRequestService.getAllLeaveRequests());
    }

    @PutMapping("/{id}/approve")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<LeaveRequestResponse> approve(@PathVariable String id, Authentication auth) {
        return ResponseEntity.ok(leaveRequestService.approve(id, auth.getName()));
    }

    @PutMapping("/{id}/reject")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<LeaveRequestResponse> reject(@PathVariable String id, Authentication auth) {
        return ResponseEntity.ok(leaveRequestService.reject(id, auth.getName()));
    }
}