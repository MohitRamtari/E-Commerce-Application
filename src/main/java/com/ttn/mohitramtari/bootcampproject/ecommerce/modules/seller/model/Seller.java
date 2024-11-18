package com.ttn.mohitramtari.bootcampproject.ecommerce.modules.seller.model;

import com.ttn.mohitramtari.bootcampproject.ecommerce.modules.user.address.model.Address;
import com.ttn.mohitramtari.bootcampproject.ecommerce.modules.user.model.User;
import lombok.Data;

import javax.persistence.*;

@Entity
@Data
@Table(name = "sellers")
public class Seller extends User {

    @Column(name = "seller_gst_no")
    private String sellerGstNo;

    @Column(name = "seller_company_contact")
    private String sellerCompanyContact;

    @Column(name = "seller_company_name")
    private String sellerCompanyName;

    @Column(name = "seller_is_deleted")
    private Boolean sellerIsDeleted;

    @OneToOne(mappedBy = "user", cascade = CascadeType.ALL, fetch = FetchType.EAGER)
    private Address address;

    public Seller() {
        this.setUserIsActive(false);
        this.setUserIsDeleted(false);
        this.setUserIsExpired(false);
        this.setUserIsLocked(false);
        this.setUserInvalidAttemptCount(0);
    }
}
