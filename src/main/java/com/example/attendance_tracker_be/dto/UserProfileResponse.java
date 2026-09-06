package com.example.attendance_tracker_be.dto;

import com.example.attendance_tracker_be.model.PayRate;
import com.example.attendance_tracker_be.model.Role;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserProfileResponse {
    private String id;
    private String name;
    private String email;
    private Role role;
    private String company;
    private String profession;
    private String location;

    // Included on the admin single-user view; a plain employee viewing their
    // own /me doesn't strictly need this, but showing it isn't a security
    // problem (it's their own pay rate) and keeps this one DTO reusable
    // across both /users/me and /users/{id}.
    private PayRate payRate;
}