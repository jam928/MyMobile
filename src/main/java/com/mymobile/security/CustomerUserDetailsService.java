package com.mymobile.security;

import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import com.mymobile.entity.Customer;
import com.mymobile.service.PhoneService;

import lombok.RequiredArgsConstructor;

/**
 * Looks customers up by email for Spring Security's login.
 */
@Service
@RequiredArgsConstructor
public class CustomerUserDetailsService implements UserDetailsService {

	private final PhoneService phoneService;

	@Override
	public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
		Customer customer = phoneService.getCustomerByEmail(email);
		if (customer == null)
			throw new UsernameNotFoundException("No customer with that email");
		return new CustomerUserDetails(customer);
	}
}
