package com.ttn.mohitramtari.bootcampproject.ecommerce.modules.user.dto;

import lombok.Getter;
import lombok.Setter;
import lombok.experimental.FieldDefaults;

import static lombok.AccessLevel.PRIVATE;

@Getter
@Setter
@FieldDefaults(level = PRIVATE)
public class UserProfileUpdateDto {
    String userFirstName;
    String userMiddleName;
    String userLastName;
}
