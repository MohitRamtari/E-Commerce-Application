package com.ttn.mohitramtari.bootcampproject.ecommerce.modules.customer.model;

import com.ttn.mohitramtari.bootcampproject.ecommerce.app.exception.ResourceAlreadyExistException;
import com.ttn.mohitramtari.bootcampproject.ecommerce.modules.user.address.model.Address;
import com.ttn.mohitramtari.bootcampproject.ecommerce.modules.user.model.User;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.util.Set;

@Entity
@Getter
@Setter
@Builder
@AllArgsConstructor
@Table(name = "customers")
public class Customer extends User {

    @Column(name = "customer_contact")
    private String customerContact;

    @Column(name = "customer_is_deleted")
    private Boolean customerIsDeleted;

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, fetch = FetchType.EAGER)
    private Set<Address> addressSet;

    public Customer() {
        this.setUserIsActive(false);
        this.setUserIsDeleted(false);
        this.setUserIsExpired(false);
        this.setUserIsLocked(false);
        this.setCustomerIsDeleted(false);
        this.setUserInvalidAttemptCount(0);
    }

    public void addAddress(Address address) {
        if (address != null) {
            if (!addressSet.contains(address)) {
                address.setUser(this);
                addressSet.add(address);
            } else {
                throw new ResourceAlreadyExistException("There is already an address with same details.");
            }
        }
    }
}
