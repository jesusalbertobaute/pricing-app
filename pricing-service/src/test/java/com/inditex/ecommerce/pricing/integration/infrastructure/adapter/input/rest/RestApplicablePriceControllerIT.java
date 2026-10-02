package com.inditex.ecommerce.pricing.integration.infrastructure.adapter.input.rest;

import static org.junit.jupiter.params.provider.Arguments.arguments;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.stream.Stream;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.web.servlet.MockMvc;

@SpringBootTest
@AutoConfigureMockMvc
@Tag("Integration")
@ActiveProfiles("integration")
@TestPropertySource(properties = {
		"spring.datasource.url=jdbc:h2:mem:controller-testdb"
})
@DisplayName("Tests for RestApplicablePriceController")
public class RestApplicablePriceControllerIT {

	@Autowired
	private MockMvc mockMvc;

	private final String URL_PATH = "/v2/ecommerce/price";

	@ParameterizedTest(name = "{0}")
	@MethodSource("applicablePriceCases")
	@DisplayName("Should return the applicable price")
	void shouldReturnApplicablePrice(
			String testCase,
			LocalDateTime applicationDate,
			Long expectedTariff,
			BigDecimal expectedPrice) throws Exception {

		final Integer brandId = 1;
		final Long productId = 35455L;

		mockMvc.perform(get(URL_PATH)
				.param("brandId", brandId.toString())
				.param("productId", productId.toString())
				.param("applicationDate", applicationDate.toString()))
		.andExpect(status().isOk())
		.andExpect(jsonPath("$.productId").value(productId))
		.andExpect(jsonPath("$.brandId").value(brandId))
		.andExpect(jsonPath("$.tariffId").value(expectedTariff))
		.andExpect(jsonPath("$.price").value(expectedPrice.doubleValue()))
		.andExpect(jsonPath("$.currencyCode").value("EUR"));
	}
	
	@ParameterizedTest(name = "{0}")
	@MethodSource("priceDateTransitionCases")
	@DisplayName("Should select the correct price around date boundaries")
	void shouldSelectCorrectPriceAroundDateBoundaries(
	        String testCase,
	        LocalDateTime applicationDate,
	        Long expectedTariff,
	        BigDecimal expectedPrice) throws Exception {

	    final Integer brandId = 1;
	    final Long productId = 35455L;

	    mockMvc.perform(get(URL_PATH)
	            .param("brandId", brandId.toString())
	            .param("productId", productId.toString())
	            .param("applicationDate", applicationDate.toString()))
	        .andExpect(status().isOk())
	        .andExpect(jsonPath("$.tariffId").value(expectedTariff))
	        .andExpect(jsonPath("$.price").value(expectedPrice.doubleValue()));
	}
	
	@Test
	@DisplayName("""
			 Given a product and brand with an applicable price date
             When the application date is exactly at the end of the pricing period
             Then the applicable price should be returned
			""")
	void shouldReturnPriceAtExactEndOfPricingPeriod() throws Exception {

	    mockMvc.perform(get(URL_PATH)
	            .param("brandId", "1")
	            .param("productId", "35455")
	            .param("applicationDate", "2026-12-31T23:59:59"))
	        .andExpect(status().isOk())
	        .andExpect(jsonPath("$.productId").value(35455))
	        .andExpect(jsonPath("$.brandId").value(1))
	        .andExpect(jsonPath("$.tariffId").value(4))
	        .andExpect(jsonPath("$.price").value(38.95))
	        .andExpect(jsonPath("$.currencyCode").value("EUR"));
	}
	
	@Test
	@DisplayName("""
			Given a product and brand  with a pricing period ending on December 31st 2026 at 23:59:59
            When the application date is January 1st 2027 at 00:00:00
            Then no applicable price should be found
			     """)
	void shouldReturnNotFoundAfterEndOfPricingPeriod() throws Exception {

	    mockMvc.perform(get(URL_PATH)
	            .param("brandId", "1")
	            .param("productId", "35455")
	            .param("applicationDate", "2027-01-01T00:00:00"))
	        .andExpect(status().isNotFound());
	}

	@Test
	@DisplayName("""
			Given a brand, a product and an application date matching an applicable price date
			When the applicable price is not found 
			      Then a 404 Not Found response should be returned 
			      And the response should contain the status, detail
			   """)
	void shouldReturnNotFoundError() throws Exception {
		final Integer brandId = 1;
		final Long productId = 35455L;
		final LocalDateTime applicationDate =
				LocalDateTime.of(2022, 6, 14, 10, 0);

		mockMvc.perform(get(URL_PATH)
				.param("brandId", brandId.toString())
				.param("productId", productId.toString())
				.param("applicationDate", applicationDate.toString()))
		.andExpect(status().isNotFound())
		.andExpect(jsonPath("$.type").exists())
		.andExpect(jsonPath("$.status").exists())
		.andExpect(jsonPath("$.title").exists())
		.andExpect(jsonPath("$.detail").exists());
	}

	@Test
	@DisplayName("""
			Given a brand, a product and an application date matching an applicable price date
			When a parameter doesn't accept
			      Then a 400 Bad Request response should be returned 
			      And the response should contain the status, detail
			   """)
	void shouldReturnBadRequestError() throws Exception {
		final Integer brandId = 1;
		final Long productId = 35455L;
		final LocalDateTime applicationDate =
				LocalDateTime.of(2022, 6, 14, 10, 0);

		mockMvc.perform(get(URL_PATH)
				.param("brandId", brandId.toString())
				.param("product", productId.toString())
				.param("applicationDate", applicationDate.toString()))
		.andExpect(status().isBadRequest())
		.andExpect(jsonPath("$.type").exists())
		.andExpect(jsonPath("$.status").exists())
		.andExpect(jsonPath("$.title").exists())
		.andExpect(jsonPath("$.detail").exists());
	}

	@Test
	@DisplayName("""
			Given a brand,a product, an application date matching an applicable price date
			When the parameter product is missing
			      Then a 400 Bad Request response should be returned 
			      And the response should contain the status, detail
			   """)
	void shouldReturnBadRequestMissingParameterError() throws Exception {
		final Integer brandId = 1;
		final LocalDateTime applicationDate =
				LocalDateTime.of(2022, 6, 14, 10, 0);

		mockMvc.perform(get(URL_PATH)
				.param("brandId", brandId.toString())
				.param("applicationDate", applicationDate.toString()))
		.andExpect(status().isBadRequest())
		.andExpect(jsonPath("$.type").exists())
		.andExpect(jsonPath("$.status").exists())
		.andExpect(jsonPath("$.title").exists())
		.andExpect(jsonPath("$.detail").exists());
	}

	@Test
	@DisplayName("""
			Given a brand,a product, an application date matching an applicable price date
			When the parameter product do not have the correct name
			      Then a 400 Bad Request response should be returned 
			      And the response should contain the status, detail 
			   """)
	void shouldReturnBadRequestMethodArgumentNotValidError() throws Exception {
		final Integer brandId = 1;
		final LocalDateTime applicationDate =
				LocalDateTime.of(2022, 6, 14, 10, 0);

		mockMvc.perform(get(URL_PATH)
				.param("brandId", brandId.toString())
				.param("product", "test")
				.param("applicationDate", applicationDate.toString()))
		.andExpect(status().isBadRequest())
		.andExpect(jsonPath("$.type").exists())
		.andExpect(jsonPath("$.status").exists())
		.andExpect(jsonPath("$.title").exists())
		.andExpect(jsonPath("$.detail").exists());
	}

	@Test
	@DisplayName("""
			Given a brand,a product, an application date
			When the applicationDate is not a valid date
			      Then a 400 Bad Request response should be returned 
			      And the response should contain the status, detail
			   """)
	void shouldReturnBadRequestDateNotValidError() throws Exception {
		final Integer brandId = 1;
		final Long productId = 35455L;

		mockMvc.perform(get(URL_PATH)
				.param("brandId", brandId.toString())
				.param("product", productId.toString())
				.param("applicationDate", "29319"))
		.andExpect(status().isBadRequest())
		.andExpect(jsonPath("$.type").exists())
		.andExpect(jsonPath("$.status").exists())
		.andExpect(jsonPath("$.title").exists())
		.andExpect(jsonPath("$.detail").exists());
	}

	@Test
	@DisplayName("""
			Given a brand,a product, an application date
			When the applicationDate is not a valid date format
			      Then a 400 Bad Request response should be returned 
			      And the response should contain the status, detail
			   """)
	void shouldReturnBadRequestDateNotValidFormatError() throws Exception {
		final Integer brandId = 1;
		final Long productId = 35455L;

		mockMvc.perform(get(URL_PATH)
				.param("brandId", brandId.toString())
				.param("product", productId.toString())
				.param("applicationDate", "2020-06-14"))
		.andExpect(status().isBadRequest())
		.andExpect(jsonPath("$.type").exists())
		.andExpect(jsonPath("$.status").exists())
		.andExpect(jsonPath("$.title").exists())
		.andExpect(jsonPath("$.detail").exists());
	}

	@Test
	@DisplayName("""
			Given a brand,a product, an application date
			When the brandId is empty
			      Then a 400 Bad Request response should be returned 
			      And the response should contain the status, detail
			   """)
	void shouldReturnBadRequestEmptyError() throws Exception {
		final Long productId = 35455L;
		final LocalDateTime applicationDate =
				LocalDateTime.of(2022, 6, 14, 10, 0);

		mockMvc.perform(get(URL_PATH)
				.param("brandId", "")
				.param("product", productId.toString())
				.param("applicationDate", applicationDate.toString()))
		.andExpect(status().isBadRequest())
		.andExpect(jsonPath("$.type").exists())
		.andExpect(jsonPath("$.status").exists())
		.andExpect(jsonPath("$.title").exists())
		.andExpect(jsonPath("$.detail").exists());
	}

	@Test
	@DisplayName("""
			Given a brand,a product, an application date
			When the brandId is null
			      Then a 400 Bad Request response should be returned 
			      And the response should contain the status, detail
			   """)
	void shouldReturnBadRequestNullError() throws Exception {
		final Long productId = 35455L;

		mockMvc.perform(get(URL_PATH)
				.param("brandId", "null")
				.param("product", productId.toString())
				.param("applicationDate", "29319"))
		.andExpect(status().isBadRequest())
		.andExpect(jsonPath("$.type").exists())
		.andExpect(jsonPath("$.status").exists())
		.andExpect(jsonPath("$.title").exists())
		.andExpect(jsonPath("$.detail").exists());
	}

	private static Stream<Arguments> applicablePriceCases() {
		return Stream.of(
				arguments(
						"Request at 10:00 on the 14th for product 35455 for brand 1 (ZARA)",
						LocalDateTime.of(2026, 6, 14, 10, 0),
						1L,
						new BigDecimal("35.50")
						),
				arguments(
						"Request at 16:00 on the 14th for product 35455 for brand 1 (ZARA)",
						LocalDateTime.of(2026, 6, 14, 16, 0),
						2L,
						new BigDecimal("25.45")
						),
				arguments(
						"Request at 21:00 on the 14th for product 35455 for brand 1 (ZARA)",
						LocalDateTime.of(2026, 6, 14, 21, 0),
						1L,
						new BigDecimal("35.50")
						),
				arguments(
						"Request at 10:00 on the 15th for product 35455 for brand 1 (ZARA)",
						LocalDateTime.of(2026, 6, 15, 10, 0),
						3L,
						new BigDecimal("30.50")
						),
				arguments(
						"Request at 21:00 on the 16th for product 35455 for brand 1 (ZARA)",
						LocalDateTime.of(2026, 6, 16, 21, 0),
						4L,
						new BigDecimal("38.95")
						),
				arguments(
						"Request exactly at the start date of tariff 2",
						LocalDateTime.of(2026, 6, 14, 15, 0),
						2L,
						new BigDecimal("25.45")
						),
				arguments(
						"Request exactly at the end date of tariff 2",
						LocalDateTime.of(2026, 6, 14, 18, 30),
						2L,
						new BigDecimal("25.45")
						),
				arguments(
						"Request exactly at the start date of tariff 3",
						LocalDateTime.of(2026, 6, 15, 0, 0),
						3L,
						new BigDecimal("30.50")
						),
				arguments(
						"Request exactly at the end date of tariff 3",
						LocalDateTime.of(2026, 6, 15, 11, 0),
						3L,
						new BigDecimal("30.50")
						),
				arguments(
						"Request exactly at the start date of tariff 4",
						LocalDateTime.of(2026, 6, 15, 16, 0),
						4L,
						new BigDecimal("38.95")
						),
				arguments(
						"Request exactly at the end date of tariff 4",
						LocalDateTime.of(2026, 12, 31, 23, 59, 59),
						4L,
						new BigDecimal("38.95")
						)
				);
	}
	
	private static Stream<Arguments> priceDateTransitionCases() {
	    return Stream.of(
	        arguments(
	            "One second before tariff 2 starts",
	            LocalDateTime.of(2026, 6, 14, 14, 59, 59),
	            1L,
	            new BigDecimal("35.50")
	        ),
	        arguments(
	            "Exactly when tariff 2 starts",
	            LocalDateTime.of(2026, 6, 14, 15, 0),
	            2L,
	            new BigDecimal("25.45")
	        ),
	        arguments(
	            "Exactly when tariff 2 ends",
	            LocalDateTime.of(2026, 6, 14, 18, 30),
	            2L,
	            new BigDecimal("25.45")
	        ),
	        arguments(
	            "One second after tariff 2 ends",
	            LocalDateTime.of(2026, 6, 14, 18, 30, 1),
	            1L,
	            new BigDecimal("35.50")
	        )
	    );
	}


}
