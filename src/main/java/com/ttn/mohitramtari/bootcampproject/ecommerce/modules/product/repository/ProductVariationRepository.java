package com.ttn.mohitramtari.bootcampproject.ecommerce.modules.product.repository;

import com.ttn.mohitramtari.bootcampproject.ecommerce.modules.product.model.ProductVariation;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import javax.transaction.Transactional;
import java.util.List;

public interface ProductVariationRepository extends JpaRepository<ProductVariation, Long> {

    @Query(value = "SELECT * FROM product_variations WHERE product_id = :productId", nativeQuery = true)
    List<ProductVariation> findByProductId(@Param("productId") Long id, Pageable page);

    @Query(value = "Select * from product_variations where product_id=:id", nativeQuery = true)
    List<ProductVariation> checkIfProductExist(@Param("id") Long id);

    @Query(value = "select min(price) from product_variations where product_id=:productId", nativeQuery = true)
    int findMinimumPrice(@Param("productId") Long productId);

    @Query(value = "select price from product_variations where product_id=:id", nativeQuery = true)
    List findPrice(@Param("id") Long id);

    @Modifying
    @Transactional
    @Query(value = "update product_variations set isPrimaryImageSet=true where id=:id", nativeQuery = true)
    void updateImageSet(Long id);

    @Query(value = "select metadata from product_variations where product_id=:id", nativeQuery = true)
    List<String> findMetadataByProductId(Long id);

    @Query(value = "SELECT * FROM product_variations WHERE product_id = :productId", nativeQuery = true)
    List<ProductVariation> findVariantsByProductId(@Param("productId") Long id);
}
