package com.mymobile.dto.request;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.PositiveOrZero;
import jakarta.validation.constraints.Size;

import org.springframework.web.multipart.MultipartFile;

import lombok.Data;
import lombok.ToString;

@Data
public class CreatePhoneRequest {

	@NotBlank(message = "is required")
	@Size(max = 100)
	private String name;

	@NotBlank(message = "is required")
	@Size(max = 50)
	private String brand;

	@NotBlank(message = "is required")
	@Pattern(regexp = "New|Used", message = "must be New or Used")
	private String condition = "New";

	@NotBlank(message = "is required")
	@Size(max = 50)
	private String color;

	@NotNull(message = "is required")
	@Positive
	private Float price;

	@NotNull(message = "is required")
	@Min(1)
	@Max(5)
	private Integer rating = 5;

	@NotNull(message = "is required")
	@PositiveOrZero
	private Integer quantity;

	@Size(max = 1000)
	private String description;

	@Size(max = 50)
	private String storage;

	@Size(max = 100)
	private String screen;

	@Size(max = 100)
	private String camera;

	@Size(max = 50)
	private String battery;

	// checked and uploaded to MinIO by the controller
	@ToString.Exclude
	private MultipartFile photo;
}
