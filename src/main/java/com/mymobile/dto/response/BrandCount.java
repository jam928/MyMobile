package com.mymobile.dto.response;

/**
 * A brand and how many phones match it, for the brand filter.
 */
public record BrandCount(String brand, long count) {
}
