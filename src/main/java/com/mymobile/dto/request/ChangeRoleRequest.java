package com.mymobile.dto.request;

import jakarta.validation.constraints.NotNull;

import com.mymobile.entity.Role;

import io.swagger.v3.oas.annotations.media.Schema;

public record ChangeRoleRequest(
		@NotNull @Schema(description = "The new role", example = "ADMIN") Role role) {
}
