package com.ttn.mohitramtari.bootcampproject.ecommerce.modules.category.dto;

import lombok.Getter;
import lombok.Setter;
import lombok.experimental.FieldDefaults;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

import static lombok.AccessLevel.PRIVATE;

@Getter
@Setter
@FieldDefaults(level = PRIVATE)
public class CategoryUpdateDto {
    @NotNull(message = "{category.id.null}")
    Long id;

    @NotBlank(message = "{category.name.blank}")
    String categoryName;
}
