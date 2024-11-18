package com.ttn.mohitramtari.bootcampproject.ecommerce.modules.customer.dto;

import com.ttn.mohitramtari.bootcampproject.ecommerce.modules.user.dto.UserDetailsDto;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CustomerDetailsDto extends UserDetailsDto {

    private String customerContact;
}
