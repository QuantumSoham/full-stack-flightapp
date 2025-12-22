package com.flightapp.apigateway.dto;

import com.flightapp.apigateway.model.UserRole;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class AuthResponse {
    private String token;
    private String email;
    private UserRole role;
    private boolean forcePasswordChange;
}
