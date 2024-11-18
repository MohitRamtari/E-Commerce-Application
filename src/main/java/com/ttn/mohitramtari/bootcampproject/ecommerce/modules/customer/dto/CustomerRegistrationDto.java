package com.ttn.mohitramtari.bootcampproject.ecommerce.modules.customer.dto;

import com.ttn.mohitramtari.bootcampproject.ecommerce.app.util.GlobalVariables;
import com.ttn.mohitramtari.bootcampproject.ecommerce.modules.user.dto.UserRegistrationDto;
import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Pattern;

@Getter
@Setter
public class CustomerRegistrationDto extends UserRegistrationDto {
    @Pattern(regexp = GlobalVariables.MOBILE_NUMBER_REGEX, message = "{contact.valid}")
    @NotBlank(message = "{contact.blank}")
    private String customerContact;
}
