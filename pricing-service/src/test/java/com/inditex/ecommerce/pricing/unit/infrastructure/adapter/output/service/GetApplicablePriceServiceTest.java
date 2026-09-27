package com.inditex.ecommerce.pricing.unit.infrastructure.adapter.output.service;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import com.inditex.ecommerce.pricing.domain.model.Price;
import com.inditex.ecommerce.pricing.infrastructure.adapter.mapper.ApplicablePriceMapper;
import com.inditex.ecommerce.pricing.infrastructure.adapter.output.persistence.entity.PriceEntity;
import com.inditex.ecommerce.pricing.infrastructure.adapter.output.persistence.repository.PriceRepository;
import com.inditex.ecommerce.pricing.infrastructure.adapter.output.service.GetApplicablePriceService;

@ExtendWith(MockitoExtension.class)
class GetApplicablePriceServiceTest {

    private static final Integer BRAND_ID = 1;
    private static final Long PRODUCT_ID = 35455L;

    private static final LocalDateTime START_DATE =
            LocalDateTime.of(2026, 6, 14, 0, 0);

    private static final LocalDateTime END_DATE =
            LocalDateTime.of(2026, 6, 14, 23, 59);

    private static final LocalDateTime APPLICATION_DATE =
            LocalDateTime.of(2026, 6, 14, 10, 0);

    private static final Long TARIFF_ID = 1L;
    private static final Integer PRIORITY = 0;
    private static final BigDecimal PRICE_AMOUNT =
            new BigDecimal("35.50");
    private static final String CURRENCY_CODE = "EUR";
    private static final LocalDateTime CREATED_AT =
            LocalDateTime.of(2026, 1, 1, 10, 0);

    @Mock
    private PriceRepository priceRepository;

    @Mock
    private ApplicablePriceMapper applicablePriceMapper;

    @InjectMocks
    private GetApplicablePriceService service;

    @Test
    @DisplayName("""
            Given a brand, product and application date with an applicable price
            When the applicable price is requested
            Then the applicable price should be returned
            """)
    void shouldReturnApplicablePriceWhenPriceExists() {

        final Pageable pageable = PageRequest.of(0, 1);

        final UUID priceId = UUID.randomUUID();

        final PriceEntity priceEntity = createPriceEntity(
                priceId,
                TARIFF_ID,
                PRIORITY,
                PRICE_AMOUNT);

        final Price price = createPrice(
                priceId,
                TARIFF_ID,
                PRIORITY,
                PRICE_AMOUNT);

        when(priceRepository.findPrices(
                BRAND_ID,
                PRODUCT_ID,
                APPLICATION_DATE,
                pageable))
            .thenReturn(List.of(priceEntity));

        when(applicablePriceMapper.toPrice(priceEntity))
            .thenReturn(price);

        final Optional<Price> result =
                service.findApplicablePrice(
                        BRAND_ID,
                        PRODUCT_ID,
                        APPLICATION_DATE);

        assertThat(result)
                .isPresent()
                .containsSame(price);

        verify(priceRepository).findPrices(
                BRAND_ID,
                PRODUCT_ID,
                APPLICATION_DATE,
                pageable);

        verify(applicablePriceMapper)
                .toPrice(priceEntity);
    }

    @Test
    @DisplayName("""
            Given a brand, product and application date with no applicable prices
            When the applicable price is requested
            Then an empty Optional should be returned
            And the price mapper should not be invoked
            """)
    void shouldReturnEmptyWhenNoPriceExists() {

        final Pageable pageable = PageRequest.of(0, 1);

        when(priceRepository.findPrices(
                BRAND_ID,
                PRODUCT_ID,
                APPLICATION_DATE,
                pageable))
            .thenReturn(List.of());

        final Optional<Price> result =
                service.findApplicablePrice(
                        BRAND_ID,
                        PRODUCT_ID,
                        APPLICATION_DATE);

        assertThat(result).isEmpty();

        verify(priceRepository).findPrices(
                BRAND_ID,
                PRODUCT_ID,
                APPLICATION_DATE,
                pageable);

        verify(applicablePriceMapper, never())
                .toPrice(any(PriceEntity.class));
    }

    @Test
    @DisplayName("""
            Given multiple applicable prices returned by the repository
            When the applicable price is requested
            Then only the first price should be mapped and returned
            And the remaining prices should not be mapped
            """)
    void shouldMapOnlyFirstPriceWhenRepositoryReturnsMultiplePrices() {

        final Pageable pageable = PageRequest.of(0, 1);

        final UUID firstPriceId = UUID.randomUUID();
        final UUID secondPriceId = UUID.randomUUID();

        final PriceEntity firstPriceEntity = createPriceEntity(
                firstPriceId,
                1L,
                0,
                new BigDecimal("35.50"));

        final PriceEntity secondPriceEntity = createPriceEntity(
                secondPriceId,
                2L,
                1,
                new BigDecimal("25.45"));

        final Price firstPrice = createPrice(
                firstPriceId,
                1L,
                0,
                new BigDecimal("35.50"));

        when(priceRepository.findPrices(
                BRAND_ID,
                PRODUCT_ID,
                APPLICATION_DATE,
                pageable))
            .thenReturn(List.of(
                    firstPriceEntity,
                    secondPriceEntity));

        when(applicablePriceMapper.toPrice(firstPriceEntity))
            .thenReturn(firstPrice);

        final Optional<Price> result =
                service.findApplicablePrice(
                        BRAND_ID,
                        PRODUCT_ID,
                        APPLICATION_DATE);

        assertThat(result)
                .isPresent()
                .containsSame(firstPrice);

        verify(applicablePriceMapper)
                .toPrice(firstPriceEntity);

        verify(applicablePriceMapper, never())
                .toPrice(secondPriceEntity);
    }

    private PriceEntity createPriceEntity(
            UUID id,
            Long tariffId,
            Integer priority,
            BigDecimal price) {

        return PriceEntity.builder()
                .id(id)
                .brandId(BRAND_ID)
                .startDate(START_DATE)
                .endDate(END_DATE)
                .tariffId(tariffId)
                .productId(PRODUCT_ID)
                .priority(priority)
                .price(price)
                .currencyCode(CURRENCY_CODE)
                .createdAt(CREATED_AT)
                .build();
    }

    private Price createPrice(
            UUID id,
            Long tariffId,
            Integer priority,
            BigDecimal price) {

        return new Price(
                id,
                BRAND_ID,
                START_DATE,
                END_DATE,
                tariffId,
                PRODUCT_ID,
                priority,
                price,
                CURRENCY_CODE,
                CREATED_AT);
    }
}