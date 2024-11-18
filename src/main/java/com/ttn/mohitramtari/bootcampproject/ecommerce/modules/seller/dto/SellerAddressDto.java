package com.ttn.mohitramtari.bootcampproject.ecommerce.modules.seller.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class SellerAddressDto {

    String userAddressLine;
    String userStreet;
    String userCity;
    String userState;
    String userCountry;
    Integer userAddressZipCode;
}
