package com.example.attendance_tracker_be.dto;

import com.example.attendance_tracker_be.model.Role;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class RegisterRequest {

    @NotBlank
    private String name;

    @NotBlank
    @Email
    private String email;

    @NotBlank
    @Size(min = 8, message = "Password must be at least 8 characters")
    private String password;

    // Optional — ignored by the service on public registration (always EMPLOYEE).
    // Kept here only so the field doesn't error out if a client sends it.
    private Role role;
}