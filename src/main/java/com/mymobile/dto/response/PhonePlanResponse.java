package com.mymobile.dto.response;

public record PhonePlanResponse(
		int planId,
		int numberOfLines,
		float monthlyRate,
		String description) {
}
