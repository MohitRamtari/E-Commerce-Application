package com.ttn.mohitramtari.bootcampproject.ecommerce.modules.category.model;

import com.ttn.mohitramtari.bootcampproject.ecommerce.app.util.Auditable;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import java.util.HashSet;
import java.util.Set;

@Getter
@Setter
@Entity
@NoArgsConstructor
@Table(name = "categories")
public class Category extends Auditable {

    @OneToMany(mappedBy = "category", cascade = CascadeType.ALL, fetch = FetchType.EAGER)
    Set<CategoryMetadataFieldValues> metadataFieldValuesSet;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;
    @Column(name = "category_name")
    private String categoryName;
    @Column(name = "is_deleted")
    private Boolean isCategoryDeleted;
    @ManyToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "parent_category_id")
    private Category parentCategory;
    @OneToMany(mappedBy = "parentCategory", cascade = CascadeType.ALL, fetch = FetchType.EAGER)
    private Set<Category> childCategoriesSet;

    public void addChildCategory(Category category) {
        if (category != null) {
            if (childCategoriesSet == null) {
                childCategoriesSet = new HashSet<>();
            }

            category.setParentCategory(this);
            childCategoriesSet.add(category);
        }
    }
}
