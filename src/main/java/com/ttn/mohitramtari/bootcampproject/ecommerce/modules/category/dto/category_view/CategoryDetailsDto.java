package com.ttn.mohitramtari.bootcampproject.ecommerce.modules.category.dto.category_view;

import lombok.Getter;
import lombok.Setter;
import lombok.experimental.FieldDefaults;

import java.util.Set;

import static lombok.AccessLevel.PRIVATE;

@Getter
@Setter
@FieldDefaults(level = PRIVATE)
public class CategoryDetailsDto {

    Long id;
    String categoryName;
    ParentCategoryDetailsDto parentCategory;
    Set<ChildCategoriesDetailsDto> childCategoriesSet;
    Set<MetadataFieldValuesDto> metadataFieldValuesSet;
}
