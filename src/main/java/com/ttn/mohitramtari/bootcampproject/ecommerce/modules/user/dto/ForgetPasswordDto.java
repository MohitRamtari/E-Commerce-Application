package com.ttn.mohitramtari.bootcampproject.ecommerce.modules.user.dto;

import com.ttn.mohitramtari.bootcampproject.ecommerce.app.util.GlobalVariables;
import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Pattern;

@Getter
@Setter
public class ForgetPasswordDto {

    @Pattern(regexp = GlobalVariables.EMAIL_REGEX, message = "{email.condition}")
    @NotBlank(message = "{email.blank}")
    String email;
}
