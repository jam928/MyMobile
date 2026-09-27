package com.mymobile.it;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.not;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.multipart;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.flash;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.redirectedUrl;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;

import com.mymobile.entity.Customer;
import com.mymobile.entity.Role;

class AdminIT extends IntegrationTest {

	static final byte[] PNG = { (byte) 0x89, 'P', 'N', 'G', 0x0D, 0x0A, 0x1A, 0x0A, 0, 0 };

	// ---- access ----

	@Test
	void customersCannotOpenAdminPages() throws Exception {
		Customer customer = createCustomer("cust@example.com", Role.CUSTOMER);

		mockMvc.perform(get("/admin/phones").with(signedInAs(customer))).andExpect(status().isForbidden());
		mockMvc.perform(get("/api/admin/users").with(signedInAs(customer))).andExpect(status().isForbidden());
	}

	@Test
	void apiCallsWithoutSigningInGet401() throws Exception {
		mockMvc.perform(get("/api/admin/users")).andExpect(status().isUnauthorized());
	}

	@Test
	void adminsSeeTheAdminPages() throws Exception {
		Customer admin = createCustomer("admin@example.com", Role.ADMIN);

		mockMvc.perform(get("/admin/phones").with(signedInAs(admin)))
				.andExpect(status().isOk())
				.andExpect(content().string(containsString("24 phones")));
		mockMvc.perform(get("/admin/users").with(signedInAs(admin)))
				.andExpect(status().isOk())
				.andExpect(content().string(containsString("admin@example.com")));
	}

	@Test
	void removingAdminAccessTakesEffectImmediately() throws Exception {
		Customer admin = createCustomer("admin@example.com", Role.ADMIN);
		// the signed-in session still says ADMIN, but the database no longer does
		update("UPDATE customer SET role = 'CUSTOMER' WHERE cid = ?", admin.getCid());

		mockMvc.perform(get("/admin/phones").with(signedInAs(admin))).andExpect(status().isForbidden());
		mockMvc.perform(get("/api/admin/users").with(signedInAs(admin))).andExpect(status().isForbidden());
	}

	// ---- roles (API) ----

	@Test
	void usersApiNeverReturnsPasswords() throws Exception {
		Customer admin = createCustomer("admin@example.com", Role.ADMIN);

		mockMvc.perform(get("/api/admin/users").with(signedInAs(admin)))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.content[?(@.email == 'admin@example.com')].role").value("ADMIN"))
				.andExpect(content().string(not(containsString("password"))))
				.andExpect(content().string(not(containsString("bcrypt"))));
	}

	@Test
	void adminPromotesACustomerThroughTheApi() throws Exception {
		Customer admin = createCustomer("admin@example.com", Role.ADMIN);
		Customer customer = createCustomer("cust@example.com", Role.CUSTOMER);

		mockMvc.perform(put("/api/admin/users/{cid}/role", customer.getCid()).with(signedInAs(admin)).with(csrf())
						.contentType(MediaType.APPLICATION_JSON).content("{\"role\":\"ADMIN\"}"))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.role").value("ADMIN"));

		assertThat(role(customer)).isEqualTo("ADMIN");
	}

	@Test
	void roleChangesNeedACsrfToken() throws Exception {
		Customer admin = createCustomer("admin@example.com", Role.ADMIN);
		Customer customer = createCustomer("cust@example.com", Role.CUSTOMER);

		mockMvc.perform(put("/api/admin/users/{cid}/role", customer.getCid()).with(signedInAs(admin))
						.contentType(MediaType.APPLICATION_JSON).content("{\"role\":\"ADMIN\"}"))
				.andExpect(status().isForbidden());
		assertThat(role(customer)).isEqualTo("CUSTOMER");
	}

	@Test
	void customersCannotPromoteThemselves() throws Exception {
		Customer customer = createCustomer("cust@example.com", Role.CUSTOMER);

		mockMvc.perform(put("/api/admin/users/{cid}/role", customer.getCid()).with(signedInAs(customer)).with(csrf())
						.contentType(MediaType.APPLICATION_JSON).content("{\"role\":\"ADMIN\"}"))
				.andExpect(status().isForbidden());
		assertThat(role(customer)).isEqualTo("CUSTOMER");
	}

	@Test
	void adminsCannotRemoveTheirOwnAdminRole() throws Exception {
		Customer admin = createCustomer("admin@example.com", Role.ADMIN);

		mockMvc.perform(put("/api/admin/users/{cid}/role", admin.getCid()).with(signedInAs(admin)).with(csrf())
						.contentType(MediaType.APPLICATION_JSON).content("{\"role\":\"CUSTOMER\"}"))
				.andExpect(status().isBadRequest())
				.andExpect(jsonPath("$.detail").value("You can't remove your own admin role."));
		assertThat(role(admin)).isEqualTo("ADMIN");
	}

	@Test
	void roleApiValidatesItsInput() throws Exception {
		Customer admin = createCustomer("admin@example.com", Role.ADMIN);

		mockMvc.perform(put("/api/admin/users/999999/role").with(signedInAs(admin)).with(csrf())
						.contentType(MediaType.APPLICATION_JSON).content("{\"role\":\"ADMIN\"}"))
				.andExpect(status().isNotFound());
		mockMvc.perform(put("/api/admin/users/{cid}/role", admin.getCid()).with(signedInAs(admin)).with(csrf())
						.contentType(MediaType.APPLICATION_JSON).content("{\"role\":\"SUPERUSER\"}"))
				.andExpect(status().isBadRequest());
	}

	// ---- roles (admin page) ----

	@Test
	void adminDemotesAnotherAdminFromTheUsersPage() throws Exception {
		Customer admin = createCustomer("admin@example.com", Role.ADMIN);
		Customer other = createCustomer("other-admin@example.com", Role.ADMIN);

		mockMvc.perform(post("/admin/users/{cid}/role", other.getCid()).with(signedInAs(admin)).with(csrf())
						.param("role", "CUSTOMER"))
				.andExpect(redirectedUrl("/admin/users?page=1"))
				.andExpect(flash().attribute("message", "other-admin is no longer an admin."));
		assertThat(role(other)).isEqualTo("CUSTOMER");
	}

	// ---- add phone ----

	@Test
	void adminAddsAPhone() throws Exception {
		Customer admin = createCustomer("admin@example.com", Role.ADMIN);
		when(photoService.uploadPhoto(any(), any())).thenReturn("new-phone.png");

		mockMvc.perform(multipart("/admin/phones").file(new MockMultipartFile("photo", "p.png", "image/png", PNG))
						.with(signedInAs(admin)).with(csrf())
						.param("name", " Galaxy S25 ").param("brand", "Samsung").param("color", "Navy")
						.param("condition", "New").param("rating", "5").param("price", "799.99").param("quantity", "5")
						.param("storage", "256 GB").param("camera", ""))
				.andExpect(redirectedUrl("/admin/phones"));

		mockMvc.perform(get("/api/phones").param("sort", "newest").param("size", "1"))
				.andExpect(jsonPath("$.totalElements").value(25))
				.andExpect(jsonPath("$.content[0].name").value("Galaxy S25"))
				.andExpect(jsonPath("$.content[0].imgSrc").value("new-phone.png"))
				.andExpect(jsonPath("$.content[0].storage").value("256 GB"))
				.andExpect(jsonPath("$.content[0].camera").doesNotExist());
	}

	private String role(Customer customer) {
		return query("SELECT role FROM customer WHERE cid = ?", String.class, customer.getCid());
	}
}
