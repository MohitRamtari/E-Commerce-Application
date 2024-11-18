package com.ttn.mohitramtari.bootcampproject.ecommerce.modules.product.repository;

import com.ttn.mohitramtari.bootcampproject.ecommerce.modules.product.model.Product;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface ProductRepository extends JpaRepository<Product, Long> {

    @Query(value = "select * from products where category_id=:categoryId AND seller_id=:sellerId AND brand=:brand", nativeQuery = true)
    Optional<Product> productAlreadyExist(String brand, Long categoryId, Long sellerId);

    @Query(value = "select * from products where seller_id=:userId", nativeQuery = true)
    Page<Product> findBySellerId(Long userId, Pageable pageable);

    @Query(value = "select name from products where brand=:brand AND category_id=:categoryId AND seller_id=:sellerId", nativeQuery = true)
    List<String> getProductNameList(String brand, Long categoryId, Long sellerId);

    @Query(value = "select * from products where category_id=:id", nativeQuery = true)
    List<Product> findByCategoryId(Long id);

    @Query(value = "select brand from products where category_id=:id", nativeQuery = true)
    List<String> getBrandNameById(Long id);

    @Query(value = "select id from products where category_id=:categoryId", nativeQuery = true)
    List<Long> getProductIdById(Long categoryId);

    @Query("SELECT p from Product p where p.isActive=0")
    List<Product> findAllProductsToBeActivated();

    @Query(value = "SELECT * FROM products where category_id=:id AND is_deleted=0", nativeQuery = true)
    List<Product> findByProductId(Long id);
}
