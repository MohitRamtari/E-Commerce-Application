package com.ttn.mohitramtari.bootcampproject.ecommerce.modules.product.dto;

import lombok.Getter;
import lombok.Setter;
import lombok.experimental.FieldDefaults;
import org.springframework.web.multipart.MultipartFile;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.util.List;

import static lombok.AccessLevel.PRIVATE;

@Getter
@Setter
@FieldDefaults(level = PRIVATE)
public class ProductVariationDto {

    @NotNull(message = "Product Id can't be blank")
    Long productId;

    @NotNull(message = "Quantity field can't be blank")
    Integer quantityAvailable;

    @NotNull(message = "Price can't be blank")
    Integer price;

    @NotBlank(message = "Metadata can't be blank")
    String metadata;

    @NotNull(message = "Image can't be blank")
    MultipartFile primaryImage;

    List<MultipartFile> secondaryImages;
}
