package com.inditex.ecommerce.pricing.domain.model;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

import com.inditex.ecommerce.pricing.domain.exception.DateRangeNotAllowedException;
import com.inditex.ecommerce.pricing.domain.exception.DomainException;

public record Price(UUID id, 
		            Long brandId, 
		            LocalDateTime startDate, 
		            LocalDateTime endDate,
		            Long tariffId,
		            Long productId,
		            Integer priority,
		            BigDecimal endPrice,
		            String currencyCode,
		            LocalDateTime createdAt
		            ) {
	
	public Price {

        if (id == null) {
            throw new DomainException("id cannot be null");
        }

        if (startDate == null || endDate == null) {
            throw new DomainException("startDate and endDate cannot be null");
        }

        if (startDate.isAfter(endDate)) {
            throw new DateRangeNotAllowedException("startDate cannot be after endDate");
        }

        if (tariffId == null || tariffId <= 0) {
            throw new DomainException("tariffId cannot be null or less than or equal to zero");
        }
        
        if (productId == null || productId <= 0) {
            throw new DomainException("productId cannot be null or less than or equal to zero");
        }
        
        if (priority == null || priority < 0) {
            throw new DomainException("priority cannot be null or less than zero");
        }
        
        if (endPrice == null || endPrice.compareTo(BigDecimal.ZERO) <= 0) {
            throw new DomainException("endPrice must be greater than zero");
        }
        
        if (currencyCode == null
                || currencyCode.isBlank()
                || currencyCode.length() != 3
                || !currencyCode.equals(currencyCode.toUpperCase())) {
            throw new DomainException("currencyCode must be a valid 3-letter uppercase code");
        }
        
        if (createdAt == null) {
            throw new DomainException("createdAt cannot be null");
        }

    }
	
	public boolean appliesAt(LocalDateTime applicationDate) {
        return !applicationDate.isBefore(startDate)
                && !applicationDate.isAfter(endDate);
    }

    public boolean hasHigherPriorityThan(Price other) {
        return this.priority > other.priority;
    }

}
