package com.ttn.mohitramtari.bootcampproject.ecommerce.modules.user.address.dto;

import com.ttn.mohitramtari.bootcampproject.ecommerce.app.util.GlobalVariables;
import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.Pattern;

@Getter
@Setter
public class AddressDto {

    String userAddressLine;
    String userStreet;
    String userCity;
    String userState;
    String userCountry;
    @Pattern(regexp = GlobalVariables.POSTAL_CODE_REGEX, message = "{postal.code.valid}")
    String userAddressZipCode;
    String userAddressLabel;

}
