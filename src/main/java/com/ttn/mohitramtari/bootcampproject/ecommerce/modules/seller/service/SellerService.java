package com.ttn.mohitramtari.bootcampproject.ecommerce.modules.seller.service;

import com.ttn.mohitramtari.bootcampproject.ecommerce.modules.seller.dto.SellerDetailsDto;
import com.ttn.mohitramtari.bootcampproject.ecommerce.modules.seller.dto.SellerProfileUpdateDto;
import com.ttn.mohitramtari.bootcampproject.ecommerce.modules.seller.dto.SellerRegistrationDto;
import com.ttn.mohitramtari.bootcampproject.ecommerce.modules.user.address.dto.AddressDto;
import com.ttn.mohitramtari.bootcampproject.ecommerce.modules.user.dto.UserPasswordUpdateDto;
import org.springframework.http.ResponseEntity;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;

public interface SellerService {

    ResponseEntity<String> createSeller(SellerRegistrationDto sellerRegistrationDto);

    ResponseEntity<SellerDetailsDto> getSellerByEmail(String email) throws IOException;

    ResponseEntity updateSellerProfileDetails(String email,
                                              SellerProfileUpdateDto sellerProfileUpdateDto)
            throws InvocationTargetException, IllegalAccessException;

    ResponseEntity updateSellerPassword(String email, UserPasswordUpdateDto userPasswordUpdateDto);

    ResponseEntity updateSellerAddress(String email, AddressDto addressDto)
            throws InvocationTargetException, IllegalAccessException;
}
