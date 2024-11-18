package com.ttn.mohitramtari.bootcampproject.ecommerce.modules.category.model;

import com.ttn.mohitramtari.bootcampproject.ecommerce.app.util.Auditable;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;

@Getter
@Setter
@NoArgsConstructor
@Entity
@Table(name = "category_metadata_field_values")
public class CategoryMetadataFieldValues extends Auditable {
    @EmbeddedId
    private CategoryMetadataFieldValuesId id = new CategoryMetadataFieldValuesId();
    @Column(name = "metadata_field_values")
    private String metadataFieldValues;
    @ManyToOne(fetch = FetchType.EAGER)
    @MapsId("categoryId")
    private Category category;
    @ManyToOne(fetch = FetchType.EAGER)
    @MapsId("categoryMetadataFieldId")
    private CategoryMetadataField categoryMetadataField;
}
