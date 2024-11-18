package com.ttn.mohitramtari.bootcampproject.ecommerce.modules.customer.service;

import com.ttn.mohitramtari.bootcampproject.ecommerce.modules.customer.dto.CustomerDetailsDto;
import com.ttn.mohitramtari.bootcampproject.ecommerce.modules.customer.dto.CustomerProfileUpdateDto;
import com.ttn.mohitramtari.bootcampproject.ecommerce.modules.customer.dto.CustomerRegistrationDto;
import com.ttn.mohitramtari.bootcampproject.ecommerce.modules.customer.model.Customer;
import com.ttn.mohitramtari.bootcampproject.ecommerce.modules.user.address.dto.AddressDto;
import com.ttn.mohitramtari.bootcampproject.ecommerce.modules.user.dto.ForgetPasswordDto;
import com.ttn.mohitramtari.bootcampproject.ecommerce.modules.user.dto.UpdatePasswordDto;
import com.ttn.mohitramtari.bootcampproject.ecommerce.modules.user.dto.UserPasswordUpdateDto;
import org.springframework.http.ResponseEntity;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;

public interface CustomerService {

    ResponseEntity<String> createCustomer(CustomerRegistrationDto customerRegistrationDto);

    ResponseEntity<String> validateRegistrationToken(String activationToken);

    ResponseEntity<CustomerDetailsDto> getCustomerByEmail(String email) throws IOException;

    ResponseEntity<String> forgotPassword(ForgetPasswordDto forgetPasswordDto);

    ResponseEntity<String> validateForgotPassword(String token, UpdatePasswordDto updatePasswordDto);

    ResponseEntity<String> resendActivationMail(ForgetPasswordDto forgetPasswordDto);

    void sendCustomerActivationMail(Customer customer);

    ResponseEntity updateCustomerProfileDetails(String name,
                                                CustomerProfileUpdateDto customerProfileUpdateDto)
            throws InvocationTargetException, IllegalAccessException;

    ResponseEntity updateCustomerPassword(String email, UserPasswordUpdateDto userPasswordUpdateDto);

    ResponseEntity addCustomerAddress(String email, AddressDto addressDto);

    ResponseEntity deleteCustomerAddress(String email, Long id);

    ResponseEntity updateCustomerAddress(String email, Long id, AddressDto addressDto)
            throws InvocationTargetException, IllegalAccessException;

    ResponseEntity getAllAddresses(String email);
}
