package com.ttn.mohitramtari.bootcampproject.ecommerce.modules.user.dto;

import com.ttn.mohitramtari.bootcampproject.ecommerce.app.util.GlobalVariables;
import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Pattern;

@Getter
@Setter
public class UserRegistrationDto {

    @Pattern(regexp = GlobalVariables.EMAIL_REGEX, message = "{email.condition}")
    @NotBlank(message = "{email.blank}")
    private String userEmail;

    @NotBlank(message = "{first.name.blank}")
    private String userFirstName;

    private String userMiddleName;

    @NotBlank(message = "{last.name.blank}")
    private String userLastName;

    @Pattern(regexp = GlobalVariables.PASSWORD_REGEX, message = "{password.condition}")
    @NotBlank(message = "{password.blank}")
    private String userPassword;

    @Pattern(regexp = GlobalVariables.PASSWORD_REGEX, message = "{password.condition}")
    @NotBlank(message = "{password.blank}")
    private String confirmPassword;
}
