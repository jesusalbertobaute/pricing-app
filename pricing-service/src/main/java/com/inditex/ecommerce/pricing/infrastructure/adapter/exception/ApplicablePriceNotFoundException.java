package com.inditex.ecommerce.pricing.infrastructure.adapter.exception;

public class ApplicablePriceNotFoundException extends RuntimeException{
	private static final long serialVersionUID = -3390076239507587228L;

	public ApplicablePriceNotFoundException(String message) {
		super(message);
	}
    
}
