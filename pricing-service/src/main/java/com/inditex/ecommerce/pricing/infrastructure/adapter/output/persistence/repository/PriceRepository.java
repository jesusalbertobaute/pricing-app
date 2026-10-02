package com.inditex.ecommerce.pricing.infrastructure.adapter.output.persistence.repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.inditex.ecommerce.pricing.infrastructure.adapter.output.persistence.entity.PriceEntity;

public interface PriceRepository extends JpaRepository<PriceEntity, UUID> {

	@Query("""
            SELECT p
            FROM PriceEntity p
            WHERE p.brandId = :brandId
            AND p.productId = :productId
            AND p.startDate <= :applicableDate
            AND p.endDate >= :applicableDate
            ORDER BY p.priority DESC, 
            p.createdAt DESC, 
            p.id
           """)
	List<PriceEntity> findPrices(@Param("brandId") Integer brandId, @Param("productId") Long productId,
			@Param("applicableDate") LocalDateTime applicableDate, Pageable pageable);

}
