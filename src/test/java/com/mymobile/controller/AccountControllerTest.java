package com.mymobile.controller;

import static com.mymobile.TestData.customer;
import static com.mymobile.TestData.phone;
import static com.mymobile.TestData.plan;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
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
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.servlet.view.InternalResourceViewResolver;

import com.mymobile.entity.CreditCard;
import com.mymobile.entity.Customer;
import com.mymobile.entity.PhoneLine;
import com.mymobile.entity.Role;
import com.mymobile.entity.Transaction;
import com.mymobile.mapper.CreditCardMapper;
import com.mymobile.mapper.CreditCardMapperImpl;
import com.mymobile.mapper.CustomerMapper;
import com.mymobile.mapper.CustomerMapperImpl;
import com.mymobile.mapper.PhoneLineMapper;
import com.mymobile.mapper.PhoneLineMapperImpl;
import com.mymobile.mapper.PhoneMapper;
import com.mymobile.mapper.PhoneMapperImpl;
import com.mymobile.mapper.PhonePlanMapper;
import com.mymobile.mapper.PhonePlanMapperImpl;
import com.mymobile.service.PhoneService;

@ExtendWith(MockitoExtension.class)
class AccountControllerTest {

	@Mock
	PhoneService phoneService;

	@Mock
	PasswordEncoder passwordEncoder;

	@Spy
	CustomerMapper customerMapper = new CustomerMapperImpl();
	@Spy
	PhoneMapper phoneMapper = new PhoneMapperImpl();
	@Spy
	PhoneLineMapper phoneLineMapper = new PhoneLineMapperImpl();
	@Spy
	PhonePlanMapper phonePlanMapper = new PhonePlanMapperImpl();
	@Spy
	CreditCardMapper creditCardMapper = new CreditCardMapperImpl();

	@InjectMocks
	AccountController controller;

	MockMvc mockMvc;

	@BeforeEach
	void setUp() {
		mockMvc = MockMvcBuilders.standaloneSetup(controller)
				.setViewResolvers(new InternalResourceViewResolver("/templates/", ".html"))
				.build();
	}

	@AfterEach
	void signOut() {
		SignedIn.clear();
	}

	// ---- registration ----

	@Test
	void registrationStoresAHashedPassword() throws Exception {
		when(passwordEncoder.encode("password123")).thenReturn("{bcrypt}hashed");

		mockMvc.perform(post("/account/addCustomer")
						.param("name", "Ann").param("email", "ann@example.com")
						.param("password", "password123").param("birthday", "1990-01-01"))
				.andExpect(redirectedUrl("/account/login"))
				.andExpect(flash().attributeExists("message"));

		ArgumentCaptor<Customer> saved = ArgumentCaptor.forClass(Customer.class);
		verify(phoneService).addCustomer(saved.capture());
		assertThat(saved.getValue().getPassword()).isEqualTo("{bcrypt}hashed");
		assertThat(saved.getValue().getRole()).isEqualTo(Role.CUSTOMER);
	}

	@Test
	void registrationCannotMakeSomeoneAnAdmin() throws Exception {
		when(passwordEncoder.encode(anyString())).thenReturn("{bcrypt}hashed");

		mockMvc.perform(post("/account/addCustomer")
						.param("name", "Eve").param("email", "eve@example.com")
						.param("password", "password123").param("birthday", "1990-01-01")
						.param("role", "ADMIN"))
				.andExpect(redirectedUrl("/account/login"));

		ArgumentCaptor<Customer> saved = ArgumentCaptor.forClass(Customer.class);
		verify(phoneService).addCustomer(saved.capture());
		assertThat(saved.getValue().getRole()).isEqualTo(Role.CUSTOMER);
	}

	@Test
	void registrationValidatesTheForm() throws Exception {
		mockMvc.perform(post("/account/addCustomer")
						.param("name", " ").param("email", "not-an-email")
						.param("password", "short").param("birthday", ""))
				.andExpect(view().name("register"))
				.andExpect(model().attributeHasFieldErrors("customer", "name", "email", "password", "birthday"));

		verify(phoneService, never()).addCustomer(any());
	}

	@Test
	void registrationRejectsAnEmailThatIsAlreadyUsed() throws Exception {
		when(phoneService.isEmailTaken("ann@example.com", 0)).thenReturn(true);

		mockMvc.perform(post("/account/addCustomer")
						.param("name", "Ann").param("email", "ann@example.com")
						.param("password", "password123").param("birthday", "1990-01-01"))
				.andExpect(view().name("register"))
				.andExpect(model().attributeHasFieldErrorCode("customer", "email", "duplicate"));

		verify(phoneService, never()).addCustomer(any());
	}

	@Test
	void registrationHandlesTheDatabaseRejectingADuplicateEmail() throws Exception {
		when(passwordEncoder.encode(anyString())).thenReturn("{bcrypt}hashed");
		doThrow(new DataIntegrityViolationException("Duplicate entry")).when(phoneService).addCustomer(any());

		mockMvc.perform(post("/account/addCustomer")
						.param("name", "Ann").param("email", "ann@example.com")
						.param("password", "password123").param("birthday", "1990-01-01"))
				.andExpect(view().name("register"))
				.andExpect(model().attributeHasFieldErrorCode("customer", "email", "duplicate"));
	}

	// ---- account pages ----

	@Test
	void accountPageNeedsASignedInCustomer() throws Exception {
		mockMvc.perform(get("/account/myAccount"))
				.andExpect(redirectedUrl("/account/login"));
	}

	@Test
	void accountPageShowsTheCustomersDetails() throws Exception {
		Customer customer = signedInCustomer(40f);
		customer.setPlanId(1);
		when(phoneService.getPhonePlan(customer)).thenReturn(plan(1, 2, 70f));
		when(phoneService.getPhoneLines(customer)).thenReturn(List.of());

		mockMvc.perform(get("/account/myAccount"))
				.andExpect(view().name("account"))
				.andExpect(model().attributeExists("currentCustomer", "phoneLines", "phonePlan"));
	}

	// ---- lines ----

	@Test
	void addingALineChargesTheFeeAndPhonePrice() throws Exception {
		Customer customer = signedInCustomer(40f);
		when(phoneService.getPhone(3)).thenReturn(phone(3, "Pixel 8", 699f));

		mockMvc.perform(post("/account/saveLine").param("pid", "3").param("phoneNumber", "5551234567"))
				.andExpect(redirectedUrl("/account/myAccount"))
				.andExpect(flash().attribute("message", "Pixel 8 line added."));

		ArgumentCaptor<PhoneLine> line = ArgumentCaptor.forClass(PhoneLine.class);
		verify(phoneService).saveLine(line.capture());
		assertThat(line.getValue().getCid()).isEqualTo(customer.getCid());
		assertThat(line.getValue().getPhoneName()).isEqualTo("Pixel 8");
		assertThat(customer.getBalance()).isEqualTo(40f + 10f + 699f);
		assertThat(customer.getPhoneLines()).isEqualTo(1);
		verify(phoneService).addCustomer(customer);
	}

	@Test
	void addingALineRequiresChoosingAPhone() throws Exception {
		signedInCustomer(0f);
		when(phoneService.getPhone(anyInt())).thenReturn(null);
		when(phoneService.getPhones()).thenReturn(List.of());

		mockMvc.perform(post("/account/saveLine").param("phoneNumber", "5551234567"))
				.andExpect(view().name("addLine"))
				.andExpect(model().attributeHasFieldErrorCode("phoneLine", "pid", "required"));

		verify(phoneService, never()).saveLine(any());
	}

	@Test
	void cannotAddMoreLinesThanThePlanAllows() throws Exception {
		Customer customer = signedInCustomer(0f);
		customer.setPlanId(1);
		customer.setPhoneLines(1);
		when(phoneService.getPhonePlan(customer)).thenReturn(plan(1, 1, 40f));

		mockMvc.perform(get("/account/addALine"))
				.andExpect(redirectedUrl("/account/myAccount"))
				.andExpect(flash().attribute("lineError", "Your plan allows 1 line(s). Choose a bigger plan to add more."));
	}

	@Test
	void addLinePagePreselectsThePhone() throws Exception {
		signedInCustomer(0f);
		when(phoneService.getPhones()).thenReturn(List.of(phone(3, "Pixel 8", 699f)));

		mockMvc.perform(get("/account/addALine").param("pid", "3"))
				.andExpect(view().name("addLine"))
				.andExpect(model().attribute("phoneLine", org.hamcrest.Matchers.hasProperty("pid", org.hamcrest.Matchers.is(3))));
	}

	// ---- payments ----

	@Test
	void payingWithASavedCardClearsTheBalance() throws Exception {
		Customer customer = signedInCustomer(120f);
		CreditCard card = new CreditCard();
		card.setCrid(9);
		card.setCid(customer.getCid());
		when(phoneService.getCreditCard(9)).thenReturn(card);

		mockMvc.perform(post("/account/savePaymentS").param("crid", "9"))
				.andExpect(redirectedUrl("/account/myAccount"));

		ArgumentCaptor<Transaction> transaction = ArgumentCaptor.forClass(Transaction.class);
		verify(phoneService).savePayment(transaction.capture(), any(), any());
		assertThat(transaction.getValue().getAmount()).isEqualTo(120f);
		assertThat(transaction.getValue().getCrid()).isEqualTo(9);
		assertThat(customer.getBalance()).isZero();
	}

	@Test
	void cannotPayWithSomeoneElsesCard() throws Exception {
		Customer customer = signedInCustomer(120f);
		CreditCard card = new CreditCard();
		card.setCrid(9);
		card.setCid(999);
		when(phoneService.getCreditCard(9)).thenReturn(card);

		mockMvc.perform(post("/account/savePaymentS").param("crid", "9"))
				.andExpect(redirectedUrl("/account/pay"));

		verify(phoneService, never()).savePayment(any(), any(), any());
		assertThat(customer.getBalance()).isEqualTo(120f);
	}

	@Test
	void newCardPaymentIsValidated() throws Exception {
		signedInCustomer(50f);
		when(phoneService.getCreditCards(any())).thenReturn(List.of());

		mockMvc.perform(post("/account/savePayment").param("creditCardNumber", "12").param("vendor", "Visa"))
				.andExpect(view().name("payment"))
				.andExpect(model().attributeHasFieldErrors("creditCard", "creditCardNumber", "expirationDate", "csc"));

		verify(phoneService, never()).savePayment(any(), any(), any());
	}

	// ---- password reset ----

	@Test
	void resetWithUnknownEmailShowsAnError() throws Exception {
		mockMvc.perform(post("/account/reset").param("email", "nobody@example.com"))
				.andExpect(view().name("verifyReset"))
				.andExpect(model().attributeExists("error"));
	}

	@Test
	void updatingCredentialsNeedsTheEmailStepFirst() throws Exception {
		mockMvc.perform(post("/account/updateCredentials")
						.param("name", "Ann").param("email", "ann@example.com")
						.param("password", "newpassword1").param("birthday", "1990-01-01"))
				.andExpect(redirectedUrl("/account/forgotPW"));

		verify(phoneService, never()).addCustomer(any());
	}

	@Test
	void updatingCredentialsChangesTheSameCustomerAndHashesThePassword() throws Exception {
		Customer customer = customer(5, Role.ADMIN);
		when(phoneService.getCustomer(5)).thenReturn(customer);
		when(passwordEncoder.encode("newpassword1")).thenReturn("{bcrypt}new");

		mockMvc.perform(post("/account/updateCredentials").sessionAttr("resetUserId", 5)
						.param("name", "Ann").param("email", "ann@example.com")
						.param("password", "newpassword1").param("birthday", "1990-01-01"))
				.andExpect(redirectedUrl("/account/login"));

		assertThat(customer.getCid()).isEqualTo(5);
		assertThat(customer.getName()).isEqualTo("Ann");
		assertThat(customer.getPassword()).isEqualTo("{bcrypt}new");
		assertThat(customer.getRole()).isEqualTo(Role.ADMIN);
		verify(phoneService).addCustomer(customer);
	}

	private Customer signedInCustomer(float balance) {
		Customer customer = customer(5, Role.CUSTOMER);
		customer.setBalance(balance);
		SignedIn.as(customer);
		when(phoneService.getCustomer(5)).thenReturn(customer);
		return customer;
	}
}
