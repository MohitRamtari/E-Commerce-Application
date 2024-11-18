package com.ttn.mohitramtari.bootcampproject.ecommerce.modules.seller.dto;

import com.ttn.mohitramtari.bootcampproject.ecommerce.modules.user.dto.UserDetailsDto;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class SellerDetailsDto extends UserDetailsDto {

    private String sellerGstNo;
    private String sellerCompanyContact;
    private String sellerCompanyName;
    private SellerAddressDto sellerAddressDto;
}
