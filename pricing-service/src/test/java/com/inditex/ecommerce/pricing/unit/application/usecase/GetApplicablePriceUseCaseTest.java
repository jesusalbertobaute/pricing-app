package com.inditex.ecommerce.pricing.unit.application.usecase;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Stream;

import com.inditex.ecommerce.pricing.application.exception.InvalidParamsException;
import com.inditex.ecommerce.pricing.application.port.output.GetApplicablePriceOutputPort;
import com.inditex.ecommerce.pricing.application.usecase.GetApplicablePriceUseCase;
import com.inditex.ecommerce.pricing.domain.model.Price;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
@DisplayName("Tests for GetApplicablePriceUseCase")
class GetApplicablePriceUseCaseTest {

    private static final Integer BRAND_ID = 1;
    private static final Long PRODUCT_ID = 35455L;
    private static final LocalDateTime APPLICATION_DATE =
            LocalDateTime.of(2026, 6, 14, 16, 0);

    @Mock
    private GetApplicablePriceOutputPort getApplicablePriceOutputPort;

    @Test
    @DisplayName("""
            Given valid brand, product and application date
            And an applicable price exists
            When the price is requested
            Then the applicable price should be returned
            And the output port should be called with the same parameters
            """)
    void shouldReturnApplicablePrice() {

        Price expectedPrice = createPrice();

        when(getApplicablePriceOutputPort.findApplicablePrice(
                BRAND_ID,
                PRODUCT_ID,
                APPLICATION_DATE))
                .thenReturn(Optional.of(expectedPrice));

        GetApplicablePriceUseCase useCase = createUseCase();

        Optional<Price> result = useCase.findApplicablePrice(
                BRAND_ID,
                PRODUCT_ID,
                APPLICATION_DATE);

        assertThat(result)
                .isPresent()
                .contains(expectedPrice);

        verify(getApplicablePriceOutputPort)
                .findApplicablePrice(
                        BRAND_ID,
                        PRODUCT_ID,
                        APPLICATION_DATE);
    }

    @Test
    @DisplayName("""
            Given valid brand, product and application date
            And no applicable price exists
            When the price is requested
            Then an empty result should be returned
            """)
    void shouldReturnEmptyWhenNoApplicablePriceExists() {

        when(getApplicablePriceOutputPort.findApplicablePrice(
                BRAND_ID,
                PRODUCT_ID,
                APPLICATION_DATE))
                .thenReturn(Optional.empty());

        GetApplicablePriceUseCase useCase = createUseCase();

        Optional<Price> result = useCase.findApplicablePrice(
                BRAND_ID,
                PRODUCT_ID,
                APPLICATION_DATE);

        assertThat(result).isEmpty();

        verify(getApplicablePriceOutputPort)
                .findApplicablePrice(
                        BRAND_ID,
                        PRODUCT_ID,
                        APPLICATION_DATE);
    }

    @ParameterizedTest(name = "{0}")
    @MethodSource("invalidParameters")
    @DisplayName("Should reject invalid parameters")
    void shouldRejectInvalidParameters(
            String testCase,
            Integer brandId,
            Long productId,
            LocalDateTime applicationDate,
            String expectedMessage) {

        GetApplicablePriceUseCase useCase = createUseCase();

        assertThatThrownBy(() ->
                useCase.findApplicablePrice(
                        brandId,
                        productId,
                        applicationDate))
                .isInstanceOf(InvalidParamsException.class)
                .hasMessage(expectedMessage);

        verify(getApplicablePriceOutputPort, never())
                .findApplicablePrice(any(), any(), any());
    }

    private static Stream<Arguments> invalidParameters() {
        return Stream.of(
                Arguments.of(
                        "Brand ID is null",
                        null,
                        PRODUCT_ID,
                        APPLICATION_DATE,
                        "Brand ID must be greater than zero"),

                Arguments.of(
                        "Brand ID is zero",
                        0,
                        PRODUCT_ID,
                        APPLICATION_DATE,
                        "Brand ID must be greater than zero"),

                Arguments.of(
                        "Brand ID is negative",
                        -1,
                        PRODUCT_ID,
                        APPLICATION_DATE,
                        "Brand ID must be greater than zero"),

                Arguments.of(
                        "Product ID is null",
                        BRAND_ID,
                        null,
                        APPLICATION_DATE,
                        "Product ID must be greater than zero"),

                Arguments.of(
                        "Product ID is zero",
                        BRAND_ID,
                        0L,
                        APPLICATION_DATE,
                        "Product ID must be greater than zero"),

                Arguments.of(
                        "Product ID is negative",
                        BRAND_ID,
                        -1L,
                        APPLICATION_DATE,
                        "Product ID must be greater than zero"),

                Arguments.of(
                        "Application date is null",
                        BRAND_ID,
                        PRODUCT_ID,
                        null,
                        "Application date cannot be null")
        );
    }

    private GetApplicablePriceUseCase createUseCase() {
        return new GetApplicablePriceUseCase(getApplicablePriceOutputPort);
    }

    private Price createPrice() {
        return new Price(
        		UUID.randomUUID(),
        		BRAND_ID,
        	    APPLICATION_DATE,
                APPLICATION_DATE,
                2L,
                PRODUCT_ID,
                0,
                BigDecimal.valueOf(25.45),
                "EUR",
                LocalDateTime.now()
        );
    }
}