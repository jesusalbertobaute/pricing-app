package com.inditex.ecommerce.pricing.infrastructure.adapter.output.service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import com.inditex.ecommerce.pricing.application.port.output.GetApplicablePriceOutputPort;
import com.inditex.ecommerce.pricing.domain.model.Price;
import com.inditex.ecommerce.pricing.infrastructure.adapter.mapper.ApplicablePriceMapper;
import com.inditex.ecommerce.pricing.infrastructure.adapter.output.persistence.entity.PriceEntity;
import com.inditex.ecommerce.pricing.infrastructure.adapter.output.persistence.repository.PriceRepository;

@Service
public class GetApplicablePriceService implements GetApplicablePriceOutputPort{
	private static final Logger log = LoggerFactory.getLogger(GetApplicablePriceService.class);
	private final PriceRepository priceRepository;
	private final ApplicablePriceMapper applicablePriceMapper;

	public GetApplicablePriceService(PriceRepository priceRepository, 
			ApplicablePriceMapper applicablePriceMapper) {
		this.priceRepository = priceRepository;
		this.applicablePriceMapper =  applicablePriceMapper;
	}

	@Override
	public Optional<Price> findApplicablePrice(Integer brandId, Long productId, LocalDateTime applicationDate) {
		log.info(
				"Searching applicable price: brandId={}, productId={}, applicationDate={}",
				brandId,
				productId,
				applicationDate
				);
		final Pageable pageable = PageRequest.of(0, 1);

		List<PriceEntity> priceEntities =  this.priceRepository.findPrices(brandId,productId,applicationDate,pageable);

		return priceEntities.stream()
				.findFirst()
				.map(this.applicablePriceMapper::toPrice);


	}

}
