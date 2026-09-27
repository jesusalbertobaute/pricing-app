package com.inditex.ecommerce.pricing.application.port.output;

import java.time.LocalDateTime;
import java.util.Optional;

import com.inditex.ecommerce.pricing.domain.model.Price;

public interface GetApplicablePriceOutputPort {
	Optional<Price> findApplicablePrice(Integer brandId, Long productId, LocalDateTime applicationDate);
}
