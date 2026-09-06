package com.example.attendance_tracker_be.controller;

import com.example.attendance_tracker_be.dto.PayRateUpdateRequest;
import com.example.attendance_tracker_be.dto.UpdateProfileRequest;
import com.example.attendance_tracker_be.dto.UserProfileResponse;
import com.example.attendance_tracker_be.dto.UserSummaryResponse;
import com.example.attendance_tracker_be.service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    /**
     * The JWT subject (set in JwtService/JwtAuthFilter) is the user's Mongo id,
     * so Authentication#getName() gives us the current user's id directly —
     * no extra DB lookup by email needed here.
     */
    private String currentUserId(Authentication authentication) {
        return authentication.getName();
    }

    @GetMapping("/me")
    public ResponseEntity<UserProfileResponse> getMyProfile(Authentication authentication) {
        return ResponseEntity.ok(userService.getProfile(currentUserId(authentication)));
    }

    @PutMapping("/me")
    public ResponseEntity<UserProfileResponse> updateMyProfile(
            Authentication authentication,
            @Valid @RequestBody UpdateProfileRequest request
    ) {
        return ResponseEntity.ok(userService.updateOwnProfile(currentUserId(authentication), request));
    }

    @GetMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<UserSummaryResponse>> listAllUsers() {
        return ResponseEntity.ok(userService.listAllUsers());
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<UserProfileResponse> getUserById(@PathVariable String id) {
        return ResponseEntity.ok(userService.getUserById(id));
    }

    @PutMapping("/{id}/payrate")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<UserProfileResponse> updatePayRate(
            @PathVariable String id,
            @Valid @RequestBody PayRateUpdateRequest request
    ) {
        return ResponseEntity.ok(userService.updatePayRate(id, request));
    }
}