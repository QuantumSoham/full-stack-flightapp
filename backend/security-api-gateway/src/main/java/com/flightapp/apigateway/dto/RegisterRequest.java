package com.flightapp.apigateway.dto;

import com.flightapp.apigateway.model.UserRole;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class RegisterRequest {

    @NotBlank
    @Email
    private String email;

    @NotBlank
    private String password;

    @NotBlank
    private String fullName;

    // optional; if null, default USER
    private UserRole role;
}
