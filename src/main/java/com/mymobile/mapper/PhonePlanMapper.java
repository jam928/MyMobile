package com.mymobile.mapper;

import java.util.List;

import org.mapstruct.Mapper;

import com.mymobile.dto.response.PhonePlanResponse;
import com.mymobile.entity.PhonePlan;

@Mapper
public interface PhonePlanMapper {

	PhonePlanResponse toResponse(PhonePlan plan);

	List<PhonePlanResponse> toResponses(List<PhonePlan> plans);
}
