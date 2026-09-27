package com.mymobile.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

import com.mymobile.dto.request.RegisterCustomerRequest;
import com.mymobile.dto.response.CustomerResponse;
import com.mymobile.entity.Customer;

@Mapper
public interface CustomerMapper {

	// a newly registered customer starts with no lines, no plan and no balance
	@Mapping(target = "cid", ignore = true)
	@Mapping(target = "phoneLines", ignore = true)
	@Mapping(target = "balance", ignore = true)
	@Mapping(target = "planId", ignore = true)
	@Mapping(target = "role", ignore = true)
	Customer toEntity(RegisterCustomerRequest request);

	// applies the reset form to the existing customer, keeping their lines, plan and balance
	@Mapping(target = "cid", ignore = true)
	@Mapping(target = "phoneLines", ignore = true)
	@Mapping(target = "balance", ignore = true)
	@Mapping(target = "planId", ignore = true)
	@Mapping(target = "role", ignore = true)
	void updateEntity(RegisterCustomerRequest request, @MappingTarget Customer customer);

	CustomerResponse toResponse(Customer customer);

	// pre-fills the reset form; the customer has to enter a new password
	@Mapping(target = "password", ignore = true)
	RegisterCustomerRequest toRegisterRequest(Customer customer);
}
