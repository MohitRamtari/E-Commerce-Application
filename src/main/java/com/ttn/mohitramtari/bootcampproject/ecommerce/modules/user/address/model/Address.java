package com.ttn.mohitramtari.bootcampproject.ecommerce.modules.user.address.model;

import com.ttn.mohitramtari.bootcampproject.ecommerce.app.util.Auditable;
import com.ttn.mohitramtari.bootcampproject.ecommerce.modules.user.model.User;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import javax.persistence.*;

@EntityListeners(AuditingEntityListener.class)
@Entity
@Getter
@Setter
@AllArgsConstructor
@Table(name = "address")
public class Address extends Auditable {

    @ManyToOne
    @JoinColumn(name = "user_id")
    User user;
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "address_id")
    private Long addressId;

    @Column(name = "user_address_line")
    private String userAddressLine;

    @Column(name = "user_street")
    private String userStreet;

    @Column(name = "user_city")
    private String userCity;
    @Column(name = "user_state")
    private String userState;
    @Column(name = "user_country")
    private String userCountry;
    @Column(name = "user_address_zip_code")
    private String userAddressZipCode;
    @Column(name = "user_address_label")
    private String userAddressLabel;
    @Column(name = "user_address_is_deleted")
    private Boolean userAddressIsDeleted;

    public Address() {
        this.userAddressIsDeleted = false;
    }
}
