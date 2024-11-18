package com.ttn.mohitramtari.bootcampproject.ecommerce.modules.seller.dto;

import com.ttn.mohitramtari.bootcampproject.ecommerce.app.util.GlobalVariables;
import com.ttn.mohitramtari.bootcampproject.ecommerce.modules.user.address.model.Address;
import com.ttn.mohitramtari.bootcampproject.ecommerce.modules.user.dto.UserRegistrationDto;
import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;

@Getter
@Setter
public class SellerRegistrationDto extends UserRegistrationDto {

    @NotBlank(message = "{gst.blank}")
    @Pattern(regexp = GlobalVariables.GST_REGEX, message = "{gst.valid}")
    private String sellerGstNo;

    @NotBlank(message = "{contact.blank}")
    @Pattern(regexp = GlobalVariables.MOBILE_NUMBER_REGEX, message = "{contact.valid}")
    private String sellerCompanyContact;

    @NotBlank(message = "{company.name.blank}")
    private String sellerCompanyName;

    @NotNull(message = "{address.blank}")
    private Address address;
}
