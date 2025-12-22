package com.flightapp.apigateway.service;

import com.flightapp.apigateway.dto.AuthResponse;
import com.flightapp.apigateway.dto.ChangePasswordRequest;
import com.flightapp.apigateway.dto.LoginRequest;
import com.flightapp.apigateway.dto.RegisterRequest;
import com.flightapp.apigateway.security.JwtUtil;
import com.flightapp.apigateway.model.User;
import com.flightapp.apigateway.repository.UserRepository;
import com.flightapp.apigateway.model.UserRole;
import lombok.RequiredArgsConstructor;

import java.time.Instant;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthService {
	
	@Value("${security.password.expiry-days}")
	private long passwordExpiryDays;
    private final UserRepository userRepository;
    private final JwtUtil jwtUtil;
    private final BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

    private boolean isPasswordExpired(User user, long expiryDays) {
        Instant expiryThreshold = Instant.now().minusSeconds(expiryDays * 24 * 60 * 60);
        return user.getPasswordLastChangedAt().isBefore(expiryThreshold);
    }

    public AuthResponse register(RegisterRequest request) {
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new RuntimeException("Email already registered");
        }

        UserRole role = request.getRole() != null ? request.getRole() : UserRole.ROLE_USER;

        User user = User.builder()
                .email(request.getEmail())
                .fullName(request.getFullName())
                .passwordHash(passwordEncoder.encode(request.getPassword()))
                .role(role)
                .passwordLastChangedAt(Instant.now()) 
                .build();

        userRepository.save(user);

        String token = jwtUtil.generateToken(user.getEmail(), user.getRole());
        return new AuthResponse(token, user.getEmail(), user.getRole(), false);
    }

    public AuthResponse login(LoginRequest request) {
        User user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new RuntimeException("Invalid email or password"));

        if (!passwordEncoder.matches(request.getPassword(), user.getPasswordHash())) {
            throw new RuntimeException("Invalid email or password");
        }
        boolean forceChange = isPasswordExpired(user, passwordExpiryDays);
        String token = jwtUtil.generateToken(user.getEmail(), user.getRole());
        return new AuthResponse(token, user.getEmail(), user.getRole(), forceChange);
    }
    
    public void changePassword(ChangePasswordRequest request) {

        User user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new RuntimeException("User not found"));

        if (!passwordEncoder.matches(
                request.getOldPassword(),
                user.getPasswordHash())) {
            throw new RuntimeException("Old password incorrect");
        }

        user.setPasswordHash(
                passwordEncoder.encode(request.getNewPassword())
        );

        user.setPasswordLastChangedAt(Instant.now());

        userRepository.save(user);
    }

    
//    public void changePassword(String token, ChangePasswordRequest request) {
//
//        if (!jwtUtil.isTokenValid(token)) {
//            throw new RuntimeException("Invalid token");
//        }
//
//        String email = jwtUtil.getEmail(token);
//
//        User user = userRepository.findByEmail(email)
//                .orElseThrow(() -> new RuntimeException("User not found"));
//
//        if (!passwordEncoder.matches(request.getOldPassword(), user.getPasswordHash())) {
//            throw new RuntimeException("Old password incorrect");
//        }
//
//        user.setPasswordHash(passwordEncoder.encode(request.getNewPassword()));
//        user.setPasswordLastChangedAt(Instant.now());
//
//        userRepository.save(user);
//    }

}
