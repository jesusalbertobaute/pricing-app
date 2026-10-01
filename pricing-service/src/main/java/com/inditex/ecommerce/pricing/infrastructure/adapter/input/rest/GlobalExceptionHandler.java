package com.inditex.ecommerce.pricing.infrastructure.adapter.input.rest;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

import com.inditex.ecommerce.pricing.generated.model.ErrorDto;
import com.inditex.ecommerce.pricing.infrastructure.adapter.exception.ApplicablePriceNotFoundException;
import com.inditex.ecommerce.pricing.infrastructure.adapter.exception.RateLimiterException;

import jakarta.validation.ConstraintViolationException;

@RestControllerAdvice
public class GlobalExceptionHandler {
	private static final Logger log = LoggerFactory.getLogger(GlobalExceptionHandler.class);
	
	@ExceptionHandler(ApplicablePriceNotFoundException.class)
	public ResponseEntity<ErrorDto> handlePriceNotFound(ApplicablePriceNotFoundException ex) {
		ErrorDto errorResponse = this.createErrorDto(HttpStatus.NOT_FOUND,ex.getMessage());
		return ResponseEntity.status(HttpStatus.NOT_FOUND).body(errorResponse);
	}
	
	@ExceptionHandler(MethodArgumentNotValidException.class)
	public ResponseEntity<ErrorDto> handleValidation(MethodArgumentNotValidException ex) {
		ErrorDto errorResponse = this.createErrorDto(HttpStatus.BAD_REQUEST,ex.getMessage());
		return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorResponse);
	}
	
	@ExceptionHandler(MissingServletRequestParameterException.class)
	public ResponseEntity<ErrorDto> handleValidation(MissingServletRequestParameterException ex) {
		ErrorDto errorResponse = this.createErrorDto(HttpStatus.BAD_REQUEST,ex.getMessage());
		return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorResponse);
	}
	
	@ExceptionHandler(MethodArgumentTypeMismatchException.class)
	public ResponseEntity<ErrorDto> handleValidation(MethodArgumentTypeMismatchException ex) {
		ErrorDto errorResponse = this.createErrorDto(HttpStatus.BAD_REQUEST,ex.getMessage());
		return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorResponse);
	}

	@ExceptionHandler(ConstraintViolationException.class)
	public ResponseEntity<ErrorDto> handleValidation(ConstraintViolationException ex) {
		ErrorDto errorResponse = this.createErrorDto(HttpStatus.BAD_REQUEST,ex.getMessage());
		return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorResponse);
	}
	
	@ExceptionHandler(RateLimiterException.class)
	public ResponseEntity<ErrorDto> handleRateLimiterException(RateLimiterException ex) {
		ErrorDto errorResponse = this.createErrorDto(HttpStatus.TOO_MANY_REQUESTS,ex.getMessage());
		return ResponseEntity.status(HttpStatus.TOO_MANY_REQUESTS).body(errorResponse);
	}
	
	@ExceptionHandler(Exception.class)
	public ResponseEntity<ErrorDto> handleException(Exception ex) {
		log.error(ex.getMessage(),ex);
		ErrorDto errorResponse = this.createErrorDto(HttpStatus.INTERNAL_SERVER_ERROR,ex.getMessage());
		return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse);
	}
	
	protected ErrorDto createErrorDto(HttpStatus httpStatus, String message) {
		return new ErrorDto("about:blank", httpStatus.value(), httpStatus.getReasonPhrase() ,message);
	}
	
	protected ErrorDto createErrorDto(String type, HttpStatus httpStatus, String title, String detail) {
		return new ErrorDto(type,httpStatus.value(),title,detail);
	}

}
