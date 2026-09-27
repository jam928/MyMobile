package com.mymobile.service;

import static com.mymobile.TestData.customer;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.mymobile.dao.CustomerDAO;
import com.mymobile.dao.PhoneDAO;
import com.mymobile.entity.Customer;
import com.mymobile.entity.Role;

@ExtendWith(MockitoExtension.class)
class PhoneServiceImplTest {

	@Mock
	PhoneDAO phoneDAO;

	@Mock
	CustomerDAO customerDAO;

	@InjectMocks
	PhoneServiceImpl phoneService;

	@Test
	void deletingOwnLineDecrementsTheLineCount() {
		Customer customer = customer(5, Role.CUSTOMER);
		customer.setPhoneLines(2);
		when(phoneDAO.deletePhoneLine(10, 5)).thenReturn(true);

		phoneService.deletePhoneLine(10, customer);

		assertThat(customer.getPhoneLines()).isEqualTo(1);
		verify(customerDAO).addCustomer(customer);
	}

	@Test
	void deletingSomeoneElsesLineChangesNothing() {
		Customer customer = customer(5, Role.CUSTOMER);
		customer.setPhoneLines(2);
		when(phoneDAO.deletePhoneLine(10, 5)).thenReturn(false);

		phoneService.deletePhoneLine(10, customer);

		assertThat(customer.getPhoneLines()).isEqualTo(2);
		verify(customerDAO, never()).addCustomer(any());
	}

	@Test
	void emailLookupsGoToTheCustomerDao() {
		Customer customer = customer(5, Role.CUSTOMER);
		when(customerDAO.getCustomerByEmail("a@b.com")).thenReturn(customer);
		when(customerDAO.isEmailTaken("a@b.com", 0)).thenReturn(true);

		assertThat(phoneService.getCustomerByEmail("a@b.com")).isSameAs(customer);
		assertThat(phoneService.isEmailTaken("a@b.com", 0)).isTrue();
	}
}
