package com.inditex.ecommerce.pricing.infrastructure.adapter.exception;

public class RateLimiterException extends RuntimeException {
	private static final long serialVersionUID = -3277117556973617287L;

	public RateLimiterException(String message) {
		super(message);
	}
}
