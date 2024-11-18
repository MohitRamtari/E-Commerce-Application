package com.ttn.mohitramtari.bootcampproject.ecommerce.modules.customer.dto;

import lombok.Getter;
import lombok.Setter;
import lombok.experimental.FieldDefaults;
import org.springframework.hateoas.RepresentationModel;

import static lombok.AccessLevel.PRIVATE;

@Getter
@Setter
@FieldDefaults(level = PRIVATE)
public class CustomerModel extends RepresentationModel<CustomerModel> {

    Long userId;
    String userFullName;
    String userEmail;
    Boolean userIsActive;
}
