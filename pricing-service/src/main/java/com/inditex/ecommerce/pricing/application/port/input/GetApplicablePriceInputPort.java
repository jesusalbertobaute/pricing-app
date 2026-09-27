package com.inditex.ecommerce.pricing.application.port.input;

import java.time.LocalDateTime;
import java.util.Optional;

import com.inditex.ecommerce.pricing.domain.model.Price;

public interface GetApplicablePriceInputPort {
	Optional<Price> findApplicablePrice(Integer brandId, Long productId, LocalDateTime applicationDate);
}
