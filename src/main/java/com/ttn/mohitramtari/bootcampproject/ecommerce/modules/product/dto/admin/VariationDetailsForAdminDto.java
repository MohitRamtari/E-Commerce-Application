package com.ttn.mohitramtari.bootcampproject.ecommerce.modules.product.dto.admin;

import lombok.Getter;
import lombok.Setter;
import lombok.experimental.FieldDefaults;

import static lombok.AccessLevel.PRIVATE;

@Getter
@Setter
@FieldDefaults(level = PRIVATE)
public class VariationDetailsForAdminDto {
    Long variationId;

    String primaryImage;
}
