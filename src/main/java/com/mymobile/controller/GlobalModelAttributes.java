package com.mymobile.controller;

import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ModelAttribute;

/**
 * Model attributes every page needs, e.g. for the navigation bar.
 */
@ControllerAdvice
public class GlobalModelAttributes {

	@ModelAttribute("loggedIn")
	public boolean loggedIn() {
		return currentAuthentication() != null;
	}

	// only decides whether to show the Admin link; access is enforced by SecurityConfig and AdminInterceptor
	@ModelAttribute("isAdmin")
	public boolean isAdmin() {
		Authentication authentication = currentAuthentication();
		return authentication != null && authentication.getAuthorities().stream()
				.anyMatch(authority -> "ROLE_ADMIN".equals(authority.getAuthority()));
	}

	// the logged in user, or null for anonymous visitors
	private static Authentication currentAuthentication() {
		Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
		if (authentication == null || authentication instanceof AnonymousAuthenticationToken || !authentication.isAuthenticated())
			return null;
		return authentication;
	}
}
