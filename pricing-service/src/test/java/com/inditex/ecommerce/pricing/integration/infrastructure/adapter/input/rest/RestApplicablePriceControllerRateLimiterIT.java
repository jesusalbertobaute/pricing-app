package com.inditex.ecommerce.pricing.integration.infrastructure.adapter.input.rest;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.time.LocalDateTime;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

@SpringBootTest
@AutoConfigureMockMvc
@Tag("Integration")
@ActiveProfiles("integration-ratelimiter")
@DisplayName("Tests for the Rest Controller RateLimiter")
public class RestApplicablePriceControllerRateLimiterIT {
	@Autowired
	private MockMvc mockMvc;

	private final String URL_PATH = "/ecommerce/price";

	@Test
	@DisplayName("""
			Given a rate limited endpoint
			When more than the allowed number of requests are made
			Then status 429 Too Many Requests should be returned
			""")
	void shouldReturnTooManyRequestsWhenRateLimitIsExceeded() throws Exception {

		final Integer brandId = 1;
		final Long productId = 35455L;
		final LocalDateTime applicationDate = LocalDateTime.of(2026, 6, 14, 10, 0);

		for (int i = 0; i < 2; i++) {
			mockMvc.perform(get(URL_PATH).param("brandId", brandId.toString()).param("productId", productId.toString())
					.param("applicationDate", applicationDate.toString())).andExpect(status().isOk());
		}

		mockMvc.perform(get(URL_PATH).param("brandId", brandId.toString()).param("productId", productId.toString())
				.param("applicationDate", applicationDate.toString())).andExpect(status().isTooManyRequests());

	}
}
