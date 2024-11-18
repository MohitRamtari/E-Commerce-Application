package com.ttn.mohitramtari.bootcampproject.ecommerce.modules.category.dto;

import com.ttn.mohitramtari.bootcampproject.ecommerce.modules.category.dto.category_view.MetadataFieldValuesDto;
import com.ttn.mohitramtari.bootcampproject.ecommerce.modules.category.dto.category_view.ParentCategoryDetailsDto;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.FieldDefaults;

import java.util.Set;

import static lombok.AccessLevel.PRIVATE;

@Getter
@Setter
@FieldDefaults(level = PRIVATE)
public class CategoryDetailsForSellerDto {
    Long id;
    String categoryName;
    ParentCategoryDetailsDto parentCategory;
    Set<MetadataFieldValuesDto> metadataFieldValuesSet;
}
