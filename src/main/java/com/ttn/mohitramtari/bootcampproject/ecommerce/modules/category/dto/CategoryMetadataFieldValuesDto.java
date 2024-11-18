package com.ttn.mohitramtari.bootcampproject.ecommerce.modules.category.dto;

import lombok.Getter;
import lombok.Setter;
import lombok.experimental.FieldDefaults;

import javax.validation.constraints.NotNull;
import java.util.List;

import static lombok.AccessLevel.PRIVATE;

@Getter
@Setter
@FieldDefaults(level = PRIVATE)
public class CategoryMetadataFieldValuesDto {
    @NotNull(message = "{category.id.null}")
    Long categoryId;

    @NotNull(message = "{metadata.id.null}")
    List<Long> metadataFieldIdList;

    @NotNull(message = "{field.values.empty}")
    List<String> fieldValuesList;
}
