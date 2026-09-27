package com.mymobile.dto.response;

/**
 * A saved card as shown to the customer: only the last four digits, and never the security code.
 */
public record CreditCardResponse(
		int crid,
		String maskedNumber,
		String expirationDate,
		String vendor) {
}
