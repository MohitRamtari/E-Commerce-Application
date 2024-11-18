package com.ttn.mohitramtari.bootcampproject.ecommerce.modules.product.dto;

import com.ttn.mohitramtari.bootcampproject.ecommerce.modules.category.dto.category_view.CategoryDetailsDto;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.FieldDefaults;

import java.util.Set;

import static lombok.AccessLevel.PRIVATE;

@Getter
@Setter
@FieldDefaults(level = PRIVATE)
public class ProductDetailsForCustomerDto {
    String name;
    String brand;
    String description;
    Boolean isCancellable;
    Boolean isReturnable;
    CategoryDetailsDto category;

    Set<VariationDetailsForCustomerDto> productVariationSet;
}
