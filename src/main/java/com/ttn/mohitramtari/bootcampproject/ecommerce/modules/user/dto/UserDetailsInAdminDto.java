package com.ttn.mohitramtari.bootcampproject.ecommerce.modules.user.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UserDetailsInAdminDto {

    Long userId;
    String userFirstName;
    String userMiddleName;
    String userLastName;
    String userEmail;
    Boolean userIsActive;
}
