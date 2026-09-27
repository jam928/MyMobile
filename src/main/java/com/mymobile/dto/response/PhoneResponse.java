package com.mymobile.dto.response;

public record PhoneResponse(
		int pid,
		String condition,
		String name,
		String brand,
		int rating,
		Float price,
		String color,
		String imgSrc,
		String alt,
		int quantity,
		String description,
		String storage,
		String screen,
		String camera,
		String battery) {
}
