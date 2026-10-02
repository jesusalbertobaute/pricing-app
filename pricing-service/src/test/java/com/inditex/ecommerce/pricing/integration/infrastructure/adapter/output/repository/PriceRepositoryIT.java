package com.inditex.ecommerce.pricing.integration.infrastructure.adapter.output.repository;

import static org.assertj.core.api.Assertions.assertThat;

import java.time.LocalDateTime;
import java.util.List;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;
import org.springframework.data.domain.PageRequest;
import org.springframework.test.context.ActiveProfiles;

import com.inditex.ecommerce.pricing.infrastructure.adapter.output.persistence.entity.PriceEntity;
import com.inditex.ecommerce.pricing.infrastructure.adapter.output.persistence.repository.PriceRepository;

@DataJpaTest
@Tag("Integration")
@ActiveProfiles("integration")
@DisplayName("Tests for PriceRepository")
public class PriceRepositoryIT {
	private static final Integer BRAND_ID = 1;
    private static final Long PRODUCT_ID = 35455L;

    @Autowired
    private PriceRepository priceRepository;

    @Test
    @DisplayName("""
            Given multiple applicable prices for the same product and brand
            When the application date is June 14th 2026 at 16:00
            Then the price with the highest priority should be returned first
            """)
    void shouldReturnPriceWithHighestPriority() {

        final LocalDateTime applicationDate =
                LocalDateTime.of(2026, 6, 14, 16, 0);

        final List<PriceEntity> prices =
                priceRepository.findPrices(
                        BRAND_ID,
                        PRODUCT_ID,
                        applicationDate,
                        PageRequest.of(0, 1));

        assertThat(prices)
                .hasSize(1);

        final PriceEntity price = prices.get(0);

        assertThat(price.getTariffId())
                .isEqualTo(2L);

        assertThat(price.getPriority())
                .isEqualTo(1);

        assertThat(price.getPrice())
                .isEqualByComparingTo("25.45");

        assertThat(price.getCurrencyCode())
                .isEqualTo("EUR");
    }

}
