package com.mymobile.api;

import static com.mymobile.TestData.phone;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import com.mymobile.dto.request.PhoneSort;
import com.mymobile.dto.response.BrandCount;
import com.mymobile.mapper.PhoneMapper;
import com.mymobile.mapper.PhoneMapperImpl;
import com.mymobile.service.PhoneService;

@ExtendWith(MockitoExtension.class)
class PhoneApiControllerTest {

	@Mock
	PhoneService phoneService;

	@Spy
	PhoneMapper phoneMapper = new PhoneMapperImpl();

	@InjectMocks
	PhoneApiController controller;

	MockMvc mockMvc;

	@BeforeEach
	void setUp() {
		mockMvc = MockMvcBuilders.standaloneSetup(controller)
				.setControllerAdvice(new ApiExceptionHandler())
				.build();
	}

	@Test
	void listsAPageOfPhones() throws Exception {
		when(phoneService.searchPhones("pixel", "Google", PhoneSort.PRICE_DESC, PageRequest.of(1, 2)))
				.thenReturn(new PageImpl<>(List.of(phone(3, "Pixel 8", 699f)), PageRequest.of(1, 2), 3));

		mockMvc.perform(get("/api/phones")
						.param("q", " pixel ").param("brand", "Google").param("sort", "price-desc")
						.param("page", "2").param("size", "2"))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.content[0].name").value("Pixel 8"))
				.andExpect(jsonPath("$.content[0].price").value(699.0))
				.andExpect(jsonPath("$.page").value(2))
				.andExpect(jsonPath("$.size").value(2))
				.andExpect(jsonPath("$.totalElements").value(3))
				.andExpect(jsonPath("$.totalPages").value(2));
	}

	@Test
	void unknownPhoneIsA404Problem() throws Exception {
		mockMvc.perform(get("/api/phones/99"))
				.andExpect(status().isNotFound())
				.andExpect(jsonPath("$.status").value(404))
				.andExpect(jsonPath("$.detail").value("No phone with id 99"));
	}

	@Test
	void getsOnePhone() throws Exception {
		when(phoneService.getPhone(3)).thenReturn(phone(3, "Pixel 8", 699f));

		mockMvc.perform(get("/api/phones/3"))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.pid").value(3))
				.andExpect(jsonPath("$.brand").value("Google"));
	}

	@Test
	void listsBrandCountsForASearch() throws Exception {
		when(phoneService.countByBrand("blue")).thenReturn(List.of(new BrandCount("Apple", 1)));

		mockMvc.perform(get("/api/phones/brands").param("q", "blue"))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$[0].brand").value("Apple"))
				.andExpect(jsonPath("$[0].count").value(1));
		verify(phoneService).countByBrand("blue");
	}
}
