package com.mymobile.dto.request;

import java.util.Arrays;

/**
 * Sort orders for the phone list. The URL only ever selects one of these,
 * so user input is never put into the query itself.
 */
public enum PhoneSort {

	FEATURED("featured", "Featured", "p.pid"),
	PRICE_ASC("price-asc", "Price: low to high", "p.price ASC, p.pid"),
	PRICE_DESC("price-desc", "Price: high to low", "p.price DESC, p.pid"),
	TOP_RATED("rating", "Top rated", "p.rating DESC, p.price ASC, p.pid"),
	NEWEST("newest", "Newest", "p.pid DESC");

	private final String param;
	private final String label;
	private final String orderBy;

	PhoneSort(String param, String label, String orderBy) {
		this.param = param;
		this.label = label;
		this.orderBy = orderBy;
	}

	public String getParam() {
		return param;
	}

	public String getLabel() {
		return label;
	}

	// JPQL ORDER BY clause, for the phone alias "p"
	public String getOrderBy() {
		return orderBy;
	}

	// unknown or missing values fall back to FEATURED
	public static PhoneSort fromParam(String param) {
		return Arrays.stream(values())
				.filter(sort -> sort.param.equals(param))
				.findFirst()
				.orElse(FEATURED);
	}
}
