package com.ttn.mohitramtari.bootcampproject.ecommerce.modules.user.dto;

import lombok.Getter;
import lombok.Setter;

import java.nio.file.Path;

@Getter
@Setter
public class UserDetailsDto {

    Long userId;
    String userFirstName;
    String userLastName;
    Path imagePath;
    String imageUrl;
    Boolean IsActive;
}
