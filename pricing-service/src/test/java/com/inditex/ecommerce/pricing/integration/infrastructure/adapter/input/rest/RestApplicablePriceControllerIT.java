package com.inditex.ecommerce.pricing.integration.infrastructure.adapter.input.rest;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.time.LocalDateTime;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
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
	
	private final String URL_PATH = "/ecommerce/price";

    @Test
    @DisplayName("""
    		        Given a brand, a product and an application date matching an applicable price date
    		        When the applicable price date is requested 
                    Then a 200 OK response should be returned 
                    And the response should contain the productId, brandId, tariffId, startDate, endDate, price and currencyCode 
                 """)
    void shouldReturnApplicablePrice() throws Exception {
        final Integer brandId = 1;
        final Long productId = 35455L;
        final LocalDateTime applicationDate =
                LocalDateTime.of(2026, 6, 14, 10, 0);
        
        mockMvc.perform(get(URL_PATH)
                .param("brandId", brandId.toString())
                .param("productId", productId.toString())
                .param("applicationDate", applicationDate.toString()))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.productId").exists())
            .andExpect(jsonPath("$.brandId").exists())
            .andExpect(jsonPath("$.tariffId").exists())
            .andExpect(jsonPath("$.startDate").exists())
            .andExpect(jsonPath("$.endDate").exists())
            .andExpect(jsonPath("$.price").exists())
            .andExpect(jsonPath("$.currencyCode").exists());
    }
    
    @Test
    @DisplayName("""
            Given a product and brand with an applicable price date
            When the brand is 1 (ZARA)
            And the product is 35455
            And the applicable price date is requested on June 14th 2026 at 10:00
            Then tariff 1 with a price of 35.50 EUR should be returned
            """)
    void shouldReturnTariff1ForJune14At10() throws Exception {

        final Integer brandId = 1;
        final Long productId = 35455L;
        final double price = 35.50;
        final Long tariff = 1L; 
        final LocalDateTime applicationDate =
                LocalDateTime.of(2026, 6, 14, 10, 0);

        mockMvc.perform(get(URL_PATH)
                .param("brandId", brandId.toString())
                .param("productId", productId.toString())
                .param("applicationDate", applicationDate.toString()))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.productId").value(productId))
            .andExpect(jsonPath("$.brandId").value(brandId))
            .andExpect(jsonPath("$.tariffId").value(tariff))
            .andExpect(jsonPath("$.price").value(price))
            .andExpect(jsonPath("$.currencyCode").value("EUR"));
    }

    @Test
    @DisplayName("""
            Given a product and brand with an applicable price date
            When the brand is 1 (ZARA)
            And the product is 35455
            And the applicable price date is requested on June 14th 2026 at 16:00
            Then tariff 2 with a price of 25.45 EUR should be returned
            """)
    void shouldReturnTariff2ForJune14At16() throws Exception {

        final Integer brandId = 1;
        final Long productId = 35455L;
        final double price = 25.45;
        final Long tariff = 2L;
        final LocalDateTime applicationDate =
                LocalDateTime.of(2026, 6, 14, 16, 0);

        mockMvc.perform(get(URL_PATH)
                .param("brandId", brandId.toString())
                .param("productId", productId.toString())
                .param("applicationDate", applicationDate.toString()))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.productId").value(productId))
            .andExpect(jsonPath("$.brandId").value(brandId))
            .andExpect(jsonPath("$.tariffId").value(tariff))
            .andExpect(jsonPath("$.price").value(price))
            .andExpect(jsonPath("$.currencyCode").value("EUR"));
    }

    @Test
    @DisplayName("""
            Given a product and brand with an applicable price date
            When the brand is 1 (ZARA)
            And the product is 35455
            And the applicable price date is requested on June 14th 2026 at 21:00
            Then tariff 1 with a price of 35.50 EUR should be returned
            """)
    void shouldReturnTariff1ForJune14At21() throws Exception {

        final Integer brandId = 1;
        final Long productId = 35455L;
        final double price = 35.50;
        final Long tariff = 1L;
        final LocalDateTime applicationDate =
                LocalDateTime.of(2026, 6, 14, 21, 0);

        mockMvc.perform(get(URL_PATH)
                .param("brandId", brandId.toString())
                .param("productId", productId.toString())
                .param("applicationDate", applicationDate.toString()))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.productId").value(productId))
            .andExpect(jsonPath("$.brandId").value(brandId))
            .andExpect(jsonPath("$.tariffId").value(tariff))
            .andExpect(jsonPath("$.price").value(price))
            .andExpect(jsonPath("$.currencyCode").value("EUR"));
    }

    @Test
    @DisplayName("""
            Given a product and brand with an applicable price date
            When the brand is 1 (ZARA)
            And the product is 35455
            And the applicable price date is requested on June 15th 2026 at 10:00
            Then tariff 3 with a price of 30.50 EUR should be returned
            """)
    void shouldReturnTariff3ForJune15At10() throws Exception {

        final Integer brandId = 1;
        final Long productId = 35455L;
        final double price = 30.50;
        final Long tariff = 3L;
        final LocalDateTime applicationDate =
                LocalDateTime.of(2026, 6, 15, 10, 0);

        mockMvc.perform(get(URL_PATH)
                .param("brandId", brandId.toString())
                .param("productId", productId.toString())
                .param("applicationDate", applicationDate.toString()))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.productId").value(productId))
            .andExpect(jsonPath("$.brandId").value(brandId))
            .andExpect(jsonPath("$.tariffId").value(tariff))
            .andExpect(jsonPath("$.price").value(price))
            .andExpect(jsonPath("$.currencyCode").value("EUR"));
    }

    @Test
    @DisplayName("""
            Given a product and brand with an applicable price date
            When the brand is 1 (ZARA)
            And the product is 35455
            And the applicable price date is requested on June 16th 2026 at 21:00
            Then tariff 4 with a price of 38.95 EUR should be returned
            """)
    void shouldReturnTariff4ForJune16At21() throws Exception {

        final Integer brandId = 1;
        final Long productId = 35455L;
        final double price = 38.95;
        final Long tariff = 4L;
        final LocalDateTime applicationDate =
                LocalDateTime.of(2026, 6, 16, 21, 0);

        mockMvc.perform(get(URL_PATH)
                .param("brandId", brandId.toString())
                .param("productId", productId.toString())
                .param("applicationDate", applicationDate.toString()))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.productId").value(productId))
            .andExpect(jsonPath("$.brandId").value(brandId))
            .andExpect(jsonPath("$.tariffId").value(tariff))
            .andExpect(jsonPath("$.price").value(price))
            .andExpect(jsonPath("$.currencyCode").value("EUR"));
    }
    
    @Test
    @DisplayName("""
    		        Given a brand, a product and an application date matching an applicable price date
    		        When the applicable price is not found 
                    Then a 404 Not Found response should be returned 
                    And the response should contain the code, detail 
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
            .andExpect(jsonPath("$.code").exists())
            .andExpect(jsonPath("$.detail").exists());
    }
    
    @Test
    @DisplayName("""
    		        Given a brand, a product and an application date matching an applicable price date
    		        When a parameter doesn't accept
                    Then a 400 Bad Request response should be returned 
                    And the response should contain the code, detail 
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
            .andExpect(jsonPath("$.code").exists())
            .andExpect(jsonPath("$.detail").exists());
    }
    
    @Test
    @DisplayName("""
    		        Given a brand,a product, an application date matching an applicable price date
    		        When the parameter product is missing
                    Then a 400 Bad Request response should be returned 
                    And the response should contain the code, detail 
                 """)
    void shouldReturnBadRequestMissingParameterError() throws Exception {
        final Integer brandId = 1;
        final LocalDateTime applicationDate =
                LocalDateTime.of(2022, 6, 14, 10, 0);
        
        mockMvc.perform(get(URL_PATH)
                .param("brandId", brandId.toString())
                .param("applicationDate", applicationDate.toString()))
            .andExpect(status().isBadRequest())
            .andExpect(jsonPath("$.code").exists())
            .andExpect(jsonPath("$.detail").exists());
    }
    
    @Test
    @DisplayName("""
    		        Given a brand,a product, an application date matching an applicable price date
    		        When the parameter product is not a number
                    Then a 400 Bad Request response should be returned 
                    And the response should contain the code, detail 
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
            .andExpect(jsonPath("$.code").exists())
            .andExpect(jsonPath("$.detail").exists());
    }
    
    @Test
    @DisplayName("""
    		        Given a brand,a product, an application date
    		        When the applicationDate is not a valid date
                    Then a 400 Bad Request response should be returned 
                    And the response should contain the code, detail 
                 """)
    void shouldReturnBadRequestDateNotValidError() throws Exception {
        final Integer brandId = 1;
        final Long productId = 35455L;
        
        mockMvc.perform(get(URL_PATH)
                .param("brandId", brandId.toString())
                .param("product", productId.toString())
                .param("applicationDate", "29319"))
            .andExpect(status().isBadRequest())
            .andExpect(jsonPath("$.code").exists())
            .andExpect(jsonPath("$.detail").exists());
    }
    
    @Test
    @DisplayName("""
    		        Given a brand,a product, an application date
    		        When the applicationDate is not a valid date format
                    Then a 400 Bad Request response should be returned 
                    And the response should contain the code, detail 
                 """)
    void shouldReturnBadRequestDateNotValidFormatError() throws Exception {
        final Integer brandId = 1;
        final Long productId = 35455L;
        
        mockMvc.perform(get(URL_PATH)
                .param("brandId", brandId.toString())
                .param("product", productId.toString())
                .param("applicationDate", "2020-06-14"))
            .andExpect(status().isBadRequest())
            .andExpect(jsonPath("$.code").exists())
            .andExpect(jsonPath("$.detail").exists());
    }
    
    @Test
    @DisplayName("""
    		        Given a brand,a product, an application date
    		        When the brandId is empty
                    Then a 400 Bad Request response should be returned 
                    And the response should contain the code, detail 
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
            .andExpect(jsonPath("$.code").exists())
            .andExpect(jsonPath("$.detail").exists());
    }
    
    @Test
    @DisplayName("""
    		        Given a brand,a product, an application date
    		        When the brandId is null
                    Then a 400 Bad Request response should be returned 
                    And the response should contain the code, detail 
                 """)
    void shouldReturnBadRequestNullError() throws Exception {
        final Long productId = 35455L;
        
        mockMvc.perform(get(URL_PATH)
                .param("brandId", "null")
                .param("product", productId.toString())
                .param("applicationDate", "29319"))
            .andExpect(status().isBadRequest())
            .andExpect(jsonPath("$.code").exists())
            .andExpect(jsonPath("$.detail").exists());
    }
    
  

}
