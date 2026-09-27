package com.mymobile.dto.request;

import jakarta.validation.constraints.Size;

import lombok.Data;

@Data
public class AddPhoneLineRequest {

	// id of the selected phone
	private int pid;

	@Size(max = 20, message = "must be at most 20 characters")
	private String phoneNumber;
}
