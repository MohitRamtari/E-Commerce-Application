package com.ttn.mohitramtari.bootcampproject.ecommerce.modules.seller.dto;

import com.ttn.mohitramtari.bootcampproject.ecommerce.modules.user.address.dto.AddressDto;
import lombok.Getter;
import lombok.Setter;
import org.springframework.hateoas.RepresentationModel;

@Getter
@Setter
public class SellerModel extends RepresentationModel<SellerModel> {

    Long userId;
    String userFullName;
    String userEmail;
    String sellerCompanyContact;
    String sellerCompanyName;
    AddressDto address;
    Boolean userIsActive;
}
