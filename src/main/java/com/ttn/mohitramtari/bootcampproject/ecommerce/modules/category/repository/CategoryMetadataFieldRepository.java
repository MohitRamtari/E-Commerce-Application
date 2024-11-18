package com.ttn.mohitramtari.bootcampproject.ecommerce.modules.category.repository;

import com.ttn.mohitramtari.bootcampproject.ecommerce.modules.category.model.CategoryMetadataField;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface CategoryMetadataFieldRepository extends JpaRepository<CategoryMetadataField, Long> {

    Boolean existsByName(String name);

    @Query(value = "select category_metadata_field_name from category_metadata_field where id=:id", nativeQuery = true)
    String findFieldNameById(Long id);
}
