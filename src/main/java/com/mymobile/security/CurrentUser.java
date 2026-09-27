package com.mymobile.security;

import java.util.Optional;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

/**
 * The customer signed in on the current request, if any.
 */
public final class CurrentUser {

	private CurrentUser() {
	}

	public static Optional<CustomerUserDetails> get() {
		Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
		if (authentication != null && authentication.getPrincipal() instanceof CustomerUserDetails user)
			return Optional.of(user);
		return Optional.empty();
	}

	// the signed in customer's id, or null for anonymous visitors
	public static Integer cid() {
		return get().map(CustomerUserDetails::getCid).orElse(null);
	}
}
