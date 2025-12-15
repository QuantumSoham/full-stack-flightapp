package com.flightapp.booking.service;

import com.flightapp.booking.dto.request.UserRegisterRequest;
import com.flightapp.booking.dto.response.UserResponse;
import com.flightapp.booking.entity.UserAccount;
import com.flightapp.booking.exception.ResourceNotFoundException;
import com.flightapp.booking.repository.UserAccountRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Base64;

@Service
@Slf4j
@RequiredArgsConstructor
public class UserAccountService {

    private final UserAccountRepository userAccountRepository;

    @Transactional //this means either execute or rollback
    public UserResponse registerUser(UserRegisterRequest request) {
        log.info("Registering new user with email: {}", request.getEmail());

        if (userAccountRepository.existsByEmail(request.getEmail())) {
            throw new IllegalArgumentException("User with email " + request.getEmail() + " already exists");
        }

        UserAccount user = UserAccount.builder()
                .name(request.getName())
                .email(request.getEmail())
                .passwordHash(hashPassword(request.getPassword()))
                .build();

        UserAccount savedUser = userAccountRepository.save(user);
        log.info("User registered successfully with ID: {}", savedUser.getId());

        return convertToResponse(savedUser);
    }

    @Transactional(readOnly = true)
    public UserResponse getUserByEmail(String email) {
        UserAccount user = userAccountRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with email: " + email));
        return convertToResponse(user);
    }

    @Transactional(readOnly = true)
    public UserResponse getUserById(Long id) {
        UserAccount user = userAccountRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with ID: " + id));
        return convertToResponse(user);
    }

    public boolean validatePassword(String rawPassword, String hashedPassword) {
        return hashPassword(rawPassword).equals(hashedPassword);
    }

    private String hashPassword(String password) {
        try {
            MessageDigest md = MessageDigest.getInstance("SHA-256");
            byte[] hashedBytes = md.digest(password.getBytes());
            return Base64.getEncoder().encodeToString(hashedBytes);
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException("Password hashing failed", e);
        }
    }

    private UserResponse convertToResponse(UserAccount user) {
        return UserResponse.builder()
                .id(user.getId())
                .name(user.getName())
                .email(user.getEmail())
                .createdAt(user.getCreatedAt())
                .build();
    }
}
