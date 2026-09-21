package com.example.attendance_tracker_be.service;

import com.example.attendance_tracker_be.dto.PayRateUpdateRequest;
import com.example.attendance_tracker_be.dto.UpdateProfileRequest;
import com.example.attendance_tracker_be.dto.UserProfileResponse;
import com.example.attendance_tracker_be.dto.UserSummaryResponse;
import com.example.attendance_tracker_be.exception.InvalidCompanyException;
import com.example.attendance_tracker_be.exception.ResourceNotFoundException;
import com.example.attendance_tracker_be.model.PayRate;
import com.example.attendance_tracker_be.model.User;
import com.example.attendance_tracker_be.repository.ListedCompanyRepository;
import com.example.attendance_tracker_be.repository.UserRepository;
import com.example.attendance_tracker_be.dto.LeaveBalanceUpdateRequest;
import com.example.attendance_tracker_be.model.LeaveBalance;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final ListedCompanyRepository listedCompanyRepository;

    public UserProfileResponse getProfile(String userId) {
        User user = findUserOrThrow(userId);
        return toProfileResponse(user);
    }

    public UserProfileResponse updateOwnProfile(String userId, UpdateProfileRequest request) {
        User user = findUserOrThrow(userId);

        if (request.getCompany() != null &&
                !listedCompanyRepository.existsByCompanyName(request.getCompany())) {
            throw new InvalidCompanyException(
                    "Company '" + request.getCompany() + "' is not a listed company");
        }

        user.setName(request.getName());
        user.setCompany(request.getCompany());
        user.setProfession(request.getProfession());
        user.setLocation(request.getLocation());

        user = userRepository.save(user);
        return toProfileResponse(user);
    }

    public List<UserSummaryResponse> listAllUsers() {
        return userRepository.findAll().stream()
                .map(this::toSummaryResponse)
                .toList();
    }

    public UserProfileResponse getUserById(String userId) {
        User user = findUserOrThrow(userId);
        return toProfileResponse(user);
    }

    public UserProfileResponse updatePayRate(String userId, PayRateUpdateRequest request) {
        User user = findUserOrThrow(userId);

        PayRate payRate = PayRate.builder()
                .regular(request.getRegular())
                .overtime(request.getOvertime())
                .weekend(request.getWeekend())
                .holiday(request.getHoliday())
                .build();

        user.setPayRate(payRate);
        user = userRepository.save(user);
        return toProfileResponse(user);
    }

    public UserProfileResponse getLeaveBalance(String userId) {
        User user = findUserOrThrow(userId);
        return toProfileResponse(user);
    }

    public UserProfileResponse updateLeaveBalance(String userId, LeaveBalanceUpdateRequest request) {
        User user = findUserOrThrow(userId);

        LeaveBalance leaveBalance = LeaveBalance.builder()
                .casualLeave(request.getCasualLeave())
                .earnedLeave(request.getEarnedLeave())
                .sickLeave(request.getSickLeave())
                .build();

        user.setLeaveBalance(leaveBalance);
        user = userRepository.save(user);
        return toProfileResponse(user);
    }

    private User findUserOrThrow(String userId) {
        return userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found: " + userId));
    }

    private UserProfileResponse toProfileResponse(User user) {
        return UserProfileResponse.builder()
                .id(user.getId())
                .name(user.getName())
                .email(user.getEmail())
                .role(user.getRole())
                .company(user.getCompany())
                .profession(user.getProfession())
                .location(user.getLocation())
                .payRate(user.getPayRate())
                .leaveBalance(user.getLeaveBalance())
                .build();
    }

    private UserSummaryResponse toSummaryResponse(User user) {
        return UserSummaryResponse.builder()
                .id(user.getId())
                .name(user.getName())
                .email(user.getEmail())
                .role(user.getRole())
                .company(user.getCompany())
                .profession(user.getProfession())
                .build();
    }
}