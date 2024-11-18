package com.ttn.mohitramtari.bootcampproject.ecommerce.modules.category.service;

import com.ttn.mohitramtari.bootcampproject.ecommerce.modules.category.dto.CategoryFieldDto;
import com.ttn.mohitramtari.bootcampproject.ecommerce.modules.category.dto.CategoryMetadataFieldValuesDto;
import com.ttn.mohitramtari.bootcampproject.ecommerce.modules.category.dto.CategoryUpdateDto;
import com.ttn.mohitramtari.bootcampproject.ecommerce.modules.category.dto.MetadataFieldDto;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;

import java.util.Optional;

public interface CategoryService {

    ResponseEntity addNewMetadataField(MetadataFieldDto metadataFieldDto);

    Page getAllMetadataFields(Integer pageSize, Integer pageOffset, String sortProperty,
                              String sortDirection);

    ResponseEntity addNewCategory(CategoryFieldDto categoryFIeldDto);

    ResponseEntity getCategory(Long id);

    ResponseEntity updateCategory(CategoryUpdateDto categoryUpdateDto);

    ResponseEntity addCategoryMetadataFieldValues(
            CategoryMetadataFieldValuesDto categoryMetadataFieldValuesDto);

    ResponseEntity updateMetadataFieldValues(
            CategoryMetadataFieldValuesDto categoryMetadataFieldValuesDto);

    Page viewAllCategories(Integer pageSize, Integer pageOffset, String sortProperty,
                           String sortDirection);

    ResponseEntity getAllCategoriesForSeller();

    ResponseEntity getAllCategoriesForCustomer(Optional<Long> id);

    ResponseEntity getFilteringDetails(Long id);

}
