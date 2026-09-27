package com.mymobile.mapper;

import java.util.List;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;
import org.springframework.util.StringUtils;

import com.mymobile.dto.request.CreatePhoneRequest;
import com.mymobile.dto.response.PhoneResponse;
import com.mymobile.entity.Phone;

@Mapper
public interface PhoneMapper {

	PhoneResponse toResponse(Phone phone);

	List<PhoneResponse> toResponses(List<Phone> phones);

	// the photo is uploaded separately; imgSrc and alt are set by the controller
	@Mapping(target = "pid", ignore = true)
	@Mapping(target = "imgSrc", ignore = true)
	@Mapping(target = "alt", ignore = true)
	@Mapping(target = "name", qualifiedByName = "trim")
	@Mapping(target = "brand", qualifiedByName = "trim")
	@Mapping(target = "color", qualifiedByName = "trim")
	@Mapping(target = "description", qualifiedByName = "trim")
	@Mapping(target = "storage", qualifiedByName = "trim")
	@Mapping(target = "screen", qualifiedByName = "trim")
	@Mapping(target = "camera", qualifiedByName = "trim")
	@Mapping(target = "battery", qualifiedByName = "trim")
	Phone toEntity(CreatePhoneRequest request);

	// trims text and stores empty optional fields as null
	@Named("trim")
	default String trim(String value) {
		return StringUtils.hasText(value) ? value.trim() : null;
	}
}
