package com.ttn.mohitramtari.bootcampproject.ecommerce.modules.category.model;

import com.ttn.mohitramtari.bootcampproject.ecommerce.app.util.Auditable;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import java.util.Set;

@Getter
@Setter
@NoArgsConstructor
@Entity
@Table(name = "category_metadata_field")
public class CategoryMetadataField extends Auditable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @Column(name = "category_metadata_field_name", unique = true, nullable = false)
    private String name;

    @OneToMany(mappedBy = "categoryMetadataField", cascade = CascadeType.ALL, fetch = FetchType.EAGER)
    private Set<CategoryMetadataFieldValues> metadataFieldValuesSet;
}
