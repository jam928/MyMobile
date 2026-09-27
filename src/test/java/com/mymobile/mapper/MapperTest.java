package com.mymobile.mapper;

import static com.mymobile.TestData.customer;
import static com.mymobile.TestData.phone;
import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;

import com.mymobile.dto.request.AddPhoneLineRequest;
import com.mymobile.dto.request.CreatePhoneRequest;
import com.mymobile.dto.request.PaymentRequest;
import com.mymobile.dto.request.RegisterCustomerRequest;
import com.mymobile.dto.response.CreditCardResponse;
import com.mymobile.dto.response.CustomerResponse;
import com.mymobile.entity.CreditCard;
import com.mymobile.entity.Customer;
import com.mymobile.entity.Phone;
import com.mymobile.entity.PhoneLine;
import com.mymobile.entity.Role;

/**
 * Tests the MapStruct-generated mappers directly.
 */
class MapperTest {

	CustomerMapper customerMapper = new CustomerMapperImpl();
	CreditCardMapper creditCardMapper = new CreditCardMapperImpl();
	PhoneLineMapper phoneLineMapper = new PhoneLineMapperImpl();
	PhoneMapper phoneMapper = new PhoneMapperImpl();

	@Test
	void registrationCreatesACustomerWithNoRoleOrBalanceFromTheForm() {
		RegisterCustomerRequest request = registration("Ann", "ann@example.com");

		Customer customer = customerMapper.toEntity(request);

		assertThat(customer.getName()).isEqualTo("Ann");
		assertThat(customer.getEmail()).isEqualTo("ann@example.com");
		assertThat(customer.getRole()).isEqualTo(Role.CUSTOMER);
		assertThat(customer.getBalance()).isZero();
		assertThat(customer.getCid()).isZero();
	}

	@Test
	void resetFormKeepsRoleBalanceAndPlan() {
		Customer admin = customer(3, Role.ADMIN);
		admin.setBalance(42f);
		admin.setPlanId(2);
		admin.setPhoneLines(1);

		customerMapper.updateEntity(registration("New Name", "new@example.com"), admin);

		assertThat(admin.getName()).isEqualTo("New Name");
		assertThat(admin.getEmail()).isEqualTo("new@example.com");
		assertThat(admin.getRole()).isEqualTo(Role.ADMIN);
		assertThat(admin.getBalance()).isEqualTo(42f);
		assertThat(admin.getPlanId()).isEqualTo(2);
		assertThat(admin.getPhoneLines()).isEqualTo(1);
	}

	@Test
	void customerResponseHasNoPassword() {
		CustomerResponse response = customerMapper.toResponse(customer(3, Role.ADMIN));

		assertThat(response.role()).isEqualTo(Role.ADMIN);
		assertThat(CustomerResponse.class.getRecordComponents())
				.extracting(component -> component.getName())
				.doesNotContain("password");
	}

	@Test
	void resetFormIsPrefilledWithoutThePassword() {
		assertThat(customerMapper.toRegisterRequest(customer(3, Role.CUSTOMER)).getPassword()).isNull();
	}

	@Test
	void savedCardsOnlyShowTheLastFourDigits() {
		CreditCard card = new CreditCard();
		card.setCrid(9);
		card.setCreditCardNumber("4111111111111234");
		card.setCsc(123);
		card.setVendor("Visa");
		card.setExpirationDate("2030-01");

		CreditCardResponse response = creditCardMapper.toResponse(card);

		assertThat(response.maskedNumber()).isEqualTo("**** 1234");
		assertThat(response.vendor()).isEqualTo("Visa");
		assertThat(response.expirationDate()).isEqualTo("2030-01");
		assertThat(creditCardMapper.mask("123")).isEqualTo("123");
		assertThat(creditCardMapper.mask(null)).isNull();
	}

	@Test
	void paymentFormKeepsTheFullCardNumber() {
		PaymentRequest request = new PaymentRequest();
		request.setCreditCardNumber("4111111111111234");
		request.setCsc(123);
		request.setVendor("Visa");

		CreditCard card = creditCardMapper.toEntity(request);

		assertThat(card.getCreditCardNumber()).isEqualTo("4111111111111234");
		assertThat(card.getCsc()).isEqualTo(123);
	}

	@Test
	void newLineCopiesThePhonesDetails() {
		AddPhoneLineRequest request = new AddPhoneLineRequest();
		request.setPid(4);
		request.setPhoneNumber("5551234567");

		PhoneLine line = phoneLineMapper.toEntity(request, phone(4, "Pixel 8", 699f), 12);

		assertThat(line.getPid()).isEqualTo(4);
		assertThat(line.getCid()).isEqualTo(12);
		assertThat(line.getPhoneNumber()).isEqualTo("5551234567");
		assertThat(line.getPhoneName()).isEqualTo("Pixel 8");
		assertThat(line.getImgSrc()).isEqualTo("4.jpg");
		assertThat(line.getColor()).isEqualTo("Black");
	}

	@Test
	void newPhoneFormTrimsTextAndStoresBlankSpecsAsNull() {
		CreatePhoneRequest request = new CreatePhoneRequest();
		request.setName("  Galaxy S25  ");
		request.setBrand("Samsung ");
		request.setColor("Navy");
		request.setPrice(799f);
		request.setQuantity(4);
		request.setStorage("   ");
		request.setScreen("");

		Phone phone = phoneMapper.toEntity(request);

		assertThat(phone.getName()).isEqualTo("Galaxy S25");
		assertThat(phone.getBrand()).isEqualTo("Samsung");
		assertThat(phone.getStorage()).isNull();
		assertThat(phone.getScreen()).isNull();
		assertThat(phone.getRating()).isEqualTo(5);
		assertThat(phone.getCondition()).isEqualTo("New");
		assertThat(phone.getImgSrc()).isNull();
	}

	private static RegisterCustomerRequest registration(String name, String email) {
		RegisterCustomerRequest request = new RegisterCustomerRequest();
		request.setName(name);
		request.setEmail(email);
		request.setPassword("password123");
		request.setBirthday("1990-01-01");
		return request;
	}
}
