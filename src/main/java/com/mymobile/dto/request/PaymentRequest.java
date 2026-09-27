package com.mymobile.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import lombok.Data;
import lombok.ToString;

@Data
public class PaymentRequest {

	@NotNull
	@Size(min = 8, max = 16)
	@ToString.Exclude
	private String creditCardNumber;

	@NotBlank(message = "is required")
	private String expirationDate;

	@NotNull(message = "is required")
	@ToString.Exclude
	private Integer csc;

	private String vendor;
}
