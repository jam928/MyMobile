package com.mymobile.dto.response;

import java.util.List;

import org.springframework.data.domain.Page;

import io.swagger.v3.oas.annotations.media.Schema;

/**
 * One page of results for the JSON API (Spring's Page has no stable JSON format).
 */
public record PageResponse<T>(
		List<T> content,
		@Schema(description = "Page number, starting at 1", example = "1") int page,
		@Schema(description = "Maximum number of items per page", example = "8") int size,
		@Schema(description = "Number of items across all pages", example = "24") long totalElements,
		@Schema(description = "Number of pages", example = "3") int totalPages) {

	public static <T> PageResponse<T> of(Page<T> page) {
		return new PageResponse<>(page.getContent(), page.getNumber() + 1, page.getSize(), page.getTotalElements(), page.getTotalPages());
	}
}
