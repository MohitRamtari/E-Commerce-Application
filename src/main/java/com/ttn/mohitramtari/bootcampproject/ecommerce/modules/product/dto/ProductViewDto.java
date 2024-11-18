package com.ttn.mohitramtari.bootcampproject.ecommerce.modules.product.dto;

import com.ttn.mohitramtari.bootcampproject.ecommerce.modules.category.dto.CategoryDetailsForSellerDto;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.FieldDefaults;

import static lombok.AccessLevel.PRIVATE;

@Getter
@Setter
@FieldDefaults(level = PRIVATE)
public class ProductViewDto {

    String name;
    String brand;
    String description;
    Boolean isCancellable;
    Boolean isReturnable;
    CategoryDetailsForSellerDto category;
}
