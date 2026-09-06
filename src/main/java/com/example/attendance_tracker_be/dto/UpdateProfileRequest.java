package com.example.attendance_tracker_be.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

/**
 * Intentionally excludes email, password, and role — a user updating their
 * own profile should never be able to change what they log in with or
 * grant themselves admin. Those need separate, more guarded flows later.
 */
@Data
public class UpdateProfileRequest {

    @NotBlank
    private String name;

    private String company;
    private String profession;
    private String location;
}