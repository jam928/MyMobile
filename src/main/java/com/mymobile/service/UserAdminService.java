package com.mymobile.service;

import java.util.NoSuchElementException;

import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.mymobile.entity.Customer;
import com.mymobile.entity.Role;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * Changes customers' roles. Only an admin may do this, which is checked twice:
 * by @PreAuthorize (the role in the caller's session) and against the database
 * (in case the caller lost their admin role after signing in).
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class UserAdminService {

	private final PhoneService phoneService;

	/**
	 * Gives the target customer the role, on behalf of the acting customer.
	 *
	 * @throws AccessDeniedException if the acting customer isn't an admin
	 * @throws NoSuchElementException if there is no customer with targetCid
	 * @throws IllegalArgumentException if an admin tries to remove their own admin role
	 */
	@PreAuthorize("hasRole('ADMIN')")
	@Transactional
	public Customer changeRole(int actingCid, int targetCid, Role role) {
		Customer acting = phoneService.getCustomer(actingCid);
		if (acting == null || acting.getRole() != Role.ADMIN)
			throw new AccessDeniedException("Only admins can change roles");

		Customer target = phoneService.getCustomer(targetCid);
		if (target == null)
			throw new NoSuchElementException("No user with id " + targetCid);

		// also guarantees there is always at least one admin
		if (target.getCid() == acting.getCid() && role != Role.ADMIN)
			throw new IllegalArgumentException("You can't remove your own admin role.");

		if (target.getRole() != role) {
			log.info("Admin {} changed the role of user {} from {} to {}", acting.getCid(), target.getCid(), target.getRole(), role);
			target.setRole(role);
			phoneService.addCustomer(target);
		}
		return target;
	}
}
