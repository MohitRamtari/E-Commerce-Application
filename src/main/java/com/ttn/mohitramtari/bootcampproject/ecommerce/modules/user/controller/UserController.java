package com.ttn.mohitramtari.bootcampproject.ecommerce.modules.user.controller;

import com.ttn.mohitramtari.bootcampproject.ecommerce.modules.customer.dto.CustomerRegistrationDto;
import com.ttn.mohitramtari.bootcampproject.ecommerce.modules.customer.service.impl.CustomerServiceImpl;
import com.ttn.mohitramtari.bootcampproject.ecommerce.modules.seller.dto.SellerRegistrationDto;
import com.ttn.mohitramtari.bootcampproject.ecommerce.modules.seller.service.impl.SellerServiceImpl;
import com.ttn.mohitramtari.bootcampproject.ecommerce.modules.user.dto.ForgetPasswordDto;
import com.ttn.mohitramtari.bootcampproject.ecommerce.modules.user.dto.UpdatePasswordDto;
import io.swagger.v3.oas.annotations.Operation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.oauth2.common.OAuth2AccessToken;
import org.springframework.security.oauth2.provider.token.TokenStore;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

@RestController
@Validated
public class UserController {

    @Autowired
    CustomerServiceImpl customerService;

    @Autowired
    SellerServiceImpl sellerService;

    @Autowired
    private TokenStore tokenStore;

    @Operation(summary = "Customer Registration", description = "Person can register as a customer by providing its basic details and contact number. After registration a mail is sent to the customer which contains a url to activate his account and has a token to activate it. Validation time for this token is 3 hours", method = "POST")
    @PostMapping("/register/customer")
    ResponseEntity customerRegistration(
            @Valid @RequestBody CustomerRegistrationDto customerRegistrationDto) {
        return customerService.createCustomer(customerRegistrationDto);
    }

    @Operation(summary = "Seller Registration", description = "Person can register as a seller by providing his basic details and his company details and gst number", method = "POST")
    @PostMapping("/register/seller")
    ResponseEntity sellerRegistration(
            @Valid @RequestBody SellerRegistrationDto sellerRegistrationDto) {
        return sellerService.createSeller(sellerRegistrationDto);
    }

    @Operation(summary = "Customer Registration Confirmation", description = "The mail sent to the customer after registration contains this url along with his activation token and when customer hits the url his account gets activated. Validation timme for this token is 3 hours.", method = "PUT")
    @PutMapping("/register/confirm")
    ResponseEntity confirmRegistration(@RequestParam("token") String activationToken) {
        return customerService.validateRegistrationToken(activationToken);
    }

    @Operation(summary = "Resend Account Activation Mail", description = "If a customer forgets to activate his account and 3 hours have passed since registration, then the old token has gotten expired. So he needs a new token to activate his account. So by using this url he gets a new Account activation mail which contains a new token. Validation time for this token is also 3 hours", method = "POST")
    @PostMapping("/register/resend-activation-mail")
    ResponseEntity resendCustomerActivationMail(
            @Valid @RequestBody ForgetPasswordDto forgetPasswordDto) {
        return customerService.resendActivationMail(forgetPasswordDto);
    }

    @Operation(summary = "Forget Password", description = "If a user has forgotten his password and he can't login then he needs to access this url and enter his email and then hit this api. After this a mail is sent to the user which contains a url alongside a new token to update his password. Validation time for this token is just 15 minutes.", method = "POST")
    @PostMapping("/register/forgot-password")
    ResponseEntity forgotPassword(@Valid @RequestBody ForgetPasswordDto forgetPasswordDto) {
        return customerService.forgotPassword(forgetPasswordDto);
    }

    @Operation(summary = "Change Password", description = "When the user hits the forgot password url a mail is sent to the user which contains change password email.User needs to enter his new password and then his password gets updated.", method = "PUT")
    @PutMapping("/register/change-password")
    ResponseEntity ResetPassword(@RequestParam("token") String token,
                                 @RequestBody UpdatePasswordDto updatePasswordDto) {
        return customerService.validateForgotPassword(token, updatePasswordDto);
    }

    @Operation(summary = "User Logout", description = "When user logs out, his activation token gets expired and he needs to login again to generate a new token to access APIs again. ", method = "GET")
    @GetMapping("/user-logout")
    public String logout(@RequestHeader(value = "Authorization") String authHeader) {
        if (authHeader != null) {
            String tokenValue = authHeader.replace("Bearer", "").trim();
            OAuth2AccessToken accessToken = tokenStore.readAccessToken(tokenValue);
            tokenStore.removeAccessToken(accessToken);
        }
        return "Logged out successfully";
    }
}
