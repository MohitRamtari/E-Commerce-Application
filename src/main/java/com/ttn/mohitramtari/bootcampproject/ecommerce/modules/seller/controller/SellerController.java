package com.ttn.mohitramtari.bootcampproject.ecommerce.modules.seller.controller;

import com.ttn.mohitramtari.bootcampproject.ecommerce.modules.seller.dto.SellerProfileUpdateDto;
import com.ttn.mohitramtari.bootcampproject.ecommerce.modules.seller.service.impl.SellerServiceImpl;
import com.ttn.mohitramtari.bootcampproject.ecommerce.modules.user.address.dto.AddressDto;
import com.ttn.mohitramtari.bootcampproject.ecommerce.modules.user.dto.UserPasswordUpdateDto;
import com.ttn.mohitramtari.bootcampproject.ecommerce.modules.user.imagestorage.service.ImageStorageService;
import io.swagger.v3.oas.annotations.Operation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.security.Principal;

@RestController
public class SellerController {

    @Autowired
    SellerServiceImpl sellerService;

    @Autowired
    ImageStorageService imageStorageService;

    @Operation(summary = "View Profile", description = "Logged in user can view his profile details. To do that all he needs his access token. And with the help of the access token his email is retrieved and passed into the method to get his details.")
    @GetMapping("/seller/view-profile")
    ResponseEntity getSellerDetails(Principal principal) throws IOException {
        return sellerService.getSellerByEmail(principal.getName());
    }

    @Operation(summary = "Update Profile", description = "Logged in seller can update his name and his company contact number and his company name", method = "PATCH")
    @PatchMapping("/seller/update-profile")
    ResponseEntity updateSellerDetails(Principal principal,
                                       @Valid @RequestBody SellerProfileUpdateDto sellerProfileUpdateDto)
            throws InvocationTargetException, IllegalAccessException {
        return sellerService.updateSellerProfileDetails(principal.getName(), sellerProfileUpdateDto);
    }

    @Operation(summary = "Seller Update Password", description = "Seller can update his password.All he needs to do is just enter his current password, new password and confirm password. New password and confirm password must be same and current password must match with the old password", method = "PATCH")
    @PatchMapping("/seller/update-password")
    ResponseEntity updateSellerPassword(Principal principal,
                                        @Valid @RequestBody UserPasswordUpdateDto userPasswordUpdateDto) {
        return sellerService.updateSellerPassword(principal.getName(), userPasswordUpdateDto);
    }

    @Operation(summary = "Seller Update Address", description = "Seller can update his address by providing all those fields that he wants to change", method = "PATCH")
    @PatchMapping("/seller/update-address")
    ResponseEntity updateSellerAddress(Principal principal, @Valid @RequestBody AddressDto addressDto)
            throws InvocationTargetException, IllegalAccessException {
        return sellerService.updateSellerAddress(principal.getName(), addressDto);
    }

}
