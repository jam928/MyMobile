package com.mymobile.controller;

import static com.mymobile.TestData.customer;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.multipart;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.flash;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.model;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.redirectedUrl;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.view;

import java.util.List;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMultipartHttpServletRequestBuilder;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.servlet.view.InternalResourceViewResolver;

import com.mymobile.entity.Phone;
import com.mymobile.entity.Role;
import com.mymobile.mapper.CustomerMapper;
import com.mymobile.mapper.CustomerMapperImpl;
import com.mymobile.mapper.PhoneMapper;
import com.mymobile.mapper.PhoneMapperImpl;
import com.mymobile.mapper.PhonePlanMapper;
import com.mymobile.mapper.PhonePlanMapperImpl;
import com.mymobile.service.PhoneService;
import com.mymobile.service.PhotoService;
import com.mymobile.service.PhotoService.ImageType;
import com.mymobile.service.UserAdminService;

@ExtendWith(MockitoExtension.class)
class AdminControllerTest {

	static final byte[] PNG = { (byte) 0x89, 'P', 'N', 'G', 0x0D, 0x0A, 0x1A, 0x0A, 0, 0 };

	@Mock
	PhoneService phoneService;
	@Mock
	PhotoService photoService;
	@Mock
	UserAdminService userAdminService;

	@Spy
	PhoneMapper phoneMapper = new PhoneMapperImpl();
	@Spy
	CustomerMapper customerMapper = new CustomerMapperImpl();
	@Spy
	PhonePlanMapper phonePlanMapper = new PhonePlanMapperImpl();

	@InjectMocks
	AdminController controller;

	MockMvc mockMvc;

	@BeforeEach
	void setUp() {
		mockMvc = MockMvcBuilders.standaloneSetup(controller)
				.setViewResolvers(new InternalResourceViewResolver("/templates/", ".html"))
				.build();
		SignedIn.as(customer(1, Role.ADMIN));
	}

	@AfterEach
	void signOut() {
		SignedIn.clear();
	}

	// ---- add phone ----

	@Test
	void addsAPhoneWithItsUploadedPhoto() throws Exception {
		when(photoService.uploadPhoto(any(), eq(ImageType.PNG))).thenReturn("abc.png");

		mockMvc.perform(phoneForm(new MockMultipartFile("photo", "phone.png", "application/octet-stream", PNG)))
				.andExpect(redirectedUrl("/admin/phones"))
				.andExpect(flash().attribute("message", "Pixel 9 was added."));

		ArgumentCaptor<Phone> saved = ArgumentCaptor.forClass(Phone.class);
		verify(phoneService).addPhone(saved.capture());
		assertThat(saved.getValue().getImgSrc()).isEqualTo("abc.png");
		assertThat(saved.getValue().getAlt()).isEqualTo("Obsidian Pixel 9");
		assertThat(saved.getValue().getPrice()).isEqualTo(799f);
	}

	@Test
	void rejectsSvgUploads() throws Exception {
		when(phoneService.countByBrand(null)).thenReturn(List.of());
		byte[] svg = "<svg xmlns=\"http://www.w3.org/2000/svg\"><script>alert(1)</script></svg>".getBytes();

		mockMvc.perform(phoneForm(new MockMultipartFile("photo", "phone.png", "image/png", svg)))
				.andExpect(view().name("admin-phone-form"))
				.andExpect(model().attributeHasFieldErrorCode("phone", "photo", "type"));

		verify(photoService, never()).uploadPhoto(any(), any());
		verify(phoneService, never()).addPhone(any());
	}

	@Test
	void requiresAPhoto() throws Exception {
		when(phoneService.countByBrand(null)).thenReturn(List.of());

		mockMvc.perform(phoneForm(null))
				.andExpect(view().name("admin-phone-form"))
				.andExpect(model().attributeHasFieldErrorCode("phone", "photo", "required"));
	}

	@Test
	void validatesTheOtherFields() throws Exception {
		when(phoneService.countByBrand(null)).thenReturn(List.of());

		mockMvc.perform(multipart("/admin/phones")
						.file(new MockMultipartFile("photo", "phone.png", "image/png", PNG))
						.param("name", "").param("brand", "").param("color", "")
						.param("condition", "Refurbished").param("rating", "9")
						.param("price", "-1").param("quantity", "-1"))
				.andExpect(view().name("admin-phone-form"))
				.andExpect(model().attributeHasFieldErrors("phone", "name", "brand", "color", "condition", "rating", "price", "quantity"));

		verify(photoService, never()).uploadPhoto(any(), any());
	}

	@Test
	void removesTheUploadedPhotoIfSavingThePhoneFails() {
		when(photoService.uploadPhoto(any(), any())).thenReturn("abc.png");
		doThrow(new IllegalStateException("database down")).when(phoneService).addPhone(any());

		assertThatThrownBy(() -> mockMvc.perform(phoneForm(new MockMultipartFile("photo", "phone.png", "image/png", PNG))))
				.hasRootCauseMessage("database down");

		verify(photoService).deletePhoto("abc.png");
	}

	// ---- roles ----

	@Test
	void changingARoleShowsTheResult() throws Exception {
		when(userAdminService.changeRole(1, 2, Role.ADMIN)).thenReturn(customer(2, Role.ADMIN));

		mockMvc.perform(post("/admin/users/2/role").param("role", "ADMIN").param("page", "3"))
				.andExpect(redirectedUrl("/admin/users?page=3"))
				.andExpect(flash().attribute("message", "Customer 2 is now an admin. They get admin access the next time they sign in."));
	}

	@Test
	void roleChangeErrorsAreShownToTheAdmin() throws Exception {
		when(userAdminService.changeRole(1, 1, Role.CUSTOMER))
				.thenThrow(new IllegalArgumentException("You can't remove your own admin role."));

		mockMvc.perform(post("/admin/users/1/role").param("role", "CUSTOMER"))
				.andExpect(redirectedUrl("/admin/users?page=1"))
				.andExpect(flash().attribute("error", "You can't remove your own admin role."));
	}

	private static MockMultipartHttpServletRequestBuilder phoneForm(MockMultipartFile photo) {
		MockMultipartHttpServletRequestBuilder request = multipart("/admin/phones");
		if (photo != null)
			request.file(photo);
		request.param("name", "Pixel 9").param("brand", "Google").param("color", "Obsidian")
				.param("condition", "New").param("rating", "5").param("price", "799").param("quantity", "4");
		return request;
	}
}
