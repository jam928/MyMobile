package com.mymobile.it;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.containsString;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.redirectedUrl;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.List;

import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;

import com.jayway.jsonpath.JsonPath;

/**
 * Search, brand filter, sorting and paging against the real database.
 */
class CatalogIT extends IntegrationTest {

	@Test
	void searchMatchesNameBrandAndColor() throws Exception {
		mockMvc.perform(get("/api/phones").param("q", "PIXEL"))
				.andExpect(jsonPath("$.totalElements").value(4));
		mockMvc.perform(get("/api/phones").param("q", "blue"))
				.andExpect(jsonPath("$.totalElements").value(3));
	}

	@Test
	void likeWildcardsAreSearchedLiterally() throws Exception {
		mockMvc.perform(get("/api/phones").param("q", "%"))
				.andExpect(jsonPath("$.totalElements").value(0));
		mockMvc.perform(get("/api/phones").param("q", "_"))
				.andExpect(jsonPath("$.totalElements").value(0));
	}

	@Test
	void sortsByPriceWithinABrand() throws Exception {
		String json = mockMvc.perform(get("/api/phones").param("brand", "Apple").param("sort", "price-desc").param("size", "100"))
				.andReturn().getResponse().getContentAsString();

		List<Double> prices = JsonPath.read(json, "$.content[*].price");
		assertThat(prices).hasSize(7).isSortedAccordingTo((a, b) -> Double.compare(b, a));
		assertThat(JsonPath.<List<String>>read(json, "$.content[*].brand")).containsOnly("Apple");
	}

	@Test
	void topRatedPutsFiveStarsFirstAndCheapestFirstWithin() throws Exception {
		String json = mockMvc.perform(get("/api/phones").param("sort", "rating").param("size", "100"))
				.andReturn().getResponse().getContentAsString();

		List<Integer> ratings = JsonPath.read(json, "$.content[*].rating");
		assertThat(ratings).isSortedAccordingTo((a, b) -> Integer.compare(b, a));
	}

	@Test
	void pagesThroughTheCatalog() throws Exception {
		mockMvc.perform(get("/api/phones").param("page", "3").param("size", "8"))
				.andExpect(jsonPath("$.content.length()").value(8))
				.andExpect(jsonPath("$.page").value(3))
				.andExpect(jsonPath("$.totalPages").value(3))
				.andExpect(jsonPath("$.totalElements").value(24));
	}

	@Test
	void invalidPagingIsRejected() throws Exception {
		mockMvc.perform(get("/api/phones").param("size", "500"))
				.andExpect(status().isBadRequest())
				.andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_PROBLEM_JSON));
	}

	@Test
	void htmlListShowsFiltersAndPageLinks() throws Exception {
		mockMvc.perform(get("/main/list").param("q", "e").param("sort", "price-asc"))
				.andExpect(status().isOk())
				.andExpect(content().string(containsString("Showing 1–8 of 19 phones")))
				.andExpect(content().string(containsString("/main/list?q=e&amp;sort=price-asc&amp;page=2")));
	}

	@Test
	void pastTheLastPageRedirectsToTheLastPage() throws Exception {
		mockMvc.perform(get("/main/list").param("brand", "Apple").param("page", "9"))
				.andExpect(redirectedUrl("/main/list?brand=Apple&page=1"));
	}

	@Test
	void phoneDetails() throws Exception {
		mockMvc.perform(get("/main/phones/{pid}", phoneId("Samsung Galaxy S24 Ultra")))
				.andExpect(status().isOk())
				.andExpect(content().string(containsString("200 MP")));
		mockMvc.perform(get("/api/phones/999999"))
				.andExpect(status().isNotFound())
				.andExpect(jsonPath("$.detail").value("No phone with id 999999"));
	}
}
