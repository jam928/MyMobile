package com.mymobile.controller;

import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;

import com.mymobile.entity.Customer;
import com.mymobile.security.CustomerUserDetails;

/**
 * Puts a customer in the security context, as if they had signed in.
 */
final class SignedIn {

	private SignedIn() {
	}

	static void as(Customer customer) {
		CustomerUserDetails user = new CustomerUserDetails(customer);
		SecurityContextHolder.getContext().setAuthentication(
				UsernamePasswordAuthenticationToken.authenticated(user, null, user.getAuthorities()));
	}

	static void clear() {
		SecurityContextHolder.clearContext();
	}
}
