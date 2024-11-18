package com.ttn.mohitramtari.bootcampproject.ecommerce.modules.user.dto;

import com.ttn.mohitramtari.bootcampproject.ecommerce.app.util.GlobalVariables;
import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Pattern;

@Getter
@Setter
public class UpdatePasswordDto {

    @NotBlank(message = "{password.blank}")
    @Pattern(regexp = GlobalVariables.PASSWORD_REGEX, message = "{password.condition}")
    String password;

    @NotBlank(message = "{password.blank}")
    @Pattern(regexp = GlobalVariables.PASSWORD_REGEX, message = "{password.condition}")
    String confirmPassword;
}
