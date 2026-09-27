package com.inditex.ecommerce.pricing.application.usecase;

import java.time.LocalDateTime;
import java.util.Objects;
import java.util.Optional;

import com.inditex.ecommerce.pricing.application.exception.InvalidParamsException;
import com.inditex.ecommerce.pricing.application.port.input.GetApplicablePriceInputPort;
import com.inditex.ecommerce.pricing.application.port.output.GetApplicablePriceOutputPort;
import com.inditex.ecommerce.pricing.domain.model.Price;

public class GetApplicablePriceUseCase implements GetApplicablePriceInputPort {
	private final GetApplicablePriceOutputPort getApplicablePriceOutputPort;

	public GetApplicablePriceUseCase(GetApplicablePriceOutputPort getApplicablePriceOutputPort) {
		this.getApplicablePriceOutputPort = getApplicablePriceOutputPort;
	}

	@Override
	public Optional<Price> findApplicablePrice(Integer brandId, Long productId, LocalDateTime applicationDate) {
		validParams(brandId,productId,applicationDate);
		return this.getApplicablePriceOutputPort.findApplicablePrice(brandId, productId, applicationDate);
	}
	
	protected void validParams(Integer brandId, Long productId, LocalDateTime applicationDate) throws InvalidParamsException{
		
		if (Objects.isNull(brandId) || brandId <= 0) throw new InvalidParamsException("Brand ID must be greater than zero");
		
		if (Objects.isNull(productId) || productId <= 0) throw new InvalidParamsException("Product ID must be greater than zero");
		
		if (Objects.isNull(applicationDate)) throw new InvalidParamsException("Application date cannot be null");
	}

}
