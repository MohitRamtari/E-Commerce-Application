package com.ttn.mohitramtari.bootcampproject.ecommerce.modules.customer.controller;

import com.ttn.mohitramtari.bootcampproject.ecommerce.modules.customer.dto.CustomerDetailsDto;
import com.ttn.mohitramtari.bootcampproject.ecommerce.modules.customer.dto.CustomerProfileUpdateDto;
import com.ttn.mohitramtari.bootcampproject.ecommerce.modules.customer.service.impl.CustomerServiceImpl;
import com.ttn.mohitramtari.bootcampproject.ecommerce.modules.user.address.dto.AddressDto;
import com.ttn.mohitramtari.bootcampproject.ecommerce.modules.user.dto.UserPasswordUpdateDto;
import io.swagger.v3.oas.annotations.Operation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.security.Principal;

@RestController
public class CustomerController {

    @Autowired
    CustomerServiceImpl customerService;

    @Operation(summary = "View Customer Profile", description = "A customer can view his profile.All he needs to do is just pass his access token and with the help of that token his email is retrieved and then used to get his details.", method = "GET")
    @GetMapping("/customer/view-profile")
    ResponseEntity<CustomerDetailsDto> getCustomerDetails(Principal principal) throws IOException {
        return customerService.getCustomerByEmail(principal.getName());
    }

    @Operation(summary = "View Customer's addresses", description = "A customer can view all the addresses that he has inserted.", method = "GET")
    @GetMapping("/customer/view-addresses")
    ResponseEntity getAllAddresses(Principal principal) {
        return customerService.getAllAddresses(principal.getName());
    }

    @Operation(summary = "Update customer details", description = "Customer can update his first name, middle name, last name and his contact number. He just needs to enter only those fields that he wants to update.", method = "PATCH")
    @PatchMapping("/customer/update-profile")
    ResponseEntity updateCustomerDetails(Principal principal,
                                         @Valid @RequestBody CustomerProfileUpdateDto customerProfileUpdateDto)
            throws InvocationTargetException, IllegalAccessException {
        return customerService.updateCustomerProfileDetails(principal.getName(),
                customerProfileUpdateDto);
    }

    @Operation(summary = "Customer Update Password", description = "Customer can update his password. All he needs to do is enter his current password and then new password and again enter the new password to confirm it. Current password must match his existing password.", method = "PATCH")
    @PatchMapping("/customer/update-password")
    ResponseEntity updateCustomerPassword(Principal principal,
                                          @Valid @RequestBody UserPasswordUpdateDto userPasswordUpdateDto) {
        return customerService.updateCustomerPassword(principal.getName(), userPasswordUpdateDto);
    }

    @Operation(summary = "Customer adds address", description = "Customer adds an address. He can add as many addresses as he wants.")
    @PostMapping("/customer/addAddress")
    ResponseEntity addCustomerAddress(Principal principal, @Valid @RequestBody AddressDto addressDto) {
        return customerService.addCustomerAddress(principal.getName(), addressDto);
    }

    @Operation(summary = "Delete Customer Address", description = "Customer can delete the address by just providing the id of the address")
    @DeleteMapping("/customer/delete-address/{id}")
    ResponseEntity deleteCustomerAddress(Principal principal, @PathVariable Long id) {
        return customerService.deleteCustomerAddress(principal.getName(), id);
    }

    @Operation(summary = "Update Customer Address", description = "To update the address customer needs to provide address id and the fields that he has provided must not be empty and there should be at least one field given to update address")
    @PatchMapping("/customer/update-address/{id}")
    ResponseEntity updateCustomerAddress(Principal principal, @PathVariable Long id,
                                         @Valid @RequestBody AddressDto addressDto)
            throws InvocationTargetException, IllegalAccessException {
        return customerService.updateCustomerAddress(principal.getName(), id, addressDto);
    }
}