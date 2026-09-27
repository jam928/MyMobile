package com.mymobile.api;

import java.util.List;
import java.util.NoSuchElementException;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;

import org.springframework.data.domain.PageRequest;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.mymobile.dto.request.PhoneSort;
import com.mymobile.dto.response.BrandCount;
import com.mymobile.dto.response.PageResponse;
import com.mymobile.dto.response.PhoneResponse;
import com.mymobile.entity.Phone;
import com.mymobile.mapper.PhoneMapper;
import com.mymobile.service.PhoneService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/phones")
@RequiredArgsConstructor
@Tag(name = "Phones", description = "The phone catalog (public, read-only)")
public class PhoneApiController {

	private final PhoneService phoneService;
	private final PhoneMapper phoneMapper;

	@GetMapping
	@Operation(summary = "List phones", description = "Search, filter by brand, sort and page through the phones.")
	public PageResponse<PhoneResponse> listPhones(
			@Parameter(description = "Matches the name, brand or color (case-insensitive)", example = "pixel")
			@RequestParam(name = "q", required = false) String q,
			@Parameter(description = "Only phones from this brand", example = "Google")
			@RequestParam(name = "brand", required = false) String brand,
			@Parameter(description = "Sort order", schema = @Schema(allowableValues = { "featured", "price-asc", "price-desc", "rating", "newest" }))
			@RequestParam(name = "sort", defaultValue = "featured") String sort,
			@Parameter(description = "Page number, starting at 1") @RequestParam(name = "page", defaultValue = "1") @Min(1) int page,
			@Parameter(description = "Phones per page (1-100)") @RequestParam(name = "size", defaultValue = "8") @Min(1) @Max(100) int size)
	{
		String search = StringUtils.hasText(q) ? q.trim() : null;
		String brandFilter = StringUtils.hasText(brand) ? brand : null;
		return PageResponse.of(phoneService
				.searchPhones(search, brandFilter, PhoneSort.fromParam(sort), PageRequest.of(page - 1, size))
				.map(phoneMapper::toResponse));
	}

	@GetMapping("/{pid}")
	@Operation(summary = "Get a phone")
	@ApiResponse(responseCode = "200", description = "The phone")
	@ApiResponse(responseCode = "404", description = "No phone with that id")
	public PhoneResponse getPhone(@PathVariable int pid)
	{
		Phone phone = phoneService.getPhone(pid);
		if(phone == null)
			throw new NoSuchElementException("No phone with id " + pid);
		return phoneMapper.toResponse(phone);
	}

	@GetMapping("/brands")
	@Operation(summary = "List brands", description = "Each brand with the number of phones matching the (optional) search.")
	public List<BrandCount> listBrands(
			@Parameter(description = "Only count phones matching this search", example = "blue")
			@RequestParam(name = "q", required = false) String q)
	{
		return phoneService.countByBrand(StringUtils.hasText(q) ? q.trim() : null);
	}
}
