package com.example.attendance_tracker_be.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Document(collection = "users")
public class User {

    @Id
    private String id;

    private String name;

    @Indexed(unique = true)
    private String email;

    private String passwordHash;

    @Builder.Default
    private Role role = Role.EMPLOYEE;

    // Profile fields (feature #2) — optional at registration, filled in later
    private String company;
    private String profession;
    private String location;

    // Feature #13 — single base rate to start; expands to full PayRate object
    // (feature #18) once Phase 2 adds overtime/weekend/holiday differentiation.
    @Builder.Default
    private PayRate payRate = PayRate.builder().build();

    // Feature #11 — live status shown on admin dashboard (online/working/break/unavailable)
    @Builder.Default
    private UserStatus status = UserStatus.UNAVAILABLE;

    // alongside payRate
    @Builder.Default
    private LeaveBalance leaveBalance = LeaveBalance.builder().build();

}