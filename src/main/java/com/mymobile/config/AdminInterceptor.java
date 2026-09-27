package com.mymobile.config;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

import com.mymobile.entity.Customer;
import com.mymobile.entity.Role;
import com.mymobile.security.CurrentUser;
import com.mymobile.service.PhoneService;

import lombok.RequiredArgsConstructor;

/**
 * Extra check for /admin/** and /api/admin/**: SecurityConfig already requires ROLE_ADMIN, but that role is
 * captured at login. This re-reads the role from the database on every request, so removing
 * someone's admin role takes effect immediately instead of at their next login.
 */
@Component
@RequiredArgsConstructor
public class AdminInterceptor implements HandlerInterceptor {

	private final PhoneService phoneService;

	@Override
	public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
		Integer cid = CurrentUser.cid();
		Customer customer = cid == null ? null : phoneService.getCustomer(cid);

		if (customer == null || customer.getRole() != Role.ADMIN) {
			response.sendError(HttpServletResponse.SC_FORBIDDEN);
			return false;
		}
		return true;
	}
}
