package com.ttn.mohitramtari.bootcampproject.ecommerce.modules.product.dto;

import com.nimbusds.jose.shaded.json.JSONObject;
import com.vladmihalcea.hibernate.type.json.JsonStringType;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.FieldDefaults;
import org.hibernate.annotations.TypeDef;

import java.util.List;

import static lombok.AccessLevel.PRIVATE;

@Getter
@Setter
@TypeDef(name = "json", typeClass = JsonStringType.class)
@FieldDefaults(level = PRIVATE)
public class VariationDto {

    Integer quantityAvailable;
    Integer price;
    Boolean isActive;
    JSONObject metadata;

    String primaryImage;
    List<String> secondaryImages;
}
