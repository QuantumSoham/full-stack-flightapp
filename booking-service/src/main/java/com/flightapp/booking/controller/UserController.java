package com.flightapp.booking.controller;

import com.flightapp.booking.dto.request.UserRegisterRequest;
import com.flightapp.booking.dto.response.ApiResponse;
import com.flightapp.booking.dto.response.UserResponse;
import com.flightapp.booking.service.UserAccountService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.bind.annotation.RequestMethod;

@RestController
@RequestMapping("/api/v1.0/user")
@RequiredArgsConstructor
@Slf4j
@CrossOrigin(origins = "*", allowedHeaders = "*", methods = {RequestMethod.GET, RequestMethod.POST, RequestMethod.DELETE, RequestMethod.PUT})
public class UserController {

    private final UserAccountService userAccountService;
    
    //a test end point for me to debug 
    @GetMapping("/test123")
    public void testendpoint()
    {
    	System.out.println("Test Hit");
    }

    //register a user for booking
    @PostMapping("/register")
    public ResponseEntity<ApiResponse<UserResponse>> registerUser(@Valid @RequestBody UserRegisterRequest request) {
        System.out.println("Controller hit");
    	try {
            log.info("Received request to register user with email: {}", request.getEmail());
            UserResponse user = userAccountService.registerUser(request);
            return ResponseEntity.status(HttpStatus.CREATED)
                    .body(ApiResponse.success("User registered successfully", user));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.CONFLICT)
                    .body(ApiResponse.error(e.getMessage()));
        } catch (Exception e) {
            log.error("Error registering user", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponse.error("Error registering user: " + e.getMessage()));
        }
    }

    //get user by email id 
    @GetMapping("/{email}")
    public ResponseEntity<ApiResponse<UserResponse>> getUser(@PathVariable String email) {
        try {
            UserResponse user = userAccountService.getUserByEmail(email);
            return ResponseEntity.ok(ApiResponse.success("User fetched successfully", user));
        } catch (Exception e) {
            log.error("Error fetching user", e);
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(ApiResponse.error(e.getMessage()));
        }
    }
}
