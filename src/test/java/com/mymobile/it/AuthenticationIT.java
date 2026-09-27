package com.mymobile.it;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestBuilders.formLogin;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestBuilders.logout;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.security.test.web.servlet.response.SecurityMockMvcResultMatchers.authenticated;
import static org.springframework.security.test.web.servlet.response.SecurityMockMvcResultMatchers.unauthenticated;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.redirectedUrl;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import com.mymobile.entity.Role;

class AuthenticationIT extends IntegrationTest {

	@Test
	void registrationStoresABcryptHash() throws Exception {
		mockMvc.perform(post("/account/addCustomer").with(csrf())
						.param("name", "Ann").param("email", "ann@example.com")
						.param("password", PASSWORD).param("birthday", "1990-01-01"))
				.andExpect(redirectedUrl("/account/login"));

		String stored = query("SELECT password FROM customer WHERE email = 'ann@example.com'", String.class);
		assertThat(stored).startsWith("{bcrypt}").isNotEqualTo(PASSWORD);
		assertThat(passwordEncoder.matches(PASSWORD, stored)).isTrue();
		assertThat(query("SELECT role FROM customer WHERE email = 'ann@example.com'", String.class)).isEqualTo("CUSTOMER");
	}

	@Test
	void formsWithoutACsrfTokenAreRejected() throws Exception {
		mockMvc.perform(post("/account/addCustomer")
						.param("name", "Ann").param("email", "ann@example.com")
						.param("password", PASSWORD).param("birthday", "1990-01-01"))
				.andExpect(status().isForbidden());

		assertThat(query("SELECT COUNT(*) FROM customer WHERE email = 'ann@example.com'", Integer.class)).isZero();
	}

	@Test
	void signInWithTheRightPassword() throws Exception {
		createCustomer("bob@example.com", Role.CUSTOMER);

		mockMvc.perform(formLogin("/account/loggedIn").userParameter("email").user("BOB@example.com").password(PASSWORD))
				.andExpect(authenticated().withUsername("bob@example.com"))
				.andExpect(redirectedUrl("/account/myAccount"));
	}

	@Test
	void signInWithTheWrongPasswordFails() throws Exception {
		createCustomer("bob@example.com", Role.CUSTOMER);

		mockMvc.perform(formLogin("/account/loggedIn").userParameter("email").user("bob@example.com").password("wrong-password"))
				.andExpect(unauthenticated())
				.andExpect(redirectedUrl("/account/login?error"));
	}

	@Test
	void signInNeedsACsrfToken() throws Exception {
		createCustomer("bob@example.com", Role.CUSTOMER);

		mockMvc.perform(post("/account/loggedIn").param("email", "bob@example.com").param("password", PASSWORD))
				.andExpect(status().isForbidden())
				.andExpect(unauthenticated());
	}

	@ParameterizedTest
	@ValueSource(strings = { "/account/myAccount", "/account/pay", "/account/selectPhonePlan", "/account/addALine", "/admin/phones" })
	void privatePagesSendVisitorsToSignIn(String page) throws Exception {
		mockMvc.perform(get(page))
				.andExpect(redirectedUrl("/account/login"));
	}

	@ParameterizedTest
	@ValueSource(strings = { "/main/list", "/main/phones/1", "/account/login", "/account/register", "/account/forgotPW", "/api/phones", "/v3/api-docs" })
	void publicPagesAreOpen(String page) throws Exception {
		mockMvc.perform(get(page)).andExpect(status().isOk());
	}

	@Test
	void signOut() throws Exception {
		mockMvc.perform(logout("/account/logout"))
				.andExpect(unauthenticated())
				.andExpect(redirectedUrl("/account/login?logout"));
	}

	@Test
	void signedInPagesGreetTheCustomer() throws Exception {
		mockMvc.perform(get("/account/myAccount").with(signedInAs(createCustomer("carol@example.com", Role.CUSTOMER))))
				.andExpect(status().isOk())
				.andExpect(content().string(org.hamcrest.Matchers.containsString("Hi, carol")));
	}
}
