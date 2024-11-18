package com.ttn.mohitramtari.bootcampproject.ecommerce.modules.product.model;

import com.ttn.mohitramtari.bootcampproject.ecommerce.app.util.Auditable;
import com.ttn.mohitramtari.bootcampproject.ecommerce.modules.category.model.Category;
import com.ttn.mohitramtari.bootcampproject.ecommerce.modules.seller.model.Seller;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.util.Set;

@Getter
@Setter
@Entity
@Table(name = "products")
public class Product extends Auditable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    Long id;

    @Column(name = "name")
    String name;

    @Column(name = "brand")
    String brand;

    @Column(name = "description")
    String description;

    @Column(name = "is_cancellable")
    Boolean isCancellable;

    @Column(name = "is_returnable")
    Boolean isReturnable;

    @Column(name = "is_active")
    Boolean isActive;

    @Column(name = "is_deleted")
    Boolean isDeleted;

    @ManyToOne
    @JoinColumn(name = "category_id")
    Category category;

    @ManyToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "seller_id")
    Seller seller;

    @OneToMany(cascade = {CascadeType.PERSIST, CascadeType.REMOVE}, fetch = FetchType.EAGER)
    Set<ProductVariation> productVariationSet;

    public Product() {
        this.isActive = false;
        this.isDeleted = false;
        this.isReturnable = false;
        this.isCancellable = false;
    }

}
