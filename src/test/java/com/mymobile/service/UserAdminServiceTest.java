package com.mymobile.service;

import static com.mymobile.TestData.customer;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.NoSuchElementException;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.access.AccessDeniedException;

import com.mymobile.entity.Customer;
import com.mymobile.entity.Role;

@ExtendWith(MockitoExtension.class)
class UserAdminServiceTest {

	@Mock
	PhoneService phoneService;

	@InjectMocks
	UserAdminService userAdminService;

	@Test
	void adminCanPromoteACustomer() {
		Customer admin = customer(1, Role.ADMIN);
		Customer target = customer(2, Role.CUSTOMER);
		when(phoneService.getCustomer(1)).thenReturn(admin);
		when(phoneService.getCustomer(2)).thenReturn(target);

		Customer result = userAdminService.changeRole(1, 2, Role.ADMIN);

		assertThat(result.getRole()).isEqualTo(Role.ADMIN);
		verify(phoneService).addCustomer(target);
	}

	@Test
	void adminCanDemoteAnotherAdmin() {
		when(phoneService.getCustomer(1)).thenReturn(customer(1, Role.ADMIN));
		Customer target = customer(2, Role.ADMIN);
		when(phoneService.getCustomer(2)).thenReturn(target);

		userAdminService.changeRole(1, 2, Role.CUSTOMER);

		assertThat(target.getRole()).isEqualTo(Role.CUSTOMER);
		verify(phoneService).addCustomer(target);
	}

	@Test
	void customerCannotChangeRoles() {
		when(phoneService.getCustomer(1)).thenReturn(customer(1, Role.CUSTOMER));

		assertThatThrownBy(() -> userAdminService.changeRole(1, 2, Role.ADMIN))
				.isInstanceOf(AccessDeniedException.class);
		verify(phoneService, never()).addCustomer(any());
	}

	@Test
	void unknownActingUserCannotChangeRoles() {
		when(phoneService.getCustomer(1)).thenReturn(null);

		assertThatThrownBy(() -> userAdminService.changeRole(1, 2, Role.ADMIN))
				.isInstanceOf(AccessDeniedException.class);
	}

	@Test
	void unknownTargetUserIsReported() {
		when(phoneService.getCustomer(1)).thenReturn(customer(1, Role.ADMIN));
		when(phoneService.getCustomer(99)).thenReturn(null);

		assertThatThrownBy(() -> userAdminService.changeRole(1, 99, Role.ADMIN))
				.isInstanceOf(NoSuchElementException.class)
				.hasMessageContaining("99");
	}

	@Test
	void adminCannotRemoveTheirOwnAdminRole() {
		Customer admin = customer(1, Role.ADMIN);
		when(phoneService.getCustomer(1)).thenReturn(admin);

		assertThatThrownBy(() -> userAdminService.changeRole(1, 1, Role.CUSTOMER))
				.isInstanceOf(IllegalArgumentException.class)
				.hasMessage("You can't remove your own admin role.");
		assertThat(admin.getRole()).isEqualTo(Role.ADMIN);
		verify(phoneService, never()).addCustomer(any());
	}

	@Test
	void settingTheSameRoleSavesNothing() {
		when(phoneService.getCustomer(1)).thenReturn(customer(1, Role.ADMIN));
		when(phoneService.getCustomer(2)).thenReturn(customer(2, Role.ADMIN));

		userAdminService.changeRole(1, 2, Role.ADMIN);

		verify(phoneService, never()).addCustomer(any());
	}
}
