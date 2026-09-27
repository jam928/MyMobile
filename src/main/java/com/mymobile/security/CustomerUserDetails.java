package com.mymobile.security;

import java.util.Collection;
import java.util.List;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import com.mymobile.entity.Customer;

/**
 * The logged in customer, as Spring Security sees it. Only the id, email, password hash
 * and role are kept; controllers load the full Customer by cid when they need it.
 */
public class CustomerUserDetails implements UserDetails {

	private final int cid;
	private final String email;
	private final String passwordHash;
	private final List<GrantedAuthority> authorities;

	public CustomerUserDetails(Customer customer) {
		this.cid = customer.getCid();
		this.email = customer.getEmail();
		this.passwordHash = customer.getPassword();
		this.authorities = List.of(new SimpleGrantedAuthority("ROLE_" + customer.getRole().name()));
	}

	public int getCid() {
		return cid;
	}

	@Override
	public String getUsername() {
		return email;
	}

	@Override
	public String getPassword() {
		return passwordHash;
	}

	@Override
	public Collection<? extends GrantedAuthority> getAuthorities() {
		return authorities;
	}
}
