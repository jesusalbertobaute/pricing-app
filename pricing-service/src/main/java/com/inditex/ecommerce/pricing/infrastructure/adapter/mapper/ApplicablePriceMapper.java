package com.inditex.ecommerce.pricing.infrastructure.adapter.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import com.inditex.ecommerce.pricing.domain.model.Price;
import com.inditex.ecommerce.pricing.generated.model.ApplicablePriceDto;
import com.inditex.ecommerce.pricing.infrastructure.adapter.output.persistence.entity.PriceEntity;

@Mapper(componentModel = "spring")
public interface ApplicablePriceMapper {

   @Mapping(target = "endPrice", source = "price")
   Price toPrice(PriceEntity priceEntity);
   
   @Mapping(target = "price", source = "endPrice")
   PriceEntity toPriceEntity(Price price);
   
   @Mapping(target = "price", source = "endPrice")
   ApplicablePriceDto toApplicablePriceDto(Price price);
}
