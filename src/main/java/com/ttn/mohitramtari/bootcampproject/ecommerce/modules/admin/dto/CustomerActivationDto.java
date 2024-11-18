package com.ttn.mohitramtari.bootcampproject.ecommerce.modules.admin.dto;

import lombok.Getter;
import lombok.Setter;
import lombok.experimental.FieldDefaults;
import org.springframework.hateoas.RepresentationModel;

import java.io.Serializable;

import static lombok.AccessLevel.PRIVATE;

@Getter
@Setter
@FieldDefaults(level = PRIVATE)
public class CustomerActivationDto extends RepresentationModel<SellerActivationDto> implements
        Serializable {

    Long userId;
    String userFirstName;
    String userLastName;
    String customerContact;
}
