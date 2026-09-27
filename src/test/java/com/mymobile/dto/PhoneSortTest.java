package com.mymobile.dto;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullAndEmptySource;
import org.junit.jupiter.params.provider.ValueSource;

import com.mymobile.dto.request.PhoneSort;

class PhoneSortTest {

	@Test
	void parsesEveryUrlValue() {
		for (PhoneSort sort : PhoneSort.values())
			assertThat(PhoneSort.fromParam(sort.getParam())).isEqualTo(sort);
	}

	@ParameterizedTest
	@NullAndEmptySource
	@ValueSource(strings = { "PRICE-ASC", "p.price; DROP TABLE phones", "cheapest" })
	void unknownValuesFallBackToFeatured(String param) {
		assertThat(PhoneSort.fromParam(param)).isEqualTo(PhoneSort.FEATURED);
	}

	@Test
	void everyOrderEndsWithTheIdForStablePaging() {
		for (PhoneSort sort : PhoneSort.values())
			assertThat(sort.getOrderBy()).containsPattern("p\\.pid( DESC)?$");
	}
}
