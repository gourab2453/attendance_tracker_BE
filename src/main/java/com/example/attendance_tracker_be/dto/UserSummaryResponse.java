package com.example.attendance_tracker_be.dto;

import com.example.attendance_tracker_be.model.Role;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserSummaryResponse {
    private String id;
    private String name;
    private String email;
    private Role role;
    private String company;
    private String profession;
}
