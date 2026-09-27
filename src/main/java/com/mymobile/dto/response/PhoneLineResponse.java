package com.mymobile.dto.response;

public record PhoneLineResponse(
		int plid,
		int pid,
		String phoneNumber,
		String phoneName,
		String imgSrc,
		String alt,
		String color) {
}
