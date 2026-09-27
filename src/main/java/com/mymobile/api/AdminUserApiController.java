package com.mymobile.api;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;

import org.springframework.data.domain.PageRequest;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.mymobile.dto.request.ChangeRoleRequest;
import com.mymobile.dto.response.CustomerResponse;
import com.mymobile.dto.response.PageResponse;
import com.mymobile.mapper.CustomerMapper;
import com.mymobile.security.CurrentUser;
import com.mymobile.service.PhoneService;
import com.mymobile.service.UserAdminService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/admin/users")
@RequiredArgsConstructor
@Tag(name = "Admin: users", description = "Requires signing in as an admin")
@SecurityRequirement(name = OpenApiConfig.SESSION_COOKIE)
@ApiResponse(responseCode = "401", description = "Not signed in")
@ApiResponse(responseCode = "403", description = "Signed in, but not an admin")
public class AdminUserApiController {

	private final PhoneService phoneService;
	private final UserAdminService userAdminService;
	private final CustomerMapper customerMapper;

	@GetMapping
	@Operation(summary = "List users", description = "Oldest first. Passwords are never included.")
	public PageResponse<CustomerResponse> listUsers(
			@Parameter(description = "Page number, starting at 1") @RequestParam(name = "page", defaultValue = "1") @Min(1) int page,
			@Parameter(description = "Users per page (1-100)") @RequestParam(name = "size", defaultValue = "20") @Min(1) @Max(100) int size)
	{
		return PageResponse.of(phoneService.getCustomers(PageRequest.of(page - 1, size)).map(customerMapper::toResponse));
	}

	@PutMapping("/{cid}/role")
	@Operation(summary = "Change a user's role",
			description = "Make a user an admin (ADMIN) or a regular customer (CUSTOMER). Admins can't remove their own admin role. "
					+ "A newly promoted admin gets admin access the next time they sign in.")
	@ApiResponse(responseCode = "200", description = "The updated user")
	@ApiResponse(responseCode = "400", description = "Invalid role, or an admin tried to remove their own admin role")
	@ApiResponse(responseCode = "404", description = "No user with that id")
	public CustomerResponse changeRole(@PathVariable int cid, @Valid @RequestBody ChangeRoleRequest request)
	{
		return customerMapper.toResponse(userAdminService.changeRole(CurrentUser.cid(), cid, request.role()));
	}
}
