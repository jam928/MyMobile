package com.mymobile.mapper;

import java.util.List;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;

import com.mymobile.dto.request.PaymentRequest;
import com.mymobile.dto.response.CreditCardResponse;
import com.mymobile.entity.CreditCard;

@Mapper
public interface CreditCardMapper {

	@Mapping(target = "crid", ignore = true)
	@Mapping(target = "cid", ignore = true)
	CreditCard toEntity(PaymentRequest request);

	@Mapping(target = "maskedNumber", source = "creditCardNumber", qualifiedByName = "mask")
	CreditCardResponse toResponse(CreditCard card);

	List<CreditCardResponse> toResponses(List<CreditCard> cards);

	// "4111111111111111" -> "**** 1111"
	@Named("mask")
	default String mask(String creditCardNumber) {
		if (creditCardNumber == null || creditCardNumber.length() < 4)
			return creditCardNumber;
		return "**** " + creditCardNumber.substring(creditCardNumber.length() - 4);
	}
}
