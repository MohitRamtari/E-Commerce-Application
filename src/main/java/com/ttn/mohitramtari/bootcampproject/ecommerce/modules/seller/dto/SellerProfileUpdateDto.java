package com.ttn.mohitramtari.bootcampproject.ecommerce.modules.seller.dto;

import com.ttn.mohitramtari.bootcampproject.ecommerce.app.util.GlobalVariables;
import com.ttn.mohitramtari.bootcampproject.ecommerce.modules.user.dto.UserProfileUpdateDto;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.FieldDefaults;

import javax.validation.constraints.Pattern;

import static lombok.AccessLevel.PRIVATE;

@Getter
@Setter
@FieldDefaults(level = PRIVATE)
public class SellerProfileUpdateDto extends UserProfileUpdateDto {
    @Pattern(regexp = GlobalVariables.MOBILE_NUMBER_REGEX, message = "{contact.valid}")
    String sellerCompanyContact;

    String sellerCompanyName;
}
