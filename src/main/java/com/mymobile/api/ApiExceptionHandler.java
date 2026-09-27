package com.mymobile.api;

import java.util.NoSuchElementException;

import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

/**
 * Turns errors from the /api controllers into RFC 9457 problem details (JSON).
 */
@RestControllerAdvice(basePackageClasses = ApiExceptionHandler.class)
public class ApiExceptionHandler extends ResponseEntityExceptionHandler {

	@ExceptionHandler(NoSuchElementException.class)
	public ProblemDetail notFound(NoSuchElementException e) {
		return ProblemDetail.forStatusAndDetail(HttpStatus.NOT_FOUND, e.getMessage());
	}

	@ExceptionHandler(IllegalArgumentException.class)
	public ProblemDetail badRequest(IllegalArgumentException e) {
		return ProblemDetail.forStatusAndDetail(HttpStatus.BAD_REQUEST, e.getMessage());
	}
}
