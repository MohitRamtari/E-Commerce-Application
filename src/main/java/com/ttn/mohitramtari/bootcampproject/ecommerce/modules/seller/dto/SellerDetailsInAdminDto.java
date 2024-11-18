package com.ttn.mohitramtari.bootcampproject.ecommerce.modules.seller.dto;

import com.ttn.mohitramtari.bootcampproject.ecommerce.modules.user.address.dto.AddressDto;
import com.ttn.mohitramtari.bootcampproject.ecommerce.modules.user.dto.UserDetailsInAdminDto;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class SellerDetailsInAdminDto extends UserDetailsInAdminDto {

    private String sellerCompanyContact;
    private String sellerCompanyName;
    private AddressDto address;
}
