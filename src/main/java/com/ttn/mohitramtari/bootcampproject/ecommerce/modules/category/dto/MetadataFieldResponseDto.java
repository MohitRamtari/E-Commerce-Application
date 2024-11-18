package com.ttn.mohitramtari.bootcampproject.ecommerce.modules.category.dto;

import lombok.Getter;
import lombok.Setter;
import lombok.experimental.FieldDefaults;

import static lombok.AccessLevel.PRIVATE;

@Getter
@Setter
@FieldDefaults(level = PRIVATE)
public class MetadataFieldResponseDto {
    Long id;
    String name;
}
