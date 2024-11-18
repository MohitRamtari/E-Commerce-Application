package com.ttn.mohitramtari.bootcampproject.ecommerce.modules.category.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.Column;
import javax.persistence.Embeddable;
import java.io.Serializable;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Embeddable
public class CategoryMetadataFieldValuesId implements Serializable {

    private static final long serialVersionUID = 7539191488419197418L;
    @Column(name = "CATEGORY_METADATA_FIELD_ID")
    private Long categoryMetadataFieldId;

    @Column(name = "CATEGORY_ID")
    private Long categoryId;
}
