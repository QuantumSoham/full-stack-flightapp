package com.flightapp.apigateway.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

@Data
@Getter
@Setter
public class ChangePasswordRequest {

	@Email
	private String email;
	@NotBlank
	private String oldPassword;
	@NotBlank
	private String newPassword;
}
