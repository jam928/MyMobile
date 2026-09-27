package com.mymobile.dto.response;

import com.mymobile.entity.Role;

public record CustomerResponse(
		int cid,
		String name,
		String email,
		String birthday,
		int phoneLines,
		float balance,
		int planId,
		Role role) {
}
