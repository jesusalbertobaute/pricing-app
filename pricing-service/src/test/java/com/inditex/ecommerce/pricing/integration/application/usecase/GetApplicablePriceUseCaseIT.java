package com.inditex.ecommerce.pricing.integration.application.usecase;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Optional;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.TestPropertySource;

import com.inditex.ecommerce.pricing.application.exception.InvalidParamsException;
import com.inditex.ecommerce.pricing.application.port.input.GetApplicablePriceInputPort;
import com.inditex.ecommerce.pricing.domain.model.Price;

@SpringBootTest
@ActiveProfiles("integration")
@Tag("Integration")
@TestPropertySource(properties = {
	    "spring.datasource.url=jdbc:h2:mem:usecase-testdb"
})
@DisplayName("Tests for GetApplicablePriceUseCase")
class GetApplicablePriceUseCaseIT {

    private static final Integer VALID_BRAND_ID = 1;
    private static final Long VALID_PRODUCT_ID = 35455L;

    private static final LocalDateTime VALID_APPLICATION_DATE =
            LocalDateTime.of(2020, 6, 14, 10, 0);

    @Autowired
    private GetApplicablePriceInputPort getApplicablePriceUseCase;

    @Test
    @DisplayName("""
            Given a null brand id
            When the applicable price is requested
            Then an InvalidParamsException should be thrown
            """)
    void shouldRejectNullBrandId() {

        assertThatThrownBy(() ->
                getApplicablePriceUseCase.findApplicablePrice(
                        null,
                        VALID_PRODUCT_ID,
                        VALID_APPLICATION_DATE))
            .isInstanceOf(InvalidParamsException.class)
            .hasMessage("Brand ID must be greater than zero");
    }

    @Test
    @DisplayName("""
            Given a brand id equal to zero
            When the applicable price is requested
            Then an InvalidParamsException should be thrown
            """)
    void shouldRejectZeroBrandId() {
        assertThatThrownBy(() ->
                getApplicablePriceUseCase.findApplicablePrice(
                        0,
                        VALID_PRODUCT_ID,
                        VALID_APPLICATION_DATE))
            .isInstanceOf(InvalidParamsException.class)
            .hasMessage("Brand ID must be greater than zero");
    }

    @Test
    @DisplayName("""
            Given a negative brand id
            When the applicable price is requested
            Then an InvalidParamsException should be thrown
            """)
    void shouldRejectNegativeBrandId() {

        assertThatThrownBy(() ->
                getApplicablePriceUseCase.findApplicablePrice(
                        -1,
                        VALID_PRODUCT_ID,
                        VALID_APPLICATION_DATE))
            .isInstanceOf(InvalidParamsException.class)
            .hasMessage("Brand ID must be greater than zero");
    }

    @Test
    @DisplayName("""
            Given a null product id
            When the applicable price is requested
            Then an InvalidParamsException should be thrown
            """)
    void shouldRejectNullProductId() {

        assertThatThrownBy(() ->
                getApplicablePriceUseCase.findApplicablePrice(
                        VALID_BRAND_ID,
                        null,
                        VALID_APPLICATION_DATE))
            .isInstanceOf(InvalidParamsException.class)
            .hasMessage("Product ID must be greater than zero");
    }

    @Test
    @DisplayName("""
            Given a product id equal to zero
            When the applicable price is requested
            Then an InvalidParamsException should be thrown
            """)
    void shouldRejectZeroProductId() {

        assertThatThrownBy(() ->
                getApplicablePriceUseCase.findApplicablePrice(
                        VALID_BRAND_ID,
                        0L,
                        VALID_APPLICATION_DATE))
            .isInstanceOf(InvalidParamsException.class)
            .hasMessage("Product ID must be greater than zero");
    }

    @Test
    @DisplayName("""
            Given a negative product id
            When the applicable price is requested
            Then an InvalidParamsException should be thrown
            """)
    void shouldRejectNegativeProductId() {

        assertThatThrownBy(() ->
                getApplicablePriceUseCase.findApplicablePrice(
                        VALID_BRAND_ID,
                        -1L,
                        VALID_APPLICATION_DATE))
            .isInstanceOf(InvalidParamsException.class)
            .hasMessage("Product ID must be greater than zero");
    }

    @Test
    @DisplayName("""
            Given a null application date
            When the applicable price is requested
            Then an InvalidParamsException should be thrown
            """)
    void shouldRejectNullApplicationDate() {

        assertThatThrownBy(() ->
                getApplicablePriceUseCase.findApplicablePrice(
                        VALID_BRAND_ID,
                        VALID_PRODUCT_ID,
                        null))
            .isInstanceOf(InvalidParamsException.class)
            .hasMessage("Application date cannot be null");
    }

    @Test
    @DisplayName("""
            Given an application date exactly equal to START_DATE
            When the applicable price is requested
            Then the price should be applicable
            """)
    void shouldIncludeStartDate() {

        final LocalDateTime applicationDate =
                LocalDateTime.of(2025, 6, 14, 15, 0);

        final Optional<Price> result =
                getApplicablePriceUseCase.findApplicablePrice(
                        VALID_BRAND_ID,
                        VALID_PRODUCT_ID,
                        applicationDate);

        assertThat(result).isPresent();
        assertThat(result.get().tariffId()).isEqualTo(6L);
        assertThat(result.get().endPrice())
                .isEqualByComparingTo(new BigDecimal("10.00"));
    }

    @Test
    @DisplayName("""
            Given an application date exactly equal to END_DATE
            When the applicable price is requested
            Then the price should be applicable
            """)
    void shouldIncludeEndDate() {

        final LocalDateTime applicationDate =
                LocalDateTime.of(2026, 6, 14, 18, 30);

        final Optional<Price> result =
                getApplicablePriceUseCase.findApplicablePrice(
                        VALID_BRAND_ID,
                        VALID_PRODUCT_ID,
                        applicationDate);

        assertThat(result).isPresent();
        assertThat(result.get().tariffId()).isEqualTo(2L);
        assertThat(result.get().endPrice())
                .isEqualByComparingTo(new BigDecimal("25.45"));
    }

    @Test
    @DisplayName("""
            Given two prices applicable at the same time
            When the applicable price is requested
            Then the price with the highest priority should be returned
            """)
    void shouldReturnPriceWithHighestPriority() {

        final LocalDateTime applicationDate =
                LocalDateTime.of(2025, 6, 14, 16, 0);

        final Optional<Price> result =
                getApplicablePriceUseCase.findApplicablePrice(
                        VALID_BRAND_ID,
                        VALID_PRODUCT_ID,
                        applicationDate);

        assertThat(result).isPresent();

        assertThat(result.get().tariffId())
                .isEqualTo(6L);

        assertThat(result.get().priority())
                .isEqualTo(99);

        assertThat(result.get().endPrice())
                .isEqualByComparingTo(new BigDecimal("10.00"));
    }

    @Test
    @DisplayName("""
            Given a price for another brand
            When the applicable price is requested for the requested brand
            Then the price from another brand should not interfere
            """)
    void shouldIgnorePriceFromAnotherBrand() {
        final Optional<Price> result =
                getApplicablePriceUseCase.findApplicablePrice(
                        VALID_BRAND_ID,
                        VALID_PRODUCT_ID,
                        LocalDateTime.of(2025, 6, 14, 16, 0));

        assertThat(result).isPresent();

        assertThat(result.get().brandId())
                .isEqualTo(VALID_BRAND_ID);

        assertThat(result.get().tariffId())
                .isEqualTo(6L);

        assertThat(result.get().endPrice())
                .isEqualByComparingTo(new BigDecimal("10.00"));
    }
}

