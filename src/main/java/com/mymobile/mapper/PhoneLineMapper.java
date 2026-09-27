package com.mymobile.mapper;

import java.util.List;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import com.mymobile.dto.request.AddPhoneLineRequest;
import com.mymobile.dto.response.PhoneLineResponse;
import com.mymobile.entity.Phone;
import com.mymobile.entity.PhoneLine;

@Mapper
public interface PhoneLineMapper {

	// a phone line copies the name, photo and color of the phone it was created with
	@Mapping(target = "plid", ignore = true)
	@Mapping(target = "pid", source = "phone.pid")
	@Mapping(target = "phoneNumber", source = "request.phoneNumber")
	@Mapping(target = "cid", source = "cid")
	@Mapping(target = "phoneName", source = "phone.name")
	@Mapping(target = "imgSrc", source = "phone.imgSrc")
	@Mapping(target = "alt", source = "phone.alt")
	@Mapping(target = "color", source = "phone.color")
	PhoneLine toEntity(AddPhoneLineRequest request, Phone phone, int cid);

	PhoneLineResponse toResponse(PhoneLine phoneLine);

	List<PhoneLineResponse> toResponses(List<PhoneLine> phoneLines);
}
