package com.example.attendance_tracker_be.service;

import com.example.attendance_tracker_be.dto.AuthResponse;
import com.example.attendance_tracker_be.dto.LoginRequest;
import com.example.attendance_tracker_be.dto.RefreshRequest;
import com.example.attendance_tracker_be.dto.RegisterRequest;
import com.example.attendance_tracker_be.exception.AuthException;
import com.example.attendance_tracker_be.model.RefreshToken;
import com.example.attendance_tracker_be.model.Role;
import com.example.attendance_tracker_be.model.User;
import com.example.attendance_tracker_be.repository.RefreshTokenRepository;
import com.example.attendance_tracker_be.repository.UserRepository;
import com.example.attendance_tracker_be.security.JwtService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final UserRepository userRepository; //dependency injection(constructor)
    private final RefreshTokenRepository refreshTokenRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;

    @Value("${app.jwt.refresh-token-expiry-days:7}")
    private long refreshTokenExpiryDays;

    public AuthResponse register(RegisterRequest request) {
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new AuthException("An account with this email already exists", HttpStatus.CONFLICT);
        }

        // Public registration is always EMPLOYEE. Promoting to ADMIN is a
        // separate, authenticated admin action — never trust a client-supplied
        // role on a self-serve register endpoint.
        User user = User.builder()
                .name(request.getName())
                .email(request.getEmail())
                .passwordHash(passwordEncoder.encode(request.getPassword()))
                .role(Role.EMPLOYEE)
                .build();

        user = userRepository.save(user);

        return buildAuthResponse(user);
    }

    public AuthResponse login(LoginRequest request) {
        User user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new AuthException("Invalid email or password", HttpStatus.UNAUTHORIZED));

        if (!passwordEncoder.matches(request.getPassword(), user.getPasswordHash())) {
            throw new AuthException("Invalid email or password", HttpStatus.UNAUTHORIZED);
        }

        return buildAuthResponse(user);
    }

    @Transactional
    public AuthResponse refresh(RefreshRequest request) {
        RefreshToken stored = refreshTokenRepository.findByToken(request.getRefreshToken())
                .orElseThrow(() -> new AuthException("Invalid refresh token", HttpStatus.UNAUTHORIZED));

        if (stored.isRevoked() || stored.getExpiresAt().isBefore(Instant.now())) {
            throw new AuthException("Refresh token expired or revoked, please log in again", HttpStatus.UNAUTHORIZED);
        }

        User user = userRepository.findById(stored.getUserId())
                .orElseThrow(() -> new AuthException("User no longer exists", HttpStatus.UNAUTHORIZED));

        // Rotate: invalidate the used token and issue a new one, so a leaked
        // refresh token can't be replayed indefinitely.
        stored.setRevoked(true);
        refreshTokenRepository.save(stored);

        String newAccessToken = jwtService.generateAccessToken(user);
        String newRefreshToken = createRefreshToken(user.getId());

        return AuthResponse.builder()
                .accessToken(newAccessToken)
                .refreshToken(newRefreshToken)
                .userId(user.getId())
                .name(user.getName())
                .email(user.getEmail())
                .role(user.getRole())
                .build();
    }

    private AuthResponse buildAuthResponse(User user) {
        String accessToken = jwtService.generateAccessToken(user);
        String refreshToken = createRefreshToken(user.getId());

        return AuthResponse.builder()
                .accessToken(accessToken)
                .refreshToken(refreshToken)
                .userId(user.getId())
                .name(user.getName())
                .email(user.getEmail())
                .role(user.getRole())
                .build();
    }

    private String createRefreshToken(String userId) {
        String token = UUID.randomUUID().toString();

        RefreshToken refreshToken = RefreshToken.builder()
                .token(token)
                .userId(userId)
                .expiresAt(Instant.now().plus(refreshTokenExpiryDays, ChronoUnit.DAYS))
                .revoked(false)
                .build();

        refreshTokenRepository.save(refreshToken);
        return token;
    }
}