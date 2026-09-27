package com.mymobile.dto.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

import lombok.Data;
import lombok.ToString;

@Data
public class RegisterCustomerRequest {

	@NotBlank(message = "is required")
	private String name;

	@NotBlank(message = "is required")
	@Email(message = "must be a valid email address")
	private String email;

	@NotBlank(message = "is required")
	@Size(min = 8, message = "at least 8 characters at the minimum")
	@ToString.Exclude
	private String password;

	@NotBlank(message = "is required")
	private String birthday;
}
