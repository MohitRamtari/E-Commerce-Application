package com.ttn.mohitramtari.bootcampproject.ecommerce.modules.product.model;

import com.ttn.mohitramtari.bootcampproject.ecommerce.app.util.Auditable;
import com.vladmihalcea.hibernate.type.json.JsonStringType;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.Type;
import org.hibernate.annotations.TypeDef;

import javax.persistence.*;

@Getter
@Setter
@Entity
@Table(name = "product_variations")
//ref : https://www.baeldung.com/hibernate-types-library
//ref : https://prateek-ashtikar512.medium.com/how-to-handle-json-in-mysql-4adaeeb1d42f
@TypeDef(name = "json", typeClass = JsonStringType.class)
public class ProductVariation extends Auditable {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    Long id;

    @ManyToOne
    @JoinColumn(name = "product_id")
    Product product;

    @Column(name = "quantity_available")
    Integer quantityAvailable;

    @Column(name = "price")
    Integer price;

    @Column(name = "is_active")
    Boolean isActive;

    @Type(type = "json")
    @Column(name = "metadata", columnDefinition = "json")
    String metadata;

    Boolean isPrimaryImageSet;
}

