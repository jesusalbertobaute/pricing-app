package com.inditex.ecommerce.pricing.infrastructure.adapter.input.rest;

import java.time.LocalDateTime;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

import com.inditex.ecommerce.pricing.application.port.input.GetApplicablePriceInputPort;
import com.inditex.ecommerce.pricing.domain.model.Price;
import com.inditex.ecommerce.pricing.generated.api.PricingApi;
import com.inditex.ecommerce.pricing.generated.model.ApplicablePriceDto;
import com.inditex.ecommerce.pricing.infrastructure.adapter.exception.ApplicablePriceNotFoundException;
import com.inditex.ecommerce.pricing.infrastructure.adapter.exception.RateLimiterException;
import com.inditex.ecommerce.pricing.infrastructure.adapter.mapper.ApplicablePriceMapper;

import io.github.resilience4j.ratelimiter.RequestNotPermitted;
import io.github.resilience4j.ratelimiter.annotation.RateLimiter;

@RestController
public class RestApplicablePriceController implements PricingApi{
	private static final Logger log = LoggerFactory.getLogger(RestApplicablePriceController.class);
	private final GetApplicablePriceInputPort getApplicablePriceUseCase;
	private final ApplicablePriceMapper mapper;
	
	public RestApplicablePriceController(GetApplicablePriceInputPort getApplicablePriceUseCase, ApplicablePriceMapper mapper) {
		this.getApplicablePriceUseCase = getApplicablePriceUseCase;
		this.mapper = mapper;
	}
	
	
	@Override
	@RateLimiter(name = "getApplicablePriceApiLimiter", fallbackMethod = "fallbackGetApplicablePrice")
	public ResponseEntity<ApplicablePriceDto> getPrice(final Integer brandId,
			   final Long productId,
			   final LocalDateTime applicationDate
			){
		log.info(
	            "Getting applicable price: brandId={}, productId={}, applicationDate={}",
	            brandId,
	            productId,
	            applicationDate
	    );
		final Price price = this.getApplicablePriceUseCase.findApplicablePrice(brandId, productId, applicationDate)
				            .orElseThrow(() ->new ApplicablePriceNotFoundException("Applicable Price Not Found"));
		final ApplicablePriceDto applicablePriceDto = this.mapper.toApplicablePriceDto(price);
		return ResponseEntity.status(HttpStatus.OK).body(applicablePriceDto);
	}
	
	public ResponseEntity<ApplicablePriceDto> fallbackGetApplicablePrice(final Integer brandId,
			   final Long productId,
			   final LocalDateTime applicationDate, RequestNotPermitted  ex){
		throw new RateLimiterException("Too many request getting applicable price");
	}

}
