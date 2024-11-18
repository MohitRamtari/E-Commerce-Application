package com.ttn.mohitramtari.bootcampproject.ecommerce.modules.category.dto;

import lombok.Getter;
import lombok.Setter;
import lombok.experimental.FieldDefaults;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

import static lombok.AccessLevel.PRIVATE;

@Getter
@Setter
@FieldDefaults(level = PRIVATE)
public class ProductVariationUpdateDto {
    Integer quantityAvailable;

    Integer price;

    String metadata;

    Boolean isActive;
    MultipartFile primaryImage;

    List<MultipartFile> secondaryImages;
}
