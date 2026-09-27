package com.mymobile.it;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.redirectedUrl;

import org.junit.jupiter.api.Test;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.mock.web.MockHttpSession;

import com.mymobile.entity.Customer;
import com.mymobile.entity.Role;

/**
 * Plans, lines, payments and password reset, end to end against MySQL.
 */
class AccountFlowIT extends IntegrationTest {

	@Test
	void planLineAndPayment() throws Exception {
		Customer customer = createCustomer("dave@example.com", Role.CUSTOMER);

		mockMvc.perform(post("/account/savePhonePlan").with(signedInAs(customer)).with(csrf()).param("planId", "2"))
				.andExpect(redirectedUrl("/account/myAccount"));
		mockMvc.perform(post("/account/saveLine").with(signedInAs(customer)).with(csrf())
						.param("pid", String.valueOf(phoneId("Google Pixel 8"))).param("phoneNumber", "5551234567"))
				.andExpect(redirectedUrl("/account/myAccount"));

		assertThat(balance(customer)).isEqualTo(70f + 10f + 699f);
		assertThat(query("SELECT phone_lines FROM customer WHERE cid = ?", Integer.class, customer.getCid())).isEqualTo(1);

		mockMvc.perform(post("/account/savePayment").with(signedInAs(customer)).with(csrf())
						.param("creditCardNumber", "4111111111111111").param("expirationDate", "2030-01")
						.param("csc", "123").param("vendor", "Visa"))
				.andExpect(redirectedUrl("/account/myAccount"));

		assertThat(balance(customer)).isZero();
		// the transaction points at the newly saved card
		Integer cardId = query("SELECT crid FROM creditcard WHERE cid = ?", Integer.class, customer.getCid());
		assertThat(query("SELECT crid FROM `transaction` WHERE cid = ?", Integer.class, customer.getCid())).isEqualTo(cardId);
		assertThat(query("SELECT amount FROM `transaction` WHERE cid = ?", Float.class, customer.getCid())).isEqualTo(779f);
	}

	@Test
	void customersCannotDeleteEachOthersLines() throws Exception {
		Customer owner = createCustomer("owner@example.com", Role.CUSTOMER);
		Customer other = createCustomer("other@example.com", Role.CUSTOMER);
		mockMvc.perform(post("/account/saveLine").with(signedInAs(owner)).with(csrf())
				.param("pid", String.valueOf(phoneId("Google Pixel 8"))).param("phoneNumber", "5550001111"));
		int lineId = query("SELECT plid FROM phonelines WHERE cid = ?", Integer.class, owner.getCid());

		mockMvc.perform(post("/account/deletePhoneLine").with(signedInAs(other)).with(csrf()).param("phoneLineId", String.valueOf(lineId)))
				.andExpect(redirectedUrl("/account/myAccount"));
		assertThat(query("SELECT COUNT(*) FROM phonelines WHERE plid = ?", Integer.class, lineId)).isEqualTo(1);
		assertThat(query("SELECT phone_lines FROM customer WHERE cid = ?", Integer.class, other.getCid())).isZero();

		mockMvc.perform(post("/account/deletePhoneLine").with(signedInAs(owner)).with(csrf()).param("phoneLineId", String.valueOf(lineId)));
		assertThat(query("SELECT COUNT(*) FROM phonelines WHERE plid = ?", Integer.class, lineId)).isZero();
		assertThat(query("SELECT phone_lines FROM customer WHERE cid = ?", Integer.class, owner.getCid())).isZero();
	}

	@Test
	void theDatabaseRejectsDuplicateEmailsInAnyCase() {
		createCustomer("erin@example.com", Role.CUSTOMER);

		assertThatThrownBy(() -> update(
				"INSERT INTO customer (name, email, password, birthday) VALUES ('Erin 2', 'ERIN@example.com', 'x', '1990-01-01')"))
				.isInstanceOf(DuplicateKeyException.class);
	}

	@Test
	void passwordResetUpdatesTheSameAccount() throws Exception {
		Customer customer = createCustomer("frank@example.com", Role.ADMIN);
		int customersBefore = query("SELECT COUNT(*) FROM customer", Integer.class);
		MockHttpSession session = new MockHttpSession();

		mockMvc.perform(post("/account/reset").session(session).with(csrf()).param("email", "frank@example.com"));
		mockMvc.perform(post("/account/updateCredentials").session(session).with(csrf())
						.param("name", "Frank").param("email", "frank@example.com")
						.param("password", "brand-new-pass").param("birthday", "1990-01-01"))
				.andExpect(redirectedUrl("/account/login"));

		assertThat(query("SELECT COUNT(*) FROM customer", Integer.class)).isEqualTo(customersBefore);
		String stored = query("SELECT password FROM customer WHERE cid = ?", String.class, customer.getCid());
		assertThat(passwordEncoder.matches("brand-new-pass", stored)).isTrue();
		assertThat(query("SELECT role FROM customer WHERE cid = ?", String.class, customer.getCid())).isEqualTo("ADMIN");
	}

	private float balance(Customer customer) {
		return query("SELECT balance FROM customer WHERE cid = ?", Float.class, customer.getCid());
	}
}
