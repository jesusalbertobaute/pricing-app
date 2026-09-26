package com.inditex.ecommerce.pricing.domain.model;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import com.inditex.ecommerce.pricing.domain.exception.DateRangeNotAllowedException;
import com.inditex.ecommerce.pricing.domain.exception.DomainException;

@DisplayName("Tests for the Price Domain Model")
class PriceTest {

    private static final UUID ID = UUID.randomUUID();
    private static final Long BRAND_ID = 1L;
    private static final LocalDateTime START_DATE =
            LocalDateTime.of(2026, 1, 1, 10, 0);
    private static final LocalDateTime END_DATE =
            LocalDateTime.of(2026, 12, 31, 23, 59);
    private static final Long TARIFF_ID = 1L;
    private static final Long PRODUCT_ID = 35455L;
    private static final Integer PRIORITY = 1;
    private static final BigDecimal END_PRICE = new BigDecimal("35.50");
    private static final String CURRENCY_CODE = "EUR";
    private static final LocalDateTime CREATED_AT =
            LocalDateTime.of(2026, 1, 1, 9, 0);

    @Test
    @DisplayName("""
            Given a Price with valid data
            When the Price is created
            Then no exception should be thrown
            """)
    void shouldCreatePriceWhenDataIsValid() {

        assertDoesNotThrow(() -> createPrice());
    }

    @Test
    @DisplayName("""
            Given a Price with a null id
            When the Price is created
            Then a DomainException should be thrown
            """)
    void shouldThrowExceptionWhenIdIsNull() {

        DomainException exception = assertThrows(
                DomainException.class,
                () -> createPrice(
                        null,
                        BRAND_ID,
                        START_DATE,
                        END_DATE,
                        TARIFF_ID,
                        PRODUCT_ID,
                        PRIORITY,
                        END_PRICE,
                        CURRENCY_CODE,
                        CREATED_AT));

        assertEquals("id cannot be null", exception.getMessage());
    }

    @Test
    @DisplayName("""
            Given a Price with a null startDate
            When the Price is created
            Then a DomainException should be thrown
            """)
    void shouldThrowExceptionWhenStartDateIsNull() {

        DomainException exception = assertThrows(
                DomainException.class,
                () -> createPrice(
                        ID,
                        BRAND_ID,
                        null,
                        END_DATE,
                        TARIFF_ID,
                        PRODUCT_ID,
                        PRIORITY,
                        END_PRICE,
                        CURRENCY_CODE,
                        CREATED_AT));

        assertEquals(
                "startDate and endDate cannot be null",
                exception.getMessage());
    }

    @Test
    @DisplayName("""
            Given a Price with a null endDate
            When the Price is created
            Then a DomainException should be thrown
            """)
    void shouldThrowExceptionWhenEndDateIsNull() {

        DomainException exception = assertThrows(
                DomainException.class,
                () -> createPrice(
                        ID,
                        BRAND_ID,
                        START_DATE,
                        null,
                        TARIFF_ID,
                        PRODUCT_ID,
                        PRIORITY,
                        END_PRICE,
                        CURRENCY_CODE,
                        CREATED_AT));

        assertEquals(
                "startDate and endDate cannot be null",
                exception.getMessage());
    }

    @Test
    @DisplayName("""
            Given a Price with a startDate after the endDate
            When the Price is created
            Then a DateRangeNotAllowedException should be thrown
            """)
    void shouldThrowExceptionWhenStartDateIsAfterEndDate() {

        LocalDateTime startDate = END_DATE.plusDays(1);

        DateRangeNotAllowedException exception = assertThrows(
                DateRangeNotAllowedException.class,
                () -> createPrice(
                        ID,
                        BRAND_ID,
                        startDate,
                        END_DATE,
                        TARIFF_ID,
                        PRODUCT_ID,
                        PRIORITY,
                        END_PRICE,
                        CURRENCY_CODE,
                        CREATED_AT));

        assertEquals(
                "startDate cannot be after endDate",
                exception.getMessage());
    }

    @Test
    @DisplayName("""
            Given a Price with a null tariffId
            When the Price is created
            Then a DomainException should be thrown
            """)
    void shouldThrowExceptionWhenTariffIdIsNull() {

        DomainException exception = assertThrows(
                DomainException.class,
                () -> createPrice(
                        ID,
                        BRAND_ID,
                        START_DATE,
                        END_DATE,
                        null,
                        PRODUCT_ID,
                        PRIORITY,
                        END_PRICE,
                        CURRENCY_CODE,
                        CREATED_AT));

        assertEquals(
                "tariffId cannot be null or less than or equal to zero",
                exception.getMessage());
    }

    @Test
    @DisplayName("""
            Given a Price with a tariffId equal to zero
            When the Price is created
            Then a DomainException should be thrown
            """)
    void shouldThrowExceptionWhenTariffIdIsZero() {

        DomainException exception = assertThrows(
                DomainException.class,
                () -> createPrice(
                        ID,
                        BRAND_ID,
                        START_DATE,
                        END_DATE,
                        0L,
                        PRODUCT_ID,
                        PRIORITY,
                        END_PRICE,
                        CURRENCY_CODE,
                        CREATED_AT));

        assertEquals(
                "tariffId cannot be null or less than or equal to zero",
                exception.getMessage());
    }

    @Test
    @DisplayName("""
            Given a Price with a negative tariffId
            When the Price is created
            Then a DomainException should be thrown
            """)
    void shouldThrowExceptionWhenTariffIdIsNegative() {

        DomainException exception = assertThrows(
                DomainException.class,
                () -> createPrice(
                        ID,
                        BRAND_ID,
                        START_DATE,
                        END_DATE,
                        -1L,
                        PRODUCT_ID,
                        PRIORITY,
                        END_PRICE,
                        CURRENCY_CODE,
                        CREATED_AT));

        assertEquals(
                "tariffId cannot be null or less than or equal to zero",
                exception.getMessage());
    }

    @Test
    @DisplayName("""
            Given a Price with a null productId
            When the Price is created
            Then a DomainException should be thrown
            """)
    void shouldThrowExceptionWhenProductIdIsNull() {

        DomainException exception = assertThrows(
                DomainException.class,
                () -> createPrice(
                        ID,
                        BRAND_ID,
                        START_DATE,
                        END_DATE,
                        TARIFF_ID,
                        null,
                        PRIORITY,
                        END_PRICE,
                        CURRENCY_CODE,
                        CREATED_AT));

        assertEquals(
                "productId cannot be null or less than or equal to zero",
                exception.getMessage());
    }

    @Test
    @DisplayName("""
            Given a Price with a productId equal to zero
            When the Price is created
            Then a DomainException should be thrown
            """)
    void shouldThrowExceptionWhenProductIdIsZero() {

        DomainException exception = assertThrows(
                DomainException.class,
                () -> createPrice(
                        ID,
                        BRAND_ID,
                        START_DATE,
                        END_DATE,
                        TARIFF_ID,
                        0L,
                        PRIORITY,
                        END_PRICE,
                        CURRENCY_CODE,
                        CREATED_AT));

        assertEquals(
                "productId cannot be null or less than or equal to zero",
                exception.getMessage());
    }

    @Test
    @DisplayName("""
            Given a Price with a null priority
            When the Price is created
            Then a DomainException should be thrown
            """)
    void shouldThrowExceptionWhenPriorityIsNull() {

        DomainException exception = assertThrows(
                DomainException.class,
                () -> createPrice(
                        ID,
                        BRAND_ID,
                        START_DATE,
                        END_DATE,
                        TARIFF_ID,
                        PRODUCT_ID,
                        null,
                        END_PRICE,
                        CURRENCY_CODE,
                        CREATED_AT));

        assertEquals(
                "priority cannot be null or less than zero",
                exception.getMessage());
    }

    @Test
    @DisplayName("""
            Given a Price with a negative priority
            When the Price is created
            Then a DomainException should be thrown
            """)
    void shouldThrowExceptionWhenPriorityIsNegative() {

        DomainException exception = assertThrows(
                DomainException.class,
                () -> createPrice(
                        ID,
                        BRAND_ID,
                        START_DATE,
                        END_DATE,
                        TARIFF_ID,
                        PRODUCT_ID,
                        -1,
                        END_PRICE,
                        CURRENCY_CODE,
                        CREATED_AT));

        assertEquals(
                "priority cannot be null or less than zero",
                exception.getMessage());
    }

    @Test
    @DisplayName("""
            Given a Price with a priority equal to zero
            When the Price is created
            Then the Price should be created successfully
            """)
    void shouldCreatePriceWhenPriorityIsZero() {

        assertDoesNotThrow(() -> createPrice(
                ID,
                BRAND_ID,
                START_DATE,
                END_DATE,
                TARIFF_ID,
                PRODUCT_ID,
                0,
                END_PRICE,
                CURRENCY_CODE,
                CREATED_AT));
    }

    @Test
    @DisplayName("""
            Given a Price with a null endPrice
            When the Price is created
            Then a DomainException should be thrown
            """)
    void shouldThrowExceptionWhenEndPriceIsNull() {

        DomainException exception = assertThrows(
                DomainException.class,
                () -> createPrice(
                        ID,
                        BRAND_ID,
                        START_DATE,
                        END_DATE,
                        TARIFF_ID,
                        PRODUCT_ID,
                        PRIORITY,
                        null,
                        CURRENCY_CODE,
                        CREATED_AT));

        assertEquals(
                "endPrice must be greater than zero",
                exception.getMessage());
    }

    @Test
    @DisplayName("""
            Given a Price with an endPrice equal to zero
            When the Price is created
            Then a DomainException should be thrown
            """)
    void shouldThrowExceptionWhenEndPriceIsZero() {

        DomainException exception = assertThrows(
                DomainException.class,
                () -> createPrice(
                        ID,
                        BRAND_ID,
                        START_DATE,
                        END_DATE,
                        TARIFF_ID,
                        PRODUCT_ID,
                        PRIORITY,
                        BigDecimal.ZERO,
                        CURRENCY_CODE,
                        CREATED_AT));

        assertEquals(
                "endPrice must be greater than zero",
                exception.getMessage());
    }

    @Test
    @DisplayName("""
            Given a Price with a negative endPrice
            When the Price is created
            Then a DomainException should be thrown
            """)
    void shouldThrowExceptionWhenEndPriceIsNegative() {

        DomainException exception = assertThrows(
                DomainException.class,
                () -> createPrice(
                        ID,
                        BRAND_ID,
                        START_DATE,
                        END_DATE,
                        TARIFF_ID,
                        PRODUCT_ID,
                        PRIORITY,
                        new BigDecimal("-1.00"),
                        CURRENCY_CODE,
                        CREATED_AT));

        assertEquals(
                "endPrice must be greater than zero",
                exception.getMessage());
    }

    @Test
    @DisplayName("""
            Given a Price with a null currencyCode
            When the Price is created
            Then a DomainException should be thrown
            """)
    void shouldThrowExceptionWhenCurrencyCodeIsNull() {

        DomainException exception = assertThrows(
                DomainException.class,
                () -> createPrice(
                        ID,
                        BRAND_ID,
                        START_DATE,
                        END_DATE,
                        TARIFF_ID,
                        PRODUCT_ID,
                        PRIORITY,
                        END_PRICE,
                        null,
                        CREATED_AT));

        assertEquals(
                "currencyCode must be a valid 3-letter uppercase code",
                exception.getMessage());
    }

    @Test
    @DisplayName("""
            Given a Price with a blank currencyCode
            When the Price is created
            Then a DomainException should be thrown
            """)
    void shouldThrowExceptionWhenCurrencyCodeIsBlank() {

        DomainException exception = assertThrows(
                DomainException.class,
                () -> createPrice(
                        ID,
                        BRAND_ID,
                        START_DATE,
                        END_DATE,
                        TARIFF_ID,
                        PRODUCT_ID,
                        PRIORITY,
                        END_PRICE,
                        "   ",
                        CREATED_AT));

        assertEquals(
                "currencyCode must be a valid 3-letter uppercase code",
                exception.getMessage());
    }

    @Test
    @DisplayName("""
            Given a Price with a currencyCode shorter than three characters
            When the Price is created
            Then a DomainException should be thrown
            """)
    void shouldThrowExceptionWhenCurrencyCodeHasLessThanThreeCharacters() {

        DomainException exception = assertThrows(
                DomainException.class,
                () -> createPrice(
                        ID,
                        BRAND_ID,
                        START_DATE,
                        END_DATE,
                        TARIFF_ID,
                        PRODUCT_ID,
                        PRIORITY,
                        END_PRICE,
                        "EU",
                        CREATED_AT));

        assertEquals(
                "currencyCode must be a valid 3-letter uppercase code",
                exception.getMessage());
    }

    @Test
    @DisplayName("""
            Given a Price with a currencyCode longer than three characters
            When the Price is created
            Then a DomainException should be thrown
            """)
    void shouldThrowExceptionWhenCurrencyCodeHasMoreThanThreeCharacters() {

        DomainException exception = assertThrows(
                DomainException.class,
                () -> createPrice(
                        ID,
                        BRAND_ID,
                        START_DATE,
                        END_DATE,
                        TARIFF_ID,
                        PRODUCT_ID,
                        PRIORITY,
                        END_PRICE,
                        "EURO",
                        CREATED_AT));

        assertEquals(
                "currencyCode must be a valid 3-letter uppercase code",
                exception.getMessage());
    }

    @Test
    @DisplayName("""
            Given a Price with a lowercase currencyCode
            When the Price is created
            Then a DomainException should be thrown
            """)
    void shouldThrowExceptionWhenCurrencyCodeIsNotUppercase() {

        DomainException exception = assertThrows(
                DomainException.class,
                () -> createPrice(
                        ID,
                        BRAND_ID,
                        START_DATE,
                        END_DATE,
                        TARIFF_ID,
                        PRODUCT_ID,
                        PRIORITY,
                        END_PRICE,
                        "eur",
                        CREATED_AT));

        assertEquals(
                "currencyCode must be a valid 3-letter uppercase code",
                exception.getMessage());
    }

    @Test
    @DisplayName("""
            Given a Price with a null createdAt
            When the Price is created
            Then a DomainException should be thrown
            """)
    void shouldThrowExceptionWhenCreatedAtIsNull() {

        DomainException exception = assertThrows(
                DomainException.class,
                () -> createPrice(
                        ID,
                        BRAND_ID,
                        START_DATE,
                        END_DATE,
                        TARIFF_ID,
                        PRODUCT_ID,
                        PRIORITY,
                        END_PRICE,
                        CURRENCY_CODE,
                        null));

        assertEquals(
                "createdAt cannot be null",
                exception.getMessage());
    }

    @Test
    @DisplayName("""
            Given a Price whose application period starts at START_DATE
            When the Price is queried at START_DATE
            Then the Price should be applicable
            """)
    void shouldReturnTrueWhenPriceAppliesAtStartDate() {

        Price price = createPrice();

        assertTrue(price.appliesAt(START_DATE));
    }

    @Test
    @DisplayName("""
            Given a Price whose application period ends at END_DATE
            When the Price is queried at END_DATE
            Then the Price should be applicable
            """)
    void shouldReturnTrueWhenPriceAppliesAtEndDate() {

        Price price = createPrice();

        assertTrue(price.appliesAt(END_DATE));
    }

    @Test
    @DisplayName("""
            Given a Price with an active application period
            When the Price is queried inside that period
            Then the Price should be applicable
            """)
    void shouldReturnTrueWhenPriceAppliesInsideDateRange() {

        Price price = createPrice();

        LocalDateTime applicationDate =
                START_DATE.plusDays(10);

        assertTrue(price.appliesAt(applicationDate));
    }

    @Test
    @DisplayName("""
            Given a Price whose application period starts at START_DATE
            When the Price is queried before START_DATE
            Then the Price should not be applicable
            """)
    void shouldReturnFalseWhenApplicationDateIsBeforeStartDate() {

        Price price = createPrice();

        LocalDateTime applicationDate =
                START_DATE.minusSeconds(1);

        assertFalse(price.appliesAt(applicationDate));
    }

    @Test
    @DisplayName("""
            Given a Price whose application period ends at END_DATE
            When the Price is queried after END_DATE
            Then the Price should not be applicable
            """)
    void shouldReturnFalseWhenApplicationDateIsAfterEndDate() {

        Price price = createPrice();

        LocalDateTime applicationDate =
                END_DATE.plusSeconds(1);

        assertFalse(price.appliesAt(applicationDate));
    }

    @Test
    @DisplayName("""
            Given a Price with a higher priority than another Price
            When their priorities are compared
            Then the first Price should have higher priority
            """)
    void shouldReturnTrueWhenPriceHasHigherPriority() {

        Price price = createPrice();

        Price other = createPrice(
                UUID.randomUUID(),
                BRAND_ID,
                START_DATE,
                END_DATE,
                TARIFF_ID,
                PRODUCT_ID,
                0,
                END_PRICE,
                CURRENCY_CODE,
                CREATED_AT);

        assertTrue(price.hasHigherPriorityThan(other));
    }

    @Test
    @DisplayName("""
            Given a Price with a lower priority than another Price
            When their priorities are compared
            Then the first Price should not have higher priority
            """)
    void shouldReturnFalseWhenPriceHasLowerPriority() {

        Price price = createPrice(
                ID,
                BRAND_ID,
                START_DATE,
                END_DATE,
                TARIFF_ID,
                PRODUCT_ID,
                0,
                END_PRICE,
                CURRENCY_CODE,
                CREATED_AT);

        Price other = createPrice();

        assertFalse(price.hasHigherPriorityThan(other));
    }

    @Test
    @DisplayName("""
            Given two Prices with the same priority
            When their priorities are compared
            Then the first Price should not have higher priority
            """)
    void shouldReturnFalseWhenPricesHaveSamePriority() {

        Price price = createPrice();

        Price other = createPrice(
                UUID.randomUUID(),
                BRAND_ID,
                START_DATE,
                END_DATE,
                TARIFF_ID,
                PRODUCT_ID,
                PRIORITY,
                END_PRICE,
                CURRENCY_CODE,
                CREATED_AT);

        assertFalse(price.hasHigherPriorityThan(other));
        assertFalse(other.hasHigherPriorityThan(price));
    }

    private Price createPrice() {
        return createPrice(
                ID,
                BRAND_ID,
                START_DATE,
                END_DATE,
                TARIFF_ID,
                PRODUCT_ID,
                PRIORITY,
                END_PRICE,
                CURRENCY_CODE,
                CREATED_AT);
    }

    private Price createPrice(
            UUID id,
            Long brandId,
            LocalDateTime startDate,
            LocalDateTime endDate,
            Long tariffId,
            Long productId,
            Integer priority,
            BigDecimal endPrice,
            String currencyCode,
            LocalDateTime createdAt) {

        return new Price(
                id,
                brandId,
                startDate,
                endDate,
                tariffId,
                productId,
                priority,
                endPrice,
                currencyCode,
                createdAt);
    }
}
