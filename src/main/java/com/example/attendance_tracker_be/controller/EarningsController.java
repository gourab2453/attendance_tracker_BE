package com.example.attendance_tracker_be.controller;

import com.example.attendance_tracker_be.dto.LiveEarningsResponse;
import com.example.attendance_tracker_be.dto.PayPeriodResponse;
import com.example.attendance_tracker_be.dto.PaycheckPreviewResponse;
import com.example.attendance_tracker_be.service.EarningsService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/earnings")
@RequiredArgsConstructor
public class EarningsController {

    private final EarningsService earningsService;

    @GetMapping("/live")
    public ResponseEntity<LiveEarningsResponse> getLiveEarnings(Authentication auth) {
        return ResponseEntity.ok(earningsService.getLiveEarnings(auth.getName()));
    }

    @GetMapping("/paycheck-preview")
    public ResponseEntity<PaycheckPreviewResponse> getPaycheckPreview(
            Authentication auth,
            @RequestParam String period) {
        return ResponseEntity.ok(earningsService.getPaycheckPreview(auth.getName(), period));
    }

    @GetMapping("/pay-period")
    public ResponseEntity<PayPeriodResponse> getPayPeriod(@RequestParam String type) {
        return ResponseEntity.ok(earningsService.getPayPeriod(type));
    }
}
