package com.inditex.ecommerce.pricing.infrastructure.adapter.output.persistence.entity;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Objects;
import java.util.UUID;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "PRICES")
public class PriceEntity {

	@Id
	private UUID id;

	@Column(name = "BRAND_ID")
	private Integer brandId;

	@Column(name = "START_DATE")
	private LocalDateTime startDate;

	@Column(name = "END_DATE")
	private LocalDateTime endDate;

	@Column(name = "TARIFF_ID")
	private Long tariffId;

	@Column(name = "PRODUCT_ID")
	private Long productId;

	@Column(name = "PRIORITY")
	private Integer priority;

	@Column(name = "PRICE")
	private BigDecimal price;

	@Column(name = "CURRENCY_CODE")
	private String currencyCode;

	@Column(name = "CREATED_AT")
	private LocalDateTime createdAt;
	
	public UUID getId() {
		return id;
	}

	public void setId(UUID id) {
		this.id = id;
	}

	public Integer getBrandId() {
		return brandId;
	}

	public void setBrandId(Integer brandId) {
		this.brandId = brandId;
	}

	public LocalDateTime getStartDate() {
		return startDate;
	}

	public void setStartDate(LocalDateTime startDate) {
		this.startDate = startDate;
	}

	public LocalDateTime getEndDate() {
		return endDate;
	}

	public void setEndDate(LocalDateTime endtDate) {
		this.endDate = endtDate;
	}

	public Long getTariffId() {
		return tariffId;
	}

	public void setTariffId(Long tariffId) {
		this.tariffId = tariffId;
	}

	public Long getProductId() {
		return productId;
	}

	public void setProductId(Long productId) {
		this.productId = productId;
	}

	public Integer getPriority() {
		return priority;
	}

	public void setPriority(Integer priority) {
		this.priority = priority;
	}

	public BigDecimal getPrice() {
		return price;
	}

	public void setPrice(BigDecimal price) {
		this.price = price;
	}

	public String getCurrencyCode() {
		return currencyCode;
	}

	public void setCurrencyCode(String currencyCode) {
		this.currencyCode = currencyCode;
	}

	public LocalDateTime getCreatedAt() {
		return createdAt;
	}

	public void setCreatedAt(LocalDateTime createdAt) {
		this.createdAt = createdAt;
	}

	@Override
	public int hashCode() {
		return Objects.hash(id);
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		PriceEntity other = (PriceEntity) obj;
		return Objects.equals(id, other.id);
	}

	public static Builder builder() {
		return new Builder();
	}

	public static class Builder {
		private final PriceEntity priceEntity;

		private Builder() {
			this.priceEntity = new PriceEntity();
		}

		public Builder id(UUID id) {
			priceEntity.id = id;
			return this;
		}

		public Builder brandId(Integer brandId) {
			priceEntity.brandId = brandId;
			return this;
		}

		public Builder startDate(LocalDateTime startDate) {
			priceEntity.startDate = startDate;
			return this;
		}

		public Builder endDate(LocalDateTime endDate) {
			priceEntity.endDate = endDate;
			return this;
		}

		public Builder tariffId(Long tariffId) {
			priceEntity.tariffId = tariffId;
			return this;
		}

		public Builder productId(Long productId) {
			priceEntity.productId = productId;
			return this;
		}

		public Builder priority(Integer priority) {
			priceEntity.priority = priority;
			return this;
		}

		public Builder price(BigDecimal price) {
			priceEntity.price = price;
			return this;
		}

		public Builder currencyCode(String currencyCode) {
			priceEntity.currencyCode = currencyCode;
			return this;
		}

		public Builder createdAt(LocalDateTime createdAt) {
			priceEntity.createdAt = createdAt;
			return this;
		}

		public PriceEntity build() {
			return priceEntity;
		}
	}

}
