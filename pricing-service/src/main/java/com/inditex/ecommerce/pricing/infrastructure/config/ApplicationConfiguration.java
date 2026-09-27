package com.inditex.ecommerce.pricing.infrastructure.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.inditex.ecommerce.pricing.application.port.input.GetApplicablePriceInputPort;
import com.inditex.ecommerce.pricing.application.port.output.GetApplicablePriceOutputPort;
import com.inditex.ecommerce.pricing.application.usecase.GetApplicablePriceUseCase;

@Configuration
public class ApplicationConfiguration {
   @Bean	
   public GetApplicablePriceInputPort getApplicablePriceUseCase(GetApplicablePriceOutputPort getApplicablePriceOutputPort) {
	   return new GetApplicablePriceUseCase(getApplicablePriceOutputPort);
   }
}
