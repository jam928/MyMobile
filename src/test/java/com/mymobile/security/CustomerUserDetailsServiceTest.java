package com.mymobile.security;

import static com.mymobile.TestData.customer;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.when;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import com.mymobile.entity.Customer;
import com.mymobile.entity.Role;
import com.mymobile.service.PhoneService;

@ExtendWith(MockitoExtension.class)
class CustomerUserDetailsServiceTest {

	@Mock
	PhoneService phoneService;

	@InjectMocks
	CustomerUserDetailsService userDetailsService;

	@Test
	void loadsCustomerWithTheirRole() {
		Customer admin = customer(7, Role.ADMIN);
		when(phoneService.getCustomerByEmail("customer7@example.com")).thenReturn(admin);

		CustomerUserDetails user = (CustomerUserDetails) userDetailsService.loadUserByUsername("customer7@example.com");

		assertThat(user.getCid()).isEqualTo(7);
		assertThat(user.getUsername()).isEqualTo("customer7@example.com");
		assertThat(user.getPassword()).isEqualTo("{bcrypt}hash");
		assertThat(user.getAuthorities()).extracting(GrantedAuthority::getAuthority).containsExactly("ROLE_ADMIN");
	}

	@Test
	void unknownEmailIsRejected() {
		when(phoneService.getCustomerByEmail("nobody@example.com")).thenReturn(null);

		assertThatThrownBy(() -> userDetailsService.loadUserByUsername("nobody@example.com"))
				.isInstanceOf(UsernameNotFoundException.class);
	}
}
