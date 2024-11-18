package com.ttn.mohitramtari.bootcampproject.ecommerce.modules.category.repository;

import com.ttn.mohitramtari.bootcampproject.ecommerce.modules.category.model.CategoryMetadataFieldValues;
import com.ttn.mohitramtari.bootcampproject.ecommerce.modules.category.model.CategoryMetadataFieldValuesId;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface CategoryMetadataFieldValuesRepository extends
        JpaRepository<CategoryMetadataFieldValues, CategoryMetadataFieldValuesId> {

    List<CategoryMetadataFieldValues> findByCategoryId(Long id);

    CategoryMetadataFieldValues findByCategoryIdAndCategoryMetadataFieldId(Long id, Long id1);

    @Query(value = "select categoryMetadataField_id from category_metadata_field_values where category_id=:id", nativeQuery = true)
    List<Long> findMetadataFieldsByCategoryId(Long id);

    @Query(value = "select metadata_field_values from category_metadata_field_values where category_id=:id", nativeQuery = true)
    List<String> findMetadataValuesByCategoryId(Long id);
}
