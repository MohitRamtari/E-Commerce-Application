package com.ttn.mohitramtari.bootcampproject.ecommerce.modules.product.dto;

import lombok.Getter;
import lombok.Setter;
import lombok.experimental.FieldDefaults;

import static lombok.AccessLevel.PRIVATE;

@Getter
@Setter
@FieldDefaults(level = PRIVATE)
public class VariationDetailsForCustomerDto {
    Long variationId;
    String primaryImage;
}
